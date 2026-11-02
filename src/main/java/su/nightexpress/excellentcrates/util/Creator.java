package su.nightexpress.excellentcrates.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.config.Ui;
import su.nightexpress.excellentcrates.crate.menu.PreviewMenu;
import su.nightexpress.excellentcrates.menu.impl.CratesMenu;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.util.ItemUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.excellentcrates.Placeholders.*;

public class Creator {

    private final CratesPlugin plugin;

    public Creator(@NotNull CratesPlugin plugin) {
        this.plugin = plugin;
    }

    public void createDefaults() {
        File dirPreviews = new File(plugin.getDataFolder().getAbsolutePath(), Config.DIR_PREVIEWS);
        if (!dirPreviews.exists() && dirPreviews.mkdirs()) {
            this.createPreviews();
        }

        File dirMenus = new File(plugin.getDataFolder().getAbsolutePath(), Config.DIR_MENUS);
        if (!dirMenus.exists() && dirMenus.mkdirs()) {
            this.createMenus();
        }

        this.installDefaultPack();
    }

    /**
     * BoxPvP default pack (8 crates + keys + previews, converted from the purchased 4.x config).
     * Installed once (flag 'Default_Pack.Installed' in config.yml): missing files are copied, and crate files
     * that are only an empty shell (no 'Name', e.g. a lone 'Key.Required' line) are replaced.
     * Real crate files are never overwritten, and deleted crates are not re-created after the first install.
     */
    private void installDefaultPack() {
        FileConfig config = this.plugin.getConfig();
        if (config.getBoolean(PACK_FLAG)) return;

        int installed = 0;
        for (String id : DEFAULT_PACK) {
            installed += this.installCrate("crates/" + id + ".yml");
            installed += this.copyIfMissing("keys/" + id + ".yml");
            installed += this.copyIfMissing("previews/" + id + "_preview.yml");
        }
        config.set(PACK_FLAG, true);
        config.saveChanges();
        this.plugin.info("Installed default crate pack (" + installed + " files).");
    }

    private static final String   PACK_FLAG    = "Default_Pack.Installed";
    private static final String[] DEFAULT_PACK = {"common", "uncommon", "rare", "epic", "legendary", "mythic", "glory", "vote"};

    private int installCrate(@NotNull String path) {
        File file = new File(plugin.getDataFolder(), path);
        List<String> positions = new ArrayList<>();
        if (file.exists()) {
            FileConfig old = new FileConfig(file);
            if (!isEmptyShell(old)) return 0;

            positions.addAll(old.getStringList("Block.Positions"));
            File backup = new File(file.getParentFile(), file.getName().replace(".yml", ".empty-backup"));
            if (!file.renameTo(backup)) return 0;
            this.plugin.warn("Replaced empty crate file '" + file.getName() + "' with the default one (old file: " + backup.getName() + ").");
        }

        int copied = this.copyIfMissing(path);
        if (copied > 0 && !positions.isEmpty()) {
            FileConfig fresh = new FileConfig(file);
            fresh.set("Block.Positions", positions);
            fresh.saveChanges();
        }
        return copied;
    }

    /** Crate file without anything useful: no rewards, no preview and no item (e.g. only 'Key.Required'). */
    private static boolean isEmptyShell(@NotNull FileConfig config) {
        if (!config.getSection("Rewards.List").isEmpty()) return false;
        if (config.contains("Preview_Config")) return false;
        String material = config.getString("Item.Material", "AIR");
        return material == null || material.equalsIgnoreCase("AIR");
    }

    private int copyIfMissing(@NotNull String path) {
        File file = new File(plugin.getDataFolder(), path);
        if (file.exists() || this.plugin.getResource(path) == null) return 0;

        this.plugin.saveResource(path, false);
        return 1;
    }

    public void createPreviews() {
        PreviewMenu previewMenu = new PreviewMenu(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_PREVIEWS, Placeholders.DEFAULT + ".yml")) {

            @Override
            @NotNull
            protected List<MenuItem> createDefaultItems() {
                List<MenuItem> list = new ArrayList<>();

                ItemStack blackPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                list.add(new MenuItem(blackPane).setPriority(0).setSlots(1,2,3,5,6,7,9,18,27,17,26,35,37,38,39,40,41,42,43));

                ItemStack grayPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                list.add(new MenuItem(grayPane).setPriority(0).setSlots(0,4,8,36,44));

                ItemStack milestones = ItemUtil.getSkinHead("1daf09284530ce92ed2df2a62e1b05a11f1871f85ae559042844206d66c0b5b0");
                ItemUtil.editMeta(milestones, meta -> {
                    meta.setDisplayName(Ui.name(Ui.C_MILE, "Mốc mở crate"));
                    meta.setLore(List.of(
                        Ui.desc("Thưởng thêm khi mở đủ số lần."),
                        "",
                        Ui.action("Chuột trái để xem")
                    ));
                });
                list.add(new MenuItem(milestones).setPriority(10).setSlots(4).setHandler(this.milesHandler));

                ItemStack exit = ItemUtil.getSkinHead(SKIN_WRONG_MARK);
                ItemUtil.editMeta(exit, meta -> {
                    meta.setDisplayName(Lang.EDITOR_ITEM_CLOSE.getLocalizedName());
                });
                list.add(new MenuItem(exit).setPriority(10).setSlots(40).setHandler(ItemHandler.forClose(this)));

                ItemStack pageNext = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
                ItemUtil.editMeta(pageNext, meta -> {
                    meta.setDisplayName(Lang.EDITOR_ITEM_NEXT_PAGE.getLocalizedName());
                });
                list.add(new MenuItem(pageNext).setPriority(10).setSlots(26).setHandler(ItemHandler.forNextPage(this)));

                ItemStack pageLeft = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
                ItemUtil.editMeta(pageLeft, meta -> {
                    meta.setDisplayName(Lang.EDITOR_ITEM_PREVIOUS_PAGE.getLocalizedName());
                });
                list.add(new MenuItem(pageLeft).setPriority(10).setSlots(18).setHandler(ItemHandler.forPreviousPage(this)));

                return list;
            }

        };

        previewMenu.clear();
    }

    private void createMenus() {
        CratesMenu menu = new CratesMenu(this.plugin, FileConfig.loadOrExtract(this.plugin, Config.DIR_MENUS, Placeholders.DEFAULT + ".yml"));
        menu.clear();
    }
}
