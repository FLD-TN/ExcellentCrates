package su.nightexpress.excellentcrates.opening;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.api.event.CrateObtainRewardEvent;
import su.nightexpress.excellentcrates.api.opening.Opening;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.impl.Reward;
import su.nightexpress.excellentcrates.api.opening.Spinner;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.CrateSource;
import su.nightexpress.excellentcrates.data.impl.CrateData;
import su.nightexpress.excellentcrates.data.impl.CrateUser;
import su.nightexpress.excellentcrates.key.CrateKey;
import su.nightexpress.excellentcrates.opening.spinner.AnimationSpinner;
import su.nightexpress.excellentcrates.opening.spinner.RewardSpinner;
import su.nightexpress.nightcore.util.Players;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractOpening extends Runnable implements Opening {

    protected final CratesPlugin         plugin;
    protected final Map<String, Spinner> spinnerMap;
    protected final Player               player;
    protected final CrateSource          source;
    protected final Crate                crate;
    protected final CrateKey             key;

    protected boolean refundable;
    protected boolean hasRewardAttempts;
    protected boolean costPaid;
    protected boolean keyTaken;
    protected boolean itemTaken;
    protected boolean refunded;
    protected int     rewardsGiven;

    public AbstractOpening(@NotNull CratesPlugin plugin, @NotNull Player player,
                           @NotNull CrateSource source, @Nullable CrateKey key) {
        this.plugin = plugin;
        this.player = player;
        this.source = source;
        this.crate = source.getCrate();
        this.key = key;
        this.spinnerMap = new LinkedHashMap<>();
        this.setRefundable(true);
    }

    @Nullable
    public Spinner getSpinner(@NotNull String id) {
        return this.spinnerMap.get(id.toLowerCase());
    }

    public void addSpinner(@NotNull Spinner spinner, @NotNull String name/*, int delay*/) {
        this.spinnerMap.put(name.toLowerCase(), spinner);
        //spinner.setStartDelay(delay);
        spinner.run();
    }

    public void stopAnimation(@NotNull String name) {
        Spinner spinner = this.getSpinner(name);
        if (!(spinner instanceof AnimationSpinner)) return;

        this.stopSpinner(spinner);
    }

    public void stopReward(@NotNull String name) {
        Spinner spinner = this.getSpinner(name);
        if (!(spinner instanceof RewardSpinner)) return;

        this.stopSpinner(spinner);
    }

    private void stopSpinner(@NotNull Spinner spinner) {
        if (spinner.isCompleted() || !spinner.isRunning()) return;

        spinner.stop();
    }

    @Override
    protected void onTick() {
        if (!this.isValidPlayer()) {
            this.stop();
            return;
        }
        this.getSpinners().forEach(Spinner::tick);
    }

    /**
     * Gives a reward rolled earlier in this opening. Limits are counted on give, so a reward rolled at the
     * start of an animation (or picked in a menu) may be out of stock by now: it's swapped for another one
     * the player can still win (same rarity first). BoxPvP fix, upstream (6.6.1, 7.x) gives it anyway.
     */
    public void giveReward(@NotNull Reward reward) {
        // A refunded opening is over: the key/item/cost are back, nothing else may come out of it.
        if (this.refunded) return;

        Reward given = reward;
        if (!reward.canWin(this.player)) {
            given = this.rollReplacement();
            if (given == null) {
                Lang.CRATE_OPEN_REWARD_SOLD_OUT.getMessage().replace(reward.replacePlaceholders()).send(this.player);
                // Refund only if this opening gave nothing yet: an opening with several rewards (hacking_x4,
                // selective_3) that already paid out one of them must not hand the key back as well.
                if (this.rewardsGiven == 0) {
                    this.refund();
                }
                return;
            }
            Lang.CRATE_OPEN_REWARD_REPLACED.getMessage()
                .replace(Placeholders.GENERIC_NAME, reward.getName())
                .replace(given.replacePlaceholders())
                .send(this.player);
        }

        given.give(this.player);
        this.rewardsGiven++;
        this.plugin.getPluginManager().callEvent(new CrateObtainRewardEvent(given, this.player));
    }

    @Nullable
    private Reward rollReplacement() {
        return this.crate.tryRollReward(this.player);
    }

    /**
     * Gives back only what was really taken for this opening (key, crate item, cost). An instant opening runs
     * inside OpeningManager#startOpening, before CrateManager takes anything: then nothing is given back here
     * and CrateManager sees {@link #isRefunded()} and takes nothing either.
     */
    protected void refund() {
        if (this.refunded) return;
        this.refunded = true;

        if (this.key != null && this.keyTaken) {
            this.plugin.getKeyManager().giveKey(this.player, this.key, 1);
        }
        if (this.itemTaken) {
            Players.addItem(this.player, this.crate.getItem());
        }
        // Port of 6.4.0: refund only the cost that was actually paid (not for cost bypass players).
        if (this.costPaid) {
            this.crate.getOpenCostMap().forEach((currency, amount) -> {
                currency.getHandler().give(this.player, amount);
            });
        }
    }

    @Override
    protected final void onStop() {
        // Never refund an opening that already gave a reward (e.g. emergency stop right after a selection was given).
        if (this.isRefundable() && this.rewardsGiven == 0) {
            if ((this.isEmergency() && !this.hasRewardRolled()) || !this.hasRewardAttempts()) {
                this.refund();
            }
        }

        // A refunded opening didn't happen: no opening count, milestone or cooldown for it.
        if (this.isCompleted() && !this.refunded) {
            this.crate.setLastOpener(player.getName());

            CrateUser user = this.plugin.getUserManager().getUserData(player);
            CrateData data = user.getCrateData(this.crate);

            data.addOpenings(1);

            if (crate.hasOpenCooldown() && !crate.hasCooldownBypassPermission(player)) {
                // Upstream 5.3.3 stored the raw seconds as the end date (= year 1970), so timed cooldowns never
                // worked. setCooldown turns seconds into an end timestamp (-1 stays one-time / permanent).
                data.setCooldown(crate.getOpenCooldown());
            }

            if (crate.hasMilestones()) {
                data.addMilestones(1);
                crate.triggerMilestones(player, data.getMilestone());
                if (data.getMilestone() >= crate.getMaxMilestone() && crate.isMilestonesRepeatable()) {
                    data.setMilestone(0);
                }
            }

            this.plugin.getUserManager().scheduleSave(user);
        }

        this.getSpinners().forEach(Spinner::stop);
        this.finalizeStop();
    }

    protected void finalizeStop() {
        this.removeOpening();
    }

    public void removeOpening() {
        this.plugin.getOpeningManager().removeOpening(this.getPlayer());
    }

    public boolean isValidPlayer() {
        return this.getPlayer().isOnline();
    }

    public boolean hasRewardRolled() {
        return this.getSpinners().stream().anyMatch(spinner -> spinner instanceof RewardSpinner && spinner.isCompleted());
    }

    @Override
    public boolean isRefundable() {
        return refundable;
    }

    @Override
    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public boolean isCostPaid() {
        return this.costPaid;
    }

    public void setCostPaid(boolean costPaid) {
        this.costPaid = costPaid;
    }

    public void setKeyTaken(boolean keyTaken) {
        this.keyTaken = keyTaken;
    }

    public void setItemTaken(boolean itemTaken) {
        this.itemTaken = itemTaken;
    }

    public boolean isRefunded() {
        return this.refunded;
    }

    public int getRewardsGiven() {
        return this.rewardsGiven;
    }

    @Override
    public boolean hasRewardAttempts() {
        return hasRewardAttempts;
    }

    @Override
    public void setHasRewardAttempts(boolean hasRewardAttempts) {
        this.hasRewardAttempts = hasRewardAttempts;
    }

    @Override
    @NotNull
    public Player getPlayer() {
        return player;
    }

    @Override
    @NotNull
    public CrateSource getSource() {
        return source;
    }

    @Override
    @NotNull
    public Crate getCrate() {
        return crate;
    }

    @Override
    @Nullable
    public CrateKey getKey() {
        return key;
    }

    @Override
    @NotNull
    public Collection<Spinner> getSpinners() {
        return this.spinnerMap.values();
    }
}
