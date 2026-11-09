package su.nightexpress.excellentcrates.hooks.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.Milestone;
import su.nightexpress.excellentcrates.crate.impl.Reward;
import su.nightexpress.excellentcrates.data.impl.CrateData;
import su.nightexpress.excellentcrates.data.impl.CrateUser;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.TimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class PlaceholderHook {

    private static Expansion expansion;

    public static void setup(@NotNull CratesPlugin plugin) {
        if (expansion == null) {
            expansion = new Expansion(plugin);
            expansion.register();
        }
    }

    public static void handleQuit(@NotNull Player player) {
        if (expansion != null) {
            expansion.keyCache.remove(player.getUniqueId());
            expansion.refreshQueued.remove(player.getUniqueId());
        }
    }

    public static void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    static class Expansion extends PlaceholderExpansion {

        private final CratesPlugin                                   plugin;
        private final Map<String, BiFunction<Player, Crate, String>> placeholders;

        /** Key counts computed on the main thread, per player and crate ID, served to async callers (TAB, boards...). */
        private final Map<UUID, Map<String, Integer>> keyCache;
        private final Set<UUID>                       refreshQueued;

        public Expansion(@NotNull CratesPlugin plugin) {
            this.plugin = plugin;
            this.placeholders = new LinkedHashMap<>();
            this.keyCache = new ConcurrentHashMap<>();
            this.refreshQueued = ConcurrentHashMap.newKeySet();

            this.placeholders.put("keys", (player, crate) -> NumberUtil.format(this.getKeys(player, crate)));

            this.placeholders.put("openings", (player, crate) -> {
                CrateUser user = plugin.getUserManager().getUserData(player);
                return NumberUtil.format(user.getCrateData(crate).getOpenings());
            });

            this.placeholders.put("cooldown", (player, crate) -> {
                CrateUser user = plugin.getUserManager().getUserData(player);
                CrateData data = user.getCrateData(crate);
                if (!data.hasCooldown()) return Config.CRATE_COOLDOWN_FORMATTER_READY.get();

                LocalDateTime time = TimeUtil.getLocalDateTimeOf(data.getOpenCooldown());
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(now, time);

                return Config.CRATE_COOLDOWN_FORMATTER_TIME.get()
                    .replace("hh", String.valueOf(duration.toHours()))
                    .replace("mm", String.valueOf(duration.toMinutesPart()))
                    .replace("ss", String.valueOf(duration.toSecondsPart()))
                    ;
            });

            this.placeholders.put("next_milestone_openings", (player, crate) -> {
                CrateUser user = plugin.getUserManager().getUserData(player);
                int milestones = user.getCrateData(crate).getMilestone();
                Milestone milestone = crate.getNextMilestone(milestones);
                return milestone == null ? "-" : NumberUtil.format(milestone.getOpenings() - milestones);
            });

            this.placeholders.put("next_milestone_reward", (player, crate) -> {
                CrateUser user = plugin.getUserManager().getUserData(player);
                int milestones = user.getCrateData(crate).getMilestone();
                Milestone milestone = crate.getNextMilestone(milestones);
                Reward reward = milestone == null ? null : milestone.getReward();

                return reward == null ? "-" : reward.getName();
            });
        }

        /**
         * Physical keys are counted by reading the player's inventory, which is not safe off the main thread
         * (PlaceholderAPI is often called async). Async calls get the last count made on the main thread and
         * queue a recount for the next tick.
         */
        private int getKeys(@NotNull Player player, @NotNull Crate crate) {
            UUID playerId = player.getUniqueId();

            if (Bukkit.isPrimaryThread() || crate.isAllVirtualKeys()) {
                int keys = this.plugin.getKeyManager().getKeysAmount(player, crate);
                if (player.isOnline()) {
                    this.keyCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>()).put(crate.getId(), keys);
                }
                return keys;
            }

            if (!player.isOnline()) return 0;

            Map<String, Integer> counts = this.keyCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
            Integer cached = counts.putIfAbsent(crate.getId(), 0); // Remember the crate so the recount includes it.

            if (this.refreshQueued.add(playerId)) {
                this.plugin.runTask(task -> {
                    this.refreshQueued.remove(playerId);
                    Map<String, Integer> playerCounts = this.keyCache.get(playerId);
                    if (playerCounts == null || !player.isOnline()) {
                        this.keyCache.remove(playerId);
                        return;
                    }
                    playerCounts.keySet().forEach(crateId -> {
                        Crate target = this.plugin.getCrateManager().getCrateById(crateId);
                        if (target == null) playerCounts.remove(crateId);
                        else playerCounts.put(crateId, this.plugin.getKeyManager().getKeysAmount(player, target));
                    });
                });
            }

            return cached == null ? 0 : cached;
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return this.plugin.getName().toLowerCase();
        }

        @Override
        @NotNull
        public String getAuthor() {
            return this.plugin.getDescription().getAuthors().get(0);
        }

        @Override
        @NotNull
        public String getVersion() {
            return this.plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
            if (player == null) return null;

            for (var entry : this.placeholders.entrySet()) {
                String prefix = entry.getKey() + "_";
                if (!params.startsWith(prefix)) continue;

                String id = params.substring(prefix.length());
                Crate crate = plugin.getCrateManager().getCrateById(id);
                if (crate == null) return null;

                return entry.getValue().apply(player, crate);
            }

            return null;
        }
    }
}
