package su.nightexpress.excellentcrates.hooks.impl;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.crate.CrateManager;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.util.ClickType;
import su.nightexpress.excellentcrates.util.CrateUtils;
import su.nightexpress.excellentcrates.util.InteractType;
import su.nightexpress.nightcore.dialog.Dialog;
import su.nightexpress.nightcore.manager.AbstractListener;

/**
 * Port of ExcellentCrates 7.0 ItemsAdderBlockController for 5.3.3:
 * crates placed as ItemsAdder custom blocks or furniture.
 * Custom blocks are real blocks, so their clicks go through the vanilla CrateListener; here we only
 * stop ItemsAdder from breaking/using them. Furniture is an entity, so its clicks are handled here.
 */
public class ItemsAdderCrateListener extends AbstractListener<CratesPlugin> {

    private final CrateManager crateManager;

    public ItemsAdderCrateListener(@NotNull CratesPlugin plugin, @NotNull CrateManager crateManager) {
        super(plugin);
        this.crateManager = crateManager;
    }

    // Custom blocks

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockInteract(CustomBlockInteractEvent event) {
        if (this.crateManager.getCrateByBlock(event.getBlockClicked()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(CustomBlockBreakEvent event) {
        if (this.crateManager.getCrateByBlock(event.getBlock()) != null) {
            event.setCancelled(true);
        }
    }

    // Furniture

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBukkitEntity().getLocation();

        // Crate assign mode from the editor (Placement -> Locations).
        Crate assignCrate = Dialog.get(player) == null ? null : CrateUtils.getAssignBlockCrate(player);
        if (assignCrate != null) {
            event.setCancelled(true);
            if (this.crateManager.getCrateByLocation(location) == null) {
                assignCrate.addFurniturePosition(location);
                assignCrate.updateHologram();
                assignCrate.saveSettings();
            }
            Dialog.stop(player);
            return;
        }

        Crate crate = this.crateManager.getCrateByLocation(location);
        if (crate == null) return;

        event.setCancelled(true);
        this.interact(player, crate, location, player.isSneaking() ? ClickType.SHIFT_RIGHT : ClickType.RIGHT);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        Location location = event.getBukkitEntity().getLocation();
        Crate crate = this.crateManager.getCrateByLocation(location);
        if (crate == null) return;

        // Upstream: a left click on crate furniture is a crate interaction, never a removal.
        event.setCancelled(true);

        Player player = event.getPlayer();
        this.interact(player, crate, location, player.isSneaking() ? ClickType.SHIFT_LEFT : ClickType.LEFT);
    }

    private void interact(@NotNull Player player, @NotNull Crate crate, @NotNull Location location, @NotNull ClickType clickType) {
        InteractType action = Config.getCrateClickAction(clickType);
        if (action == null) return;

        if (action == InteractType.CRATE_PREVIEW) {
            if (this.crateManager.hasPreviewCooldown(player)) return;
            this.crateManager.setPreviewCooldown(player);
        }

        this.crateManager.interactCrate(player, crate, action, null, location.getBlock());
    }
}
