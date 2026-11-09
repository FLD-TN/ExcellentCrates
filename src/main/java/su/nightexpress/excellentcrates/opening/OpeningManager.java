package su.nightexpress.excellentcrates.opening;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.api.opening.Opening;
import su.nightexpress.excellentcrates.api.opening.OpeningProvider;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.CrateSource;
import su.nightexpress.excellentcrates.key.CrateKey;
import su.nightexpress.excellentcrates.opening.impl.BasicOpening;
import su.nightexpress.excellentcrates.opening.inventory.InventoryOpening;
import su.nightexpress.excellentcrates.opening.inventory.InventoryOpeningMenu;
import su.nightexpress.excellentcrates.opening.selectable.SelectableProvider;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.FileUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OpeningManager extends AbstractManager<CratesPlugin> {

    private final Map<String, OpeningProvider> providerMap;
    private final Map<UUID, Opening>           openingsMap;

    public OpeningManager(@NotNull CratesPlugin plugin) {
        super(plugin);
        this.providerMap = new HashMap<>();
        this.openingsMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.extractResources(Config.DIR_OPENINGS);
        this.loadInventoryProviders();

        this.addListener(new OpeningListener(this.plugin, this));

        this.addTask(this.plugin.createTask(this::tickOpenings).setTicksInterval(1L));
    }

    @Override
    protected void onShutdown() {
        this.getOpenings().forEach(Opening::emergencyStop);
    }

    private void loadInventoryProviders() {
        for (FileConfig config : FileConfig.loadAll(plugin.getDataFolder() + Config.DIR_OPENINGS)) {
            InventoryOpeningMenu menu = new InventoryOpeningMenu(plugin, config);
            String id = FileConfig.getName(config.getFile());
            this.loadProvider(id, menu);
        }
        this.loadSelectableProviders();
        this.plugin.info("Loaded " + this.providerMap.size() + " crate openings.");
    }

    // Port of 6.3.0: selectable openings from /openings/selectable/, defaults 'selective_1' and 'selective_3'.
    private void loadSelectableProviders() {
        File dir = new File(plugin.getDataFolder() + Config.DIR_OPENINGS_SELECTABLE);
        if (!dir.exists() && dir.mkdirs()) {
            FileUtil.create(new File(dir, "selective_1.yml"));
            FileUtil.create(new File(dir, "selective_3.yml"));
        }

        for (FileConfig config : FileConfig.loadAll(dir.getAbsolutePath())) {
            String id = FileConfig.getName(config.getFile());
            int defaultAmount = id.equalsIgnoreCase("selective_3") ? 3 : 1;
            this.loadProvider(id, new SelectableProvider(this.plugin, config, defaultAmount));
            config.saveChanges();
        }
    }

    public void loadProvider(@NotNull String id, @NotNull OpeningProvider provider) {
        this.providerMap.put(id.toLowerCase(), provider);
    }

    @NotNull
    public Map<String, OpeningProvider> getProviderMap() {
        return providerMap;
    }

    @NotNull
    public Map<UUID, Opening> getOpeningsMap() {
        return Collections.unmodifiableMap(this.openingsMap);
    }

    @NotNull
    public Set<Opening> getOpenings() {
        return new HashSet<>(this.openingsMap.values());
    }

    @Nullable
    public OpeningProvider getOpeningProvider(@NotNull String id) {
        return this.getProviderMap().get(id.toLowerCase());
    }

    @Nullable
    public Opening getOpening(@NotNull Player player) {
        return this.openingsMap.get(player.getUniqueId());
    }


    public void tickOpenings() {
        // One broken opening must not stop the others (an exception used to abort this loop every tick)
        // nor stay in the map forever (the player could never open a crate or nightcore menu again).
        for (Opening opening : this.getOpenings()) {
            try {
                opening.tick();
            }
            catch (Throwable throwable) {
                this.plugin.error("Crate opening of '" + opening.getPlayer().getName() + "' (" + opening.getCrate().getId() + ") failed and was stopped:");
                throwable.printStackTrace();
                this.discardOpening(opening);
            }
        }
    }

    /**
     * Stops a failed opening (refunds it if it gave nothing, like an emergency stop) and makes sure it leaves the map.
     */
    private void discardOpening(@NotNull Opening opening) {
        try {
            opening.emergencyStop();
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        this.openingsMap.remove(opening.getPlayer().getUniqueId(), opening);
    }

    public boolean isOpening(@NotNull Player player) {
        return this.getOpening(player) != null;
    }

    public void stopOpening(@NotNull Player player) {
        Opening opening = this.removeOpening(player);
        if (opening == null) return;

        opening.stop();
    }

    @Nullable
    public Opening removeOpening(@NotNull Player player) {
        return this.openingsMap.remove(player.getUniqueId());
    }

    @NotNull
    public Opening createOpening(@NotNull Player player, @NotNull CrateSource source, @Nullable CrateKey key) {
        Crate crate = source.getCrate();
        String openingName = crate.getOpeningConfig();

        OpeningProvider provider = openingName == null ? null : this.getOpeningProvider(openingName);
        if (provider != null) {
            return provider.createOpening(player, source, key);
        }

//        if (openingName != null) {
//            InventoryOpeningMenu menu = this.getInventoryOpening(openingName);
//            if (menu != null) {
//                InventoryOpeningConfig config = menu.getOpeningConfig();
//                return new InventoryOpening(plugin, menu, config, player, source, key);
//            }
//        }

        return new BasicOpening(plugin, player, source, key);
    }

    public boolean startOpening(@NotNull Player player, @NotNull Opening opening, boolean instaRoll) {
        if (this.isOpening(player)) return false;

        InventoryOpening inventoryOpening = opening instanceof InventoryOpening io ? io : null;

        // Must be in the map before the menu opens: OpeningListener only lets this opening's own menu open.
        this.openingsMap.put(player.getUniqueId(), opening);

        if (inventoryOpening != null) {
            if (!instaRoll) {
                InventoryOpeningMenu menu = inventoryOpening.getMenu();
                if (menu.isViewer(player) || !menu.open(player) || player.getOpenInventory().getType() == InventoryType.CRAFTING) {
                    // Nothing was taken yet (CrateManager takes after this returns true): just forget the opening,
                    // otherwise the player stays 'opening' until relog and every nightcore menu is blocked.
                    this.openingsMap.remove(player.getUniqueId(), opening);
                    menu.close(player);
                    return false;
                }
            }
        }

        try {
            if (inventoryOpening != null) {
                InventoryOpeningMenu menu = inventoryOpening.getMenu();
                Inventory inventory;

                if (!instaRoll) {
                    inventory = player.getOpenInventory().getTopInventory();
                }
                else {
                    inventory = menu.getOptions().createInventory();
                }

                inventoryOpening.setInventory(inventory);
                inventoryOpening.postOpen();

                if (instaRoll) opening.instaRoll();
            }
            else {
                opening.run();
                if (instaRoll) {
                    opening.instaRoll();
                }
            }
        }
        catch (Throwable throwable) {
            this.plugin.error("Could not start crate opening of '" + player.getName() + "' (" + opening.getCrate().getId() + "):");
            throwable.printStackTrace();
            this.discardOpening(opening);

            // Nothing is taken yet. If a reward already came out (instant opening failed half way),
            // let CrateManager take the key/cost for it; otherwise nothing was opened.
            return opening instanceof AbstractOpening abstractOpening && abstractOpening.getRewardsGiven() > 0 && !abstractOpening.isRefunded();
        }

        return true;
    }
}
