package su.nightexpress.excellentcrates.crate.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Ui;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.CrateSource;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.ItemOptions;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.excellentcrates.Placeholders.*;

/**
 * Port of ExcellentCrates 7.0 BatchAmountSelectionMenu (6.4.0 OpeningAmountMenu): choose how many crates to open.
 * One button per amount from 1 to the max. amount the player can open, nothing is taken until a button is clicked.
 */
public class OpeningAmountMenu extends ConfigMenu<CratesPlugin> implements Linked<OpeningAmountMenu.Data> {

    public static final String FILE_NAME = "opening_amount.yml";

    public record Data(@NotNull CrateSource source, int maxAmount) {}

    private final ViewLink<Data> link;

    private int[]        amountSlots;
    private boolean      useCrateItem;
    private ItemStack    amountItem;
    private String       amountName;
    private List<String> amountLore;

    public OpeningAmountMenu(@NotNull CratesPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENUS, FILE_NAME));
        this.link = new ViewLink<>();
        this.load();
    }

    @Override
    @NotNull
    public ViewLink<Data> getLink() {
        return this.link;
    }

    public void open(@NotNull Player player, @NotNull CrateSource source, int maxAmount) {
        this.open(player, new Data(source, maxAmount));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        Player player = viewer.getPlayer();
        Data data = this.getLink(player);
        CrateSource source = data.source();
        Crate crate = source.getCrate();

        options.editTitle(crate.replacePlaceholders());

        int shown = Math.min(data.maxAmount(), this.amountSlots.length);
        for (int index = 0; index < shown; index++) {
            int amount = index + 1;

            ItemStack item = this.useCrateItem ? crate.getRawItem() : new ItemStack(this.amountItem);
            item.setAmount(Math.min(amount, item.getMaxStackSize()));
            ItemReplacer.create(item).trimmed().hideFlags()
                .setDisplayName(this.amountName)
                .setLore(this.amountLore)
                .replace(GENERIC_AMOUNT, String.valueOf(amount))
                .replace(crate.getPlaceholders())
                .writeMeta();

            MenuItem menuItem = new MenuItem(item).setSlots(this.amountSlots[index]).setPriority(Integer.MAX_VALUE);
            menuItem.setOptions(ItemOptions.personalWeak(player));
            menuItem.setHandler(ItemHandler.forClick((viewer1, event) -> {
                this.runNextTick(() -> {
                    player.closeInventory();
                    this.plugin.getCrateManager().batchOpen(player, source, amount, amount > 1);
                });
            }));
            this.addItem(menuItem);
        }
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(Ui.color("#3A3A3A", "Mở bao nhiêu " + CRATE_NAME + "?"), MenuSize.CHEST_27);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemUtil.editMeta(border, meta -> meta.setDisplayName(" "));
        list.add(new MenuItem(border).setSlots(IntStream.range(0, 27).toArray()).setPriority(0));

        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemUtil.editMeta(background, meta -> meta.setDisplayName(" "));
        list.add(new MenuItem(background).setSlots(2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24).setPriority(1));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.amountSlots = ConfigValue.create("Amount.Slots",
            new int[]{2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24},
            "Các ô nút số lượng: ô thứ 1 = mở 1, ô thứ 2 = mở 2, ...",
            "Chỉ hiện đến số lượng tối đa người chơi mở được."
        ).read(cfg);

        this.useCrateItem = ConfigValue.create("Amount.Use_Crate_Item", true,
            "true: nút là item của crate. false: dùng 'Amount.Item'."
        ).read(cfg);

        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemUtil.editMeta(item, meta -> meta.addItemFlags(ItemFlag.values()));
        this.amountItem = ConfigValue.create("Amount.Item", item).read(cfg);

        this.amountName = ConfigValue.create("Amount.Name",
            Ui.name(Ui.C_AMOUNT, "Mở x" + GENERIC_AMOUNT)
        ).read(cfg);

        this.amountLore = ConfigValue.create("Amount.Lore", Lists.newList(
            Ui.desc("Mở " + GENERIC_AMOUNT + " " + CRATE_NAME + " cùng lúc."),
            "",
            Ui.section("Lưu ý"),
            Ui.note("Mở từ 2 trở lên sẽ bỏ qua"),
            Ui.note("hiệu ứng mở crate."),
            "",
            Ui.action("Chuột trái để mở")
        )).read(cfg);
    }
}
