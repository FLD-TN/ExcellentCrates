package su.nightexpress.excellentcrates.opening.selectable;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.api.event.CrateObtainRewardEvent;
import su.nightexpress.excellentcrates.crate.impl.CrateSource;
import su.nightexpress.excellentcrates.crate.impl.Reward;
import su.nightexpress.excellentcrates.key.CrateKey;
import su.nightexpress.excellentcrates.opening.AbstractOpening;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Port of ExcellentCrates 6.3.0 SelectableOpening: the player picks rewards in a GUI instead of gambling.
 * Keys/costs are taken on start like any opening, and refunded if the GUI is closed before confirming.
 */
public class SelectableOpening extends AbstractOpening {

    private final SelectableProvider provider;
    private final SelectableMenu     menu;
    private final Set<Reward>        selectedRewards;

    private boolean confirmed;
    private boolean completed;
    private boolean menuOpened;

    public SelectableOpening(@NotNull CratesPlugin plugin,
                             @NotNull SelectableProvider provider,
                             @NotNull SelectableMenu menu,
                             @NotNull Player player,
                             @NotNull CrateSource source,
                             @Nullable CrateKey key) {
        super(plugin, player, source, key);
        this.provider = provider;
        this.menu = menu;
        this.selectedRewards = new LinkedHashSet<>();
    }

    @Override
    public long getInterval() {
        return 1L;
    }

    @NotNull
    public List<Reward> getCrateRewards() {
        return this.crate.getRewards(this.player);
    }

    public int getRequiredAmount() {
        return Math.min(this.getCrateRewards().size(), this.provider.getSelectionAmount());
    }

    public int getSelectedAmount() {
        return this.selectedRewards.size();
    }

    @NotNull
    public Set<Reward> getSelectedRewards() {
        return this.selectedRewards;
    }

    public void addSelectedReward(@NotNull Reward reward) {
        this.selectedRewards.add(reward);
    }

    public void removeSelectedReward(@NotNull Reward reward) {
        this.selectedRewards.remove(reward);
    }

    public boolean isSelectedReward(@NotNull Reward reward) {
        return this.selectedRewards.contains(reward);
    }

    public boolean isAllRewardsSelected() {
        return this.getRequiredAmount() > 0 && this.getSelectedAmount() == this.getRequiredAmount();
    }

    public boolean giveSelectedRewards() {
        if (!this.isAllRewardsSelected()) return false;

        this.setRefundable(false);
        this.setHasRewardAttempts(true);

        this.selectedRewards.forEach(reward -> {
            reward.give(this.player);
            this.plugin.getPluginManager().callEvent(new CrateObtainRewardEvent(reward, this.player));
        });
        this.selectedRewards.clear();
        this.completed = true; // Use explicit variable to ensure all checks and validations are passed.
        return true;
    }

    public void confirm() {
        this.confirmed = true;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onTick() {
        super.onTick();
        if (!this.isRunning()) return;

        if (this.confirmed) {
            this.confirmed = this.giveSelectedRewards(); // Try give rewards next tick, cancel confirmation if could not.
            return;
        }

        // Open menu on tick, not on start, because we don't need it for instant (mass) openings.
        if (!this.menuOpened) {
            this.menuOpened = true;
            if (!this.menu.open(this.player, this)) {
                this.stop();
            }
        }
    }

    @Override
    protected void finalizeStop() {
        super.finalizeStop();

        if (this.menu.isViewer(this.player)) {
            this.plugin.runTask(task -> this.player.closeInventory()); // Let the GUI handle close event properly.
        }
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public void instaRoll() {
        // Upstream: instant openings pick random rewards.
        List<Reward> rewards = new ArrayList<>(this.getCrateRewards());
        while (!this.isAllRewardsSelected() && !rewards.isEmpty()) {
            this.selectedRewards.add(rewards.remove(Rnd.get(rewards.size())));
        }

        this.giveSelectedRewards();
        this.stop();
    }

    @NotNull
    public SelectableProvider getProvider() {
        return this.provider;
    }

    @NotNull
    public SelectableMenu getMenu() {
        return this.menu;
    }
}
