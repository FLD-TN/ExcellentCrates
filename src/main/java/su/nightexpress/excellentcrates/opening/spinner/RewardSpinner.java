package su.nightexpress.excellentcrates.opening.spinner;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.config.Keys;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.Rarity;
import su.nightexpress.excellentcrates.crate.impl.Reward;
import su.nightexpress.excellentcrates.opening.inventory.InventoryOpening;
import su.nightexpress.excellentcrates.opening.inventory.InventorySpinner;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RewardSpinner extends InventorySpinner {

    private final RewardSpinSettings  settings;
    private final Map<Rarity, Double> rarityWeightMap;

    public RewardSpinner(@NotNull CratesPlugin plugin,
                         @NotNull String id,
                         @NotNull RewardSpinSettings settings,
                         @NotNull InventoryOpening opening,
                         @NotNull SpinMode mode, int[] slots) {
        super(plugin, id, opening, mode, slots);
        this.settings = settings;
        this.rarityWeightMap = new HashMap<>();

        Set<Rarity> rarities = new HashSet<>();
        if (this.settings.getRarities().contains(Placeholders.WILDCARD)) {
            rarities.addAll(plugin.getCrateManager().getRarities());
        }
        else {
            plugin.getCrateManager().getRarities().forEach(rarity -> {
                if (this.settings.getRarities().contains(rarity.getId())) {
                    rarities.add(rarity);
                }
            });
        }

        rarities.forEach(rarity -> {
            this.rarityWeightMap.put(rarity, rarity.getWeight());
        });
    }

    @Override
    @NotNull
    public RewardSpinSettings getSettings() {
        return settings;
    }

    @Override
    @NotNull
    public ItemStack createItem() {
        Crate crate = this.opening.getCrate();
        Player player = this.opening.getPlayer();

        // BoxPvP: roll by reward weight; the spin's rarities only filter which rewards can show up.
        List<Reward> rewards = crate.getRewards(player);
        rewards.removeIf(reward -> !this.rarityWeightMap.containsKey(reward.getRarity()));
        if (rewards.isEmpty()) {
            return new ItemStack(Material.AIR);
        }

        Map<Reward, Double> weights = new HashMap<>();
        rewards.forEach(reward -> weights.put(reward, reward.getWeight()));
        Reward reward = Rnd.getByWeight(weights);

        ItemStack item = reward.getPreview();
        PDCUtil.set(item, Keys.rewardId, reward.getId());

        return item;
    }

    @Override
    protected void onStop() {
        if (this.isCompleted()) {
            this.checkRewards();
        }
    }

    private void checkRewards() {
        for (int slot : this.getOpening().getConfig().getWinSlots()) {
            if (!Lists.contains(this.slots, slot)) continue;

            ItemStack item = this.getOpening().getInventory().getItem(slot);
            if (item == null || item.getType().isAir()) continue;

            String rewardId = PDCUtil.getString(item, Keys.rewardId).orElse(null);
            if (rewardId == null) continue;

            Reward reward = this.opening.getCrate().getReward(rewardId);
            if (reward == null) continue;

            PDCUtil.remove(item, Keys.rewardId);

            this.getOpening().giveReward(reward); // Re-checks limits: rolled at spin time, given now.
        }
    }
}
