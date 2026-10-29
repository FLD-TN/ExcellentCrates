package su.nightexpress.excellentcrates.crate;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.api.currency.Currency;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;
import su.nightexpress.excellentcrates.api.opening.Opening;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Keys;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.effect.AbstractEffect;
import su.nightexpress.excellentcrates.crate.effect.EffectModel;
import su.nightexpress.excellentcrates.crate.impl.*;
import su.nightexpress.excellentcrates.crate.listener.CrateListener;
import su.nightexpress.excellentcrates.config.Perms;
import su.nightexpress.excellentcrates.config.Ui;
import su.nightexpress.excellentcrates.crate.menu.MilestonesMenu;
import su.nightexpress.excellentcrates.crate.menu.OpeningAmountMenu;
import su.nightexpress.excellentcrates.crate.menu.PreviewMenu;
import su.nightexpress.excellentcrates.data.impl.CrateData;
import su.nightexpress.excellentcrates.data.impl.CrateUser;
import su.nightexpress.excellentcrates.data.impl.LimitData;
import su.nightexpress.excellentcrates.editor.type.CreationResult;
import su.nightexpress.excellentcrates.hooks.impl.ItemsAdderCrateListener;
import su.nightexpress.excellentcrates.hooks.impl.ItemsAdderHook;
import su.nightexpress.excellentcrates.key.CrateKey;
import su.nightexpress.excellentcrates.opening.AbstractOpening;
import su.nightexpress.excellentcrates.opening.impl.BasicOpening;
import su.nightexpress.excellentcrates.util.CrateUtils;
import su.nightexpress.excellentcrates.util.InteractType;
import su.nightexpress.excellentcrates.util.pos.WorldPos;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.text.tag.Tags;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

import java.io.File;
import java.util.*;

public class CrateManager extends AbstractManager<CratesPlugin> {

    private final Map<String, Rarity>      rarityMap;
    private final Map<String, Crate>       crateMap;
    private final Map<String, PreviewMenu> previewMap;
    private final Map<UUID, Long> previewCooldown;
    private final Map<UUID, Long> openCooldown;
    private final Map<WorldPos, Crate> crateByPosMap;
    private final Map<UUID, Map<String, Integer>> batchRewards;

    private MilestonesMenu    milestonesMenu;
    private OpeningAmountMenu amountMenu;

    public CrateManager(@NotNull CratesPlugin plugin) {
        super(plugin);
        this.rarityMap = new HashMap<>();
        this.crateMap = new HashMap<>();
        this.previewMap = new HashMap<>();
        this.previewCooldown = new HashMap<>();
        this.openCooldown = new HashMap<>();
        this.crateByPosMap = new HashMap<>();
        this.batchRewards = new HashMap<>();
    }

    @Override
    public void onLoad() {
        this.loadRarities();
        this.loadPreviews();
        this.loadCrates();
        this.loadUI();
        this.plugin.runTaskAsync(task -> {
            this.loadRewardLimits();
            this.getCrates().forEach(crate -> crate.setLoaded(true));
        });

        if (this.plugin.hasHolograms()) {
            this.addTask(plugin.createAsyncTask(this::updateCrateHolograms).setSecondsInterval(Config.CRATE_HOLOGRAM_UPDATE_INTERVAL.get()));
        }

        this.addTask(plugin.createAsyncTask(this::playCrateEffects).setTicksInterval(1L));
        this.addTask(plugin.createTask(() -> BasicOpening.tickVisuals(this.plugin)).setSecondsInterval(1));

        this.addListener(new CrateListener(this.plugin, this));
        if (ItemsAdderHook.isAvailable()) {
            this.addListener(new ItemsAdderCrateListener(this.plugin, this));
        }
    }

    private void loadRarities() {
        FileConfig config = this.plugin.getConfig();
        
        if (config.getSection("Rewards.Rarities").isEmpty()) {
            Set<Rarity> rarities = new HashSet<>();

            File oldFile = new File(plugin.getDataFolder(), "rarity.yml");
            if (oldFile.exists()) {
                FileConfig oldConfig = new FileConfig(oldFile);
                for (String id : oldConfig.getSection("")) {
                    rarities.add(Rarity.read(plugin, oldConfig, id, id));
                }
                oldFile.delete();
            }
            if (rarities.isEmpty()) {
                rarities.add(new Rarity(this.plugin, "common", Ui.color("#E8E8E8", "Thường"), 10, true));
                rarities.add(new Rarity(this.plugin, "uncommon", Ui.color("#7FDBFF", "Hiếm"), 5, false));
                rarities.add(new Rarity(this.plugin, "rare", Ui.color("#B39DFF", "Sử thi"), 2, false));
                rarities.add(new Rarity(this.plugin, "legendary", Ui.color("#FFB86B", "Huyền thoại"), 1, false));
            }

            rarities.forEach(rarity -> {
                rarity.write(config, "Rewards.Rarities." + rarity.getId());
            });
        }

        config.getSection("Rewards.Rarities").forEach(rarityId -> {
            Rarity rarity = Rarity.read(this.plugin, config, "Rewards.Rarities." + rarityId, rarityId);
            this.rarityMap.put(rarity.getId(), rarity);
        });


        // Should never happen.
        if (this.rarityMap.isEmpty()) {
            Rarity dummy = Rarity.dummy(this.plugin);
            this.rarityMap.put(dummy.getId(), dummy);

            this.plugin.warn("No rarities defined in the config, adding dummy one...");
            this.plugin.warn("YOU MUST FIX THIS ISSUE BY EDITING OR REGENERATING 'Rarities' SECTION IN THE CONFIG!");
            return;
        }


        this.plugin.info("Loaded " + this.rarityMap.size() + " rarities!");
    }

    private void loadPreviews() {
        for (FileConfig cfg : FileConfig.loadAll(plugin.getDataFolder() + Config.DIR_PREVIEWS, true)) {
            PreviewMenu menu = new PreviewMenu(plugin, cfg);
            String id = cfg.getFile().getName().replace(".yml", "").toLowerCase();
            this.previewMap.put(id, menu);
        }
    }

    private void loadUI() {
        if (Config.isMilestonesEnabled()) {
            this.milestonesMenu = new MilestonesMenu(this.plugin);
        }
        this.amountMenu = new OpeningAmountMenu(this.plugin);
    }

    private void loadCrates() {
        for (File file : FileUtil.getFiles(plugin.getDataFolder() + Config.DIR_CRATES, false)) {
            Crate crate = new Crate(plugin, file);
            this.loadCrate(crate);
        }
        this.plugin.info("Loaded " + this.crateMap.size() + " crates.");
    }

    private void loadCrate(@NotNull Crate crate) {
        if (crate.load()) {
            this.crateMap.put(crate.getId(), crate);
        }
        else {
            this.removeCratePositions(crate);
            this.plugin.error("Crate not loaded: '" + crate.getFile().getName() + "'.");
        }
    }

    public void loadRewardLimits() {
        var map = this.plugin.getData().getRewardLimits();

        boolean purge = Config.DATABASE_PURGE_REWARDS_DATA.get();
        map.entrySet().removeIf(entry -> {
            String crateId = entry.getKey();
            Crate crate = this.getCrateById(crateId);
            if (crate == null) {
                if (purge) this.plugin.getData().deleteRewardLimitData(crateId);
                //this.plugin.debug("Deleted all limit data for invalid '" + crateId + "' crate.");
                return true;
            }

            var limitMap = entry.getValue();
            limitMap.keySet().removeIf(rewardId -> {
                Reward reward = crate.getReward(rewardId);
                if (reward != null) return false;

                if (purge) this.plugin.getData().deleteRewardLimitData(crateId, rewardId);
                //this.plugin.debug("Deleted limit data for invalid '" + rewardId + "' reward of '" + crateId + "' crate.");
                return true;
            });

            return limitMap.isEmpty();
        });
        if (map.isEmpty()) return;

        this.getCrates().forEach(crate -> {
            var limitMap = map.get(crate.getId());
            if (limitMap == null) return;

            crate.getRewards().forEach(reward -> {
                LimitData data = limitMap.get(reward.getId());
                if (data == null) {
                    data = this.createRewardLimit(reward);
                }
                reward.loadGlobalLimit(data);
                //if (data != null) this.plugin.debug("Loaded limit data for '" + reward.getId() + "' reward of '" + crate.getId() + "' crate.");
            });
        });
    }

    @Nullable
    public LimitData createRewardLimit(@NotNull Reward reward) {
        if (!reward.getGlobalLimits().isEnabled()) return null;

        LimitData data = LimitData.create();
        this.plugin.getData().insertRewardLimitData(reward, data);
        //this.plugin.debug("Created fresh limit data for '" + reward.getId() + "' reward of '" + reward.getCrate().getId() + "' crate.");
        return data;
    }

    @Override
    protected void onShutdown() {
        BasicOpening.clearVisuals(this.plugin);
        Arrays.asList(EffectModel.values()).forEach(model -> model.getEffect().reset());

        if (this.milestonesMenu != null) this.milestonesMenu.clear();
        if (this.amountMenu != null) this.amountMenu.clear();
        this.batchRewards.clear();

        this.previewMap.values().forEach(ConfigMenu::clear);
        this.previewMap.clear();

        this.crateMap.values().forEach(crate -> {
            crate.saveLastOpenData();
            crate.clear();
        });
        this.crateMap.clear();
        this.crateByPosMap.clear();
        this.openCooldown.clear();

        this.rarityMap.clear();
    }

    @NotNull
    public Map<String, Rarity> getRarityMap() {
        return rarityMap;
    }

    @NotNull
    public Collection<Rarity> getRarities() {
        return this.getRarityMap().values();
    }

    @Nullable
    public Rarity getRarity(@NotNull String id) {
        return this.getRarityMap().get(id.toLowerCase());
    }

    @NotNull
    public Rarity getDefaultRarity() {
        return this.getRarities().stream().filter(Rarity::isDefault).findFirst().orElse(this.getRarities().stream().findAny().orElseThrow());
    }

    @NotNull
    public Map<String, PreviewMenu> getPreviewMap() {
        return Collections.unmodifiableMap(this.previewMap);
    }

    @Nullable
    public PreviewMenu getPreview(@NotNull Crate crate) {
        String config = crate.getPreviewConfig();
        return config == null ? null : this.getPreview(config);
    }

    @Nullable
    public PreviewMenu getPreview(@NotNull String id) {
        return this.previewMap.get(id.toLowerCase());
    }

    @NotNull
    public Collection<PreviewMenu> getPreviews() {
        return this.getPreviewMap().values();
    }

    @NotNull
    public List<String> getPreviewNames() {
        return new ArrayList<>(this.previewMap.keySet());
    }

    public void openMilestones(@NotNull Player player, @NotNull CrateSource source) {
        if (this.milestonesMenu != null) {
            this.milestonesMenu.open(player, source);
        }
    }

    public boolean isCrate(@NotNull ItemStack item) {
        return this.getCrateByItem(item) != null;
    }

    @NotNull
    public List<String> getCrateIds(boolean keyOnly) {
        return this.getCrates().stream().filter(crate -> crate.isKeyRequired() || !keyOnly).map(Crate::getId).toList();
    }

    @NotNull
    public Map<String, Crate> getCratesMap() {
        return Collections.unmodifiableMap(this.crateMap);
    }

    @NotNull
    public Collection<Crate> getCrates() {
        return this.getCratesMap().values();
    }

    @Nullable
    public Crate getCrateById(@NotNull String id) {
        return this.crateMap.get(id.toLowerCase());
    }

    @Nullable
    public Crate getCrateByItem(@NotNull ItemStack item) {
        String id = PDCUtil.getString(item, Keys.crateId).orElse(null);
        return id != null ? this.getCrateById(id) : null;
    }

    @Nullable
    public Crate getCrateByBlock(@NotNull Block block) {
        return this.getCrateByLocation(block.getLocation());
    }

    @Nullable
    public Crate getCrateByLocation(@NotNull Location location) {
        WorldPos pos = WorldPos.from(location);

        return this.crateByPosMap.get(pos);
    }

    // Port of 6.0.0: positions are mapped to crates, so a block click is one map lookup instead of a scan of all crates.
    public void updateCratePositions(@NotNull Crate crate) {
        this.removeCratePositions(crate);
        crate.getBlockPositions().forEach(pos -> this.crateByPosMap.put(pos, crate));
    }

    public void removeCratePositions(@NotNull Crate crate) {
        this.crateByPosMap.values().removeIf(has -> has == crate);
    }

    public void updateCrateHolograms() {
        this.getCrates().forEach(Crate::updateHologram);
    }

    @NotNull
    public CreationResult create(@NotNull String id) {
        id = CrateUtils.validateId(id);
        if (id.isBlank()) return CreationResult.ERROR_NAME;
        if (this.getCrateById(id) != null) return CreationResult.ERROR_DUPLICATE;

        File file = new File(plugin.getDataFolder() + Config.DIR_CRATES, id + ".yml");
        FileUtil.create(file);

        Crate crate = new Crate(this.plugin, file);
        crate.setName(StringUtil.capitalizeUnderscored(crate.getId()) + " Crate");
        crate.setOpeningConfig(null);
        crate.setPreviewConfig(Placeholders.DEFAULT);

        ItemStack item = ItemUtil.getSkinHead(Placeholders.SKIN_NEW_CRATE);
        ItemUtil.editMeta(item, meta -> {
            meta.setDisplayName(crate.getName());
            meta.addItemFlags(ItemFlag.values());
        });
        crate.setItem(item);

        crate.setPushbackEnabled(true);
        crate.setHologramEnabled(true);
        crate.setHologramTemplate(Placeholders.DEFAULT);
        crate.setEffectModel(EffectModel.HELIX);
        crate.setEffectParticle(UniParticle.of(Particle.CLOUD));
        crate.save();

        this.loadCrate(crate);
        return CreationResult.SUCCESS;
    }

    @NotNull
    public CreationResult createReward(@NotNull Crate crate, @NotNull String id) {
        id = CrateUtils.validateId(id);
        if (id.isBlank()) return CreationResult.ERROR_NAME;
        if (crate.getReward(id) != null) return CreationResult.ERROR_DUPLICATE;

        Reward reward = Reward.createEmpty(plugin, crate, id);
        crate.addReward(reward);

        return CreationResult.SUCCESS;
    }

    public boolean delete(@NotNull Crate crate) {
        if (crate.getFile().delete()) {
            crate.clear();
            this.plugin.runTaskAsync(task -> plugin.getData().deleteRewardLimitData(crate));
            this.crateMap.remove(crate.getId());
            this.removeCratePositions(crate);
            return true;
        }
        return false;
    }

    public boolean spawnCrate(@NotNull Crate crate, @NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;

        world.dropItemNaturally(location, crate.getItem());
        return true;
    }

    public void giveCrate(@NotNull Player player, @NotNull Crate crate, int amount) {
        if (amount < 1) return;

        // Port of 6.0.0 giveCrateItem: no 64 cap, Players.addItem splits stacks (and drops what doesn't fit).
        Players.addItem(player, crate.getItem(), amount);
    }

    public void previewCrate(@NotNull Player player, @NotNull CrateSource source) {
        Crate crate = source.getCrate();
        PreviewMenu menu = this.getPreview(crate);
        if (menu == null) return;

        menu.open(player, source);
    }

    public void interactCrate(@NotNull Player player, @NotNull Crate crate, @NotNull InteractType action, @Nullable ItemStack item, @Nullable Block block) {
        //player.closeInventory();

        CrateSource source = new CrateSource(crate, item, block);

        if (action == InteractType.CRATE_PREVIEW) {
            this.previewCrate(player, source);
            return;
        }

        if (action == InteractType.CRATE_OPEN || action == InteractType.CRATE_MASS_OPEN) {
            // Port of 6.4.0 / 7.0.0: fast open (sneak) opens the max. amount instantly,
            // a normal open shows the amount selector when 2+ crates can be opened at once.
            boolean fastOpen = action == InteractType.CRATE_MASS_OPEN;
            boolean canBatch = player.hasPermission(Perms.MASS_OPEN);
            int maxAmount = canBatch ? this.countMaxOpenings(player, source) : 1;

            if (fastOpen) {
                this.batchOpen(player, source, maxAmount, true);
                return;
            }

            if (maxAmount >= 2 && Config.CRATE_AMOUNT_SELECTOR_ENABLED.get() && this.amountMenu != null) {
                this.amountMenu.open(player, source, maxAmount);
                return;
            }

            this.batchOpen(player, source, 1, false);
        }
    }

    /**
     * Port of 6.4.0 Cost#countMaxOpenings: how many times the player can open this crate right now
     * (keys, crate items, open costs, cooldown), capped by the mass opening limit. At least 1, so a failed
     * open still shows the proper error message.
     */
    public int countMaxOpenings(@NotNull Player player, @NotNull CrateSource source) {
        Crate crate = source.getCrate();
        int max = Math.max(1, Config.CRATE_MASS_OPENING_LIMIT.get());

        ItemStack sourceItem = source.getItem();
        if (sourceItem != null) {
            max = Math.min(max, sourceItem.getAmount());
        }
        if (crate.isKeyRequired()) {
            max = Math.min(max, this.plugin.getKeyManager().getKeysAmount(player, crate));
        }
        if (sourceItem == null && !crate.isKeyRequired()) {
            max = 1; // Free crate block: one at a time, like 5.3.3 mass open.
        }

        if (!crate.hasCostBypassPermisssion(player)) {
            for (var entry : crate.getOpenCostMap().entrySet()) {
                double cost = entry.getValue();
                if (cost <= 0D) continue;

                int affordable = (int) Math.floor(entry.getKey().getHandler().getBalance(player) / cost);
                max = Math.min(max, affordable);
            }
        }

        if (crate.hasOpenCooldown() && !crate.hasCooldownBypassPermission(player)) {
            max = Math.min(max, 1);
        }

        return Math.max(1, max);
    }

    /**
     * Opens the crate several times in a row. With 2+ openings rewards are announced in one summary
     * (6.4.0 merged reward messages of one opening into one message; here: of one batch).
     */
    public void batchOpen(@NotNull Player player, @NotNull CrateSource source, int amount, boolean skipAnimation) {
        Crate crate = source.getCrate();
        Block block = source.getBlock();
        OpenSettings settings = new OpenSettings().setSkipAnimation(skipAnimation);

        boolean summary = amount > 1 && skipAnimation && Config.CRATE_BATCH_SUMMARY_ENABLED.get();
        if (summary) {
            this.batchRewards.put(player.getUniqueId(), new LinkedHashMap<>());
        }

        int opened = 0;
        try {
            for (int spent = 0; spent < amount; spent++) {
                if (!this.openCrate(player, source, settings)) {
                    if (spent == 0 && block != null && crate.isPushbackEnabled()) {
                        player.setVelocity(player.getEyeLocation().getDirection().setY(Config.CRATE_PUSHBACK_Y.get()).multiply(Config.CRATE_PUSHBACK_MULTIPLY.get()));
                    }
                    break;
                }
                opened++;
            }
        }
        finally {
            if (summary) {
                Map<String, Integer> rewards = this.batchRewards.remove(player.getUniqueId());
                if (opened > 0 && rewards != null && !rewards.isEmpty()) {
                    List<String> entries = new ArrayList<>();
                    rewards.forEach((name, count) -> entries.add(Lang.CRATE_OPEN_BATCH_ENTRY.getString()
                        .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(count))
                        .replace(Placeholders.REWARD_NAME, name)));

                    Lang.CRATE_OPEN_BATCH_SUMMARY.getMessage()
                        .replace(crate.replacePlaceholders())
                        .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(opened))
                        .replace(Placeholders.GENERIC_ENTRY, entries)
                        .send(player);
                }
            }
        }
    }

    /**
     * @return true if the reward is collected for a batch summary (so its own message must be skipped).
     */
    public boolean collectBatchReward(@NotNull Player player, @NotNull Reward reward) {
        Map<String, Integer> rewards = this.batchRewards.get(player.getUniqueId());
        if (rewards == null) return false;

        rewards.merge(reward.getName(), 1, Integer::sum);
        return true;
    }

    public boolean openCrate(@NotNull Player player, @NotNull CrateSource source, @NotNull OpenSettings settings) {
        Opening openingData = this.plugin.getOpeningManager().getOpening(player);
        if (openingData != null && !openingData.isCompleted()) {
            return false;
        }

        Crate crate = source.getCrate();
        if (!crate.isLoaded()) {
            return false;
        }

        // Stop mass open (mostly only this case) if crate itemstack is out.
        if (source.getItem() != null && source.getItem().getAmount() <= 0) {
            return false;
        }

        if (!settings.isForce() && !crate.hasPermission(player)) {
            Lang.ERROR_NO_PERMISSION.getMessage(plugin).send(player);
            return false;
        }

        if (!settings.isForce() && player.getInventory().firstEmpty() == -1) {
            Lang.CRATE_OPEN_ERROR_INVENTORY_SPACE.getMessage().replace(crate.replacePlaceholders()).send(player);
            return false;
        }

        CrateKey key = null;
        CrateUser user = plugin.getUserManager().getUserData(player);
        CrateData crateData = user.getCrateData(crate);

        if (!settings.isForce() && crate.hasOpenCooldown() && crateData.hasCooldown()) {
            (crateData.isCooldownPermanent() ? Lang.CRATE_OPEN_ERROR_COOLDOWN_ONE_TIMED : Lang.CRATE_OPEN_ERROR_COOLDOWN_TEMPORARY).getMessage()
                .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(crateData.getOpenCooldown()))
                .replace(crate.replacePlaceholders())
                .send(player);
            return false;
        }

        // Port of 6.1.0: pick the key once (hand > off hand > inventory > virtual) and use it for both take and refund.
        if (!settings.isForce() && crate.isKeyRequired()) {
            key = this.plugin.getKeyManager().getOpenKey(player, crate);
            if (key == null) {
                boolean holdRequired = Config.CRATE_HOLD_KEY_TO_OPEN.get() && !crate.isAllVirtualKeys();
                (holdRequired ? Lang.CRATE_OPEN_ERROR_NO_HOLD_KEY : Lang.CRATE_OPEN_ERROR_NO_KEY).getMessage().replace(crate.replacePlaceholders()).send(player);
                return false;
            }
        }

        if (!settings.isForce() && !crate.hasCostBypassPermisssion(player)) {
            for (var entry : crate.getOpenCostMap().entrySet()) {
                Currency currency = entry.getKey();
                double amount = entry.getValue();
                if (currency.getHandler().getBalance(player) < amount) {
                    Lang.CRATE_OPEN_ERROR_CANT_AFFORD.getMessage()
                        .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                        .replace(crate.replacePlaceholders()).send(player);
                    return false;
                }
            }
        }

        if (crate.getRewards(player).isEmpty()) {
            Lang.CRATE_OPEN_ERROR_NO_REWARDS.getMessage().replace(crate.replacePlaceholders()).send(player);
            return false;
        }

        CrateOpenEvent openEvent = new CrateOpenEvent(crate, player);
        plugin.getPluginManager().callEvent(openEvent);
        if (openEvent.isCancelled()) return false;

        Opening opening = this.plugin.getOpeningManager().createOpening(player, source, key);
        opening.setRefundable(!settings.isForce());

        if (!this.plugin.getOpeningManager().startOpening(player, opening, settings.isSkipAnimation())) {
            return false;
        }

        if (!settings.isForce()) {
            // Take costs (skipped for bypass players, so nothing is refunded to them either).
            if (!crate.hasCostBypassPermisssion(player)) {
                crate.getOpenCostMap().forEach((currency, amount) -> currency.getHandler().take(player, amount));
                if (opening instanceof AbstractOpening abstractOpening) {
                    abstractOpening.setCostPaid(true);
                }
            }

            // Take key
            if (crate.isKeyRequired() && key != null) {
                this.plugin.getKeyManager().takeKey(player, key, 1);
            }

            // Take crate item stack
            ItemStack item = source.getItem();
            if (item != null) {
                item.setAmount(item.getAmount() - 1);
            }
        }

        return true;
    }

    public void setPreviewCooldown(@NotNull Player player) {
        long timestamp = System.currentTimeMillis() + Config.CRATE_PREVIEW_COOLDOWN.get();
        this.previewCooldown.put(player.getUniqueId(), timestamp);
    }

    public long getPreviewCooldown(@NotNull Player player) {
        long timestamp = this.previewCooldown.getOrDefault(player.getUniqueId(), 0L);
        if (System.currentTimeMillis() < timestamp) {
            return timestamp;
        }

        this.removePreviewCooldown(player);
        return 0L;
    }

    public boolean hasPreviewCooldown(@NotNull Player player) {
        return this.getPreviewCooldown(player) > 0L;
    }

    public void removePreviewCooldown(@NotNull Player player) {
        this.previewCooldown.remove(player.getUniqueId());
    }

    // Port of 7.0 InteractCooldownService (one cooldown for every crate interaction); here split per action:
    // preview keeps 'Preview_Cooldown', opening uses 'Open_Click_Cooldown'.
    public void setOpenClickCooldown(@NotNull Player player) {
        long cooldown = Config.CRATE_OPEN_CLICK_COOLDOWN.get();
        if (cooldown <= 0L) return;

        this.openCooldown.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
    }

    public long getOpenClickCooldown(@NotNull Player player) {
        long timestamp = this.openCooldown.getOrDefault(player.getUniqueId(), 0L);
        if (System.currentTimeMillis() < timestamp) {
            return timestamp;
        }

        this.openCooldown.remove(player.getUniqueId());
        return 0L;
    }

    public void removeOpenClickCooldown(@NotNull Player player) {
        this.openCooldown.remove(player.getUniqueId());
    }

    /**
     * World interaction cooldowns (crate blocks / furniture). Not used for GUIs and commands.
     * @return true if the interaction can go on.
     */
    public boolean checkClickCooldown(@NotNull Player player, @NotNull InteractType action) {
        if (action == InteractType.CRATE_PREVIEW) {
            if (this.hasPreviewCooldown(player)) {
                Lang.CRATE_PREVIEW_ERROR_COOLDOWN.getMessage()
                    .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(this.getPreviewCooldown(player)))
                    .send(player);
                return false;
            }
            this.setPreviewCooldown(player);
            return true;
        }

        long openCooldown = this.getOpenClickCooldown(player);
        if (openCooldown > 0L) {
            Lang.CRATE_OPEN_ERROR_INTERACT_COOLDOWN.getMessage()
                .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(openCooldown))
                .send(player);
            return false;
        }
        this.setOpenClickCooldown(player);
        return true;
    }

    public void playCrateEffects() {
        this.getCrates().forEach(crate -> {
            if (crate.getEffectModel() == EffectModel.NONE) return;

            UniParticle particle = crate.getEffectParticle();
            AbstractEffect effect = crate.getEffectModel().getEffect();

            crate.getBlockPositions().forEach(worldPos -> {
                if (!worldPos.isChunkLoaded()) return;

                Location location = worldPos.toLocation();
                if (location == null) return;

                CrateUtils.getPlayersForEffects(location).forEach(player -> {
                    effect.step(LocationUtil.setCenter3D(location), particle, player);
                });
            });
        });

        Arrays.asList(EffectModel.values()).forEach(model -> model.getEffect().addStep());
    }
}
