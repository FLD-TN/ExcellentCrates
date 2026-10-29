package su.nightexpress.excellentcrates.opening;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.api.opening.Opening;
import su.nightexpress.excellentcrates.opening.inventory.InventoryOpening;
import su.nightexpress.excellentcrates.opening.selectable.SelectableOpening;
import su.nightexpress.nightcore.api.event.PlayerOpenMenuEvent;
import su.nightexpress.nightcore.manager.AbstractListener;

public class OpeningListener extends AbstractListener<CratesPlugin> {

    private final OpeningManager openingManager;

    public OpeningListener(@NotNull CratesPlugin plugin, @NotNull OpeningManager openingManager) {
        super(plugin);
        this.openingManager = openingManager;
    }

    /**
     * Port of 7.0 AnimationSessionController#onQuit: leaving mid-animation finishes the opening and still
     * grants the rewards (v7 BaseAnimationInstance#stop always completes). Openings that haven't rolled
     * anything yet (selection not started, reward menu not confirmed) are refunded as before.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Opening opening = this.openingManager.getOpening(player);
        if (opening == null) return;

        boolean rolling;
        if (opening instanceof InventoryOpening inventoryOpening) {
            rolling = inventoryOpening.isStarted();
        }
        else if (opening instanceof SelectableOpening selectableOpening) {
            rolling = selectableOpening.isConfirmed();
        }
        else rolling = true;

        if (rolling && opening.isRunning() && !opening.isCompleted()) {
            opening.instaRoll();
        }
        this.openingManager.stopOpening(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMenuOpen(PlayerOpenMenuEvent event) {
        Player player = event.getPlayer();
        if (!this.openingManager.isOpening(player)) return;

        Opening opening = this.openingManager.getOpening(player);

        if (opening instanceof InventoryOpening inventoryOpening) {
            if (event.getMenu() != inventoryOpening.getMenu()) {
                event.setCancelled(true);
            }
            return;
        }

        if (opening instanceof SelectableOpening selectableOpening) {
            if (event.getMenu() != selectableOpening.getMenu()) {
                event.setCancelled(true);
            }
            return;
        }

        event.setCancelled(true);
    }
}
