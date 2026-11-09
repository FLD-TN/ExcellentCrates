package su.nightexpress.excellentcrates.opening.selectable;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.config.Ui;
import su.nightexpress.excellentcrates.crate.impl.Reward;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.wrapper.UniSound;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.excellentcrates.Placeholders.*;

/**
 * Port of ExcellentCrates 6.3.0 SelectableMenu (reward selection GUI) to the 5.3.3 menu framework.
 */
public class SelectableMenu extends ConfigMenu<CratesPlugin> implements AutoFilled<Reward>, Linked<SelectableOpening> {

    private static final String CURRENT = "%current%";
    private static final String REWARDS = "%rewards%";

    private final ViewLink<SelectableOpening> link;
    private final ItemHandler confirmHandler;

    private int[]        rewardSlots;
    private String       rewardName;
    private List<String> rewardLore;
    private String       rewardEntry;
    private ItemStack    selectedIcon;

    private UniSound selectSound;
    private UniSound unselectSound;
    private UniSound limitSound;
    private UniSound confirmSound;

    public SelectableMenu(@NotNull CratesPlugin plugin, @NotNull FileConfig config) {
        super(plugin, config);
        this.link = new ViewLink<>();

        this.addHandler(this.confirmHandler = new ItemHandler("confirm", (viewer, event) -> {
            SelectableOpening opening = this.getLink(viewer);
            if (opening == null || !opening.isAllRewardsSelected()) return;

            opening.confirm();
            this.confirmSound.play(viewer.getPlayer());
        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            if (menuItem.getHandler() == this.confirmHandler) {
                menuItem.getOptions().setVisibilityPolicy(viewer -> {
                    SelectableOpening opening = this.getLink(viewer);
                    return opening != null && opening.isAllRewardsSelected();
                });
            }

            menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                SelectableOpening opening = this.getLink(viewer);
                if (opening == null) return;

                List<String> entries = opening.getSelectedRewards().stream()
                    .sorted(Comparator.comparing(Reward::getId))
                    .map(reward -> reward.replacePlaceholders().apply(this.rewardEntry))
                    .toList();

                ItemReplacer.create(item).readMeta()
                    .replace(GENERIC_AMOUNT, String.valueOf(opening.getRequiredAmount()))
                    .replace(CURRENT, String.valueOf(opening.getSelectedAmount()))
                    .replace(REWARDS, entries)
                    .replace(opening.getCrate().getPlaceholders())
                    .writeMeta();
            });
        });
    }

    @Override
    @NotNull
    public ViewLink<SelectableOpening> getLink() {
        return this.link;
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        SelectableOpening opening = this.getLink(viewer);

        options.editTitle(str -> opening.getCrate().replacePlaceholders().apply(str
            .replace(GENERIC_AMOUNT, String.valueOf(opening.getRequiredAmount()))
            .replace(CURRENT, String.valueOf(opening.getSelectedAmount()))));

        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Reward> autoFill) {
        Player player = viewer.getPlayer();
        SelectableOpening opening = this.getLink(player);

        autoFill.setSlots(this.rewardSlots);
        autoFill.setItems(opening.getCrateRewards());
        autoFill.setItemCreator(reward -> {
            ItemStack item;
            if (opening.isSelectedReward(reward)) {
                item = new ItemStack(this.selectedIcon);
                ItemReplacer.create(item).readMeta().replace(reward.replacePlaceholders()).writeMeta();
            }
            else {
                item = reward.getPreview();
                ItemReplacer.create(item).trimmed().hideFlags()
                    .setDisplayName(this.rewardName)
                    .setLore(this.rewardLore)
                    .replace(reward.getPlaceholders())
                    .replace(opening.getCrate().getPlaceholders())
                    .writeMeta();
            }
            return item;
        });
        autoFill.setClickAction(reward -> (viewer1, event) -> {
            if (opening.isSelectedReward(reward)) {
                opening.removeSelectedReward(reward);
                this.unselectSound.play(player);
            }
            else {
                if (opening.isAllRewardsSelected()) {
                    this.limitSound.play(player);
                    return;
                }

                opening.addSelectedReward(reward);
                this.selectSound.play(player);
            }
            this.runNextTick(() -> this.flush(viewer));
        });
    }

    @Override
    public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
        SelectableOpening opening = this.getLink(viewer);

        super.onClose(viewer, event);

        // Closed without confirming: stop the opening, keys/costs are refunded.
        if (opening != null && !opening.isCompleted() && opening.isRunning()) {
            opening.stop();
        }
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(Ui.color("#3A3A3A", "Chọn " + GENERIC_AMOUNT + " phần thưởng"), MenuSize.CHEST_54);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemUtil.editMeta(border, meta -> meta.setDisplayName(" "));
        list.add(new MenuItem(border).setSlots(IntStream.range(45, 54).toArray()).setPriority(0));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> meta.setDisplayName(Ui.name(Ui.ALT, "Trang sau")));
        list.add(new MenuItem(nextPage).setSlots(53).setPriority(10).setHandler(ItemHandler.forNextPage(this)));

        ItemStack previousPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(previousPage, meta -> meta.setDisplayName(Ui.name(Ui.ALT, "Trang trước")));
        list.add(new MenuItem(previousPage).setSlots(45).setPriority(10).setHandler(ItemHandler.forPreviousPage(this)));

        ItemStack confirm = new ItemStack(Material.LIME_DYE);
        ItemUtil.editMeta(confirm, meta -> {
            meta.setDisplayName(Ui.name(Ui.OK, "Xác nhận"));
            meta.setLore(Lists.newList(
                Ui.desc("Bạn sẽ nhận các phần thưởng:"),
                REWARDS,
                "",
                Ui.action("Chuột trái để xác nhận")
            ));
            meta.addItemFlags(ItemFlag.values());
        });
        list.add(new MenuItem(confirm).setSlots(49).setPriority(10).setHandler(this.confirmHandler));

        ItemStack notEnough = new ItemStack(Material.GRAY_DYE);
        ItemUtil.editMeta(notEnough, meta -> {
            meta.setDisplayName(Ui.name("#C9C9C9", "Xác nhận", "Chưa đủ"));
            meta.setLore(Lists.newList(
                Ui.desc("Hãy chọn đủ phần thưởng."),
                "",
                Ui.section("Thông tin"),
                Ui.row("Đã chọn", CURRENT + "/" + GENERIC_AMOUNT),
                "",
                Ui.section("Lưu ý"),
                Ui.note("Đóng menu khi chưa xác nhận"),
                Ui.note("sẽ được hoàn lại key."),
                "",
                Ui.actionBad("Chuột trái để thoát")
            ));
            meta.addItemFlags(ItemFlag.values());
        });
        list.add(new MenuItem(notEnough).setSlots(49).setPriority(1).setHandler(ItemHandler.forClose(this)));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.rewardSlots = ConfigValue.create("Reward.Slots",
            new int[] {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34},
            "Các ô hiển thị phần thưởng để chọn."
        ).read(cfg);

        this.rewardName = ConfigValue.create("Reward.Name",
            Ui.name(Ui.C_REWARD, REWARD_NAME)
        ).read(cfg);

        this.rewardLore = ConfigValue.create("Reward.Lore", Lists.newList(
            REWARD_PREVIEW_LORE,
            "",
            Ui.action("Chuột trái để chọn")
        )).read(cfg);

        this.rewardEntry = ConfigValue.create("Reward.Entry",
            Ui.bullet(REWARD_NAME),
            "Dòng hiển thị mỗi phần thưởng đã chọn trong nút Xác nhận."
        ).read(cfg);

        ItemStack selected = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemUtil.editMeta(selected, meta -> {
            meta.setDisplayName(Ui.name(Ui.OK, "Đã chọn", REWARD_NAME));
            meta.setLore(Lists.newList(
                Ui.desc("Bạn sẽ nhận phần thưởng này."),
                "",
                Ui.actionBad("Chuột trái để bỏ chọn")
            ));
            meta.addItemFlags(ItemFlag.values());
        });
        this.selectedIcon = ConfigValue.create("Selection.Icon", selected).read(cfg);

        this.selectSound = ConfigValue.create("Selection.Sound.Select", UniSound.of(Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE)).read(cfg);
        this.unselectSound = ConfigValue.create("Selection.Sound.Unselect", UniSound.of(Sound.BLOCK_GLASS_BREAK)).read(cfg);
        this.limitSound = ConfigValue.create("Selection.Sound.Limit", UniSound.of(Sound.ENTITY_VILLAGER_NO)).read(cfg);
        this.confirmSound = ConfigValue.create("Selection.Sound.Confirm", UniSound.of(Sound.ENTITY_PLAYER_LEVELUP)).read(cfg);
    }
}
