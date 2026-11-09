package su.nightexpress.excellentcrates.config;

import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.nightcore.language.entry.LangItem;

import static su.nightexpress.excellentcrates.Placeholders.*;
import static su.nightexpress.excellentcrates.config.Ui.*;

/**
 * Editor buttons, BoxPvP style (BoxCore standard): plain names in a role colour, short lines,
 * "Thông tin" / "Lưu ý" sections, action lines last.
 */
public class EditorLang {

    private static final String PREFIX = "Editor.Item.";

    private static final String DELETE = "Shift + Chuột phải để xóa (không hoàn tác)";

    public static final LangItem CUSTOM_ITEMS_BY_ID = LangItem.of(PREFIX + "CustomItemsById",
        name(C_ITEM, "Lưu item ItemsAdder theo ID"),
        desc("Item ItemsAdder được lưu theo ID,"),
        desc("sửa trong ItemsAdder là tự cập nhật."),
        "",
        section("Thông tin"),
        row("Theo ID", Placeholders.CUSTOM_ITEMS_BY_ID),
        row("Nguồn item", ITEM_SOURCE),
        "",
        section("Lưu ý"),
        note("Tắt thì lưu bản NBT cố định,"),
        note("không đổi theo ItemsAdder nữa."),
        "",
        action("Chuột trái để bật / tắt")
    );

    public static final LangItem CRATES_EDITOR = LangItem.of(PREFIX + "Crates",
        name(C_CRATE, "Danh sách crate"),
        desc("Tạo và chỉnh sửa các crate."),
        "",
        action("Chuột trái để mở")
    );

    public static final LangItem KEYS_EDITOR = LangItem.of(PREFIX + "Keys",
        name(C_KEY, "Danh sách key"),
        desc("Tạo và chỉnh sửa các key."),
        "",
        action("Chuột trái để mở")
    );

    public static final LangItem CRATE_OBJECT = LangItem.of(PREFIX + "Crate.Object",
        CRATE_NAME + " " + color(DARK, "[") + color(VALUE, CRATE_ID) + color(DARK, "]"),
        CRATE_INSPECT,
        "",
        action("Chuột trái để chỉnh sửa"),
        actionBad(DELETE)
    );

    public static final LangItem CRATE_CREATE = LangItem.of(PREFIX + "Crate.Create",
        name(OK, "Tạo crate mới"),
        desc("Nhập ID (chữ không dấu, số)"),
        desc("cho crate mới."),
        "",
        action("Chuột trái để tạo")
    );

    public static final LangItem CRATE_DISPLAY_NAME = LangItem.of(PREFIX + "Crate.DisplayName",
        name(C_CRATE, "Tên hiển thị"),
        desc("Tên crate trong tin nhắn,"),
        desc("placeholder và hologram."),
        "",
        section("Thông tin"),
        row("Hiện tại", CRATE_NAME),
        "",
        action("Chuột trái để đổi")
    );

    public static final LangItem CRATE_PERMISSION_REQUIREMENT = LangItem.of(PREFIX + "Crate.Permission",
        name(C_CRATE, "Yêu cầu quyền"),
        desc("Bật thì người chơi cần quyền"),
        desc("mới mở được crate này."),
        "",
        section("Thông tin"),
        row("Bật", CRATE_PERMISSION_REQUIRED),
        row("Quyền", CRATE_PERMISSION),
        "",
        action("Chuột trái để bật / tắt")
    );

    public static final LangItem CRATE_OPEN_COOLDOWN = LangItem.of(PREFIX + "Crate.OpenCooldown",
        name(C_CRATE, "Thời gian chờ mở"),
        desc("Thời gian chờ giữa 2 lần mở"),
        desc("của mỗi người chơi."),
        "",
        section("Thông tin"),
        row("Hiện tại", Placeholders.CRATE_OPEN_COOLDOWN),
        "",
        action("Chuột trái để đặt (giây)"),
        actionBad("Chuột phải để tắt"),
        actionAlt("Phím Q để chỉ cho mở 1 lần")
    );

    public static final LangItem CRATE_PREVIEW_AND_OPENING = LangItem.of(PREFIX + "Crate.PreviewAndOpening",
        name(C_CRATE, "Xem trước & Kiểu mở"),
        desc("Menu xem trước (" + Config.DIR_PREVIEWS + ")"),
        desc("và kiểu mở crate."),
        "",
        section("Thông tin"),
        row("Xem trước", CRATE_PREVIEW_CONFIG),
        row("Kiểu mở", CRATE_ANIMATION_CONFIG),
        "",
        section("Lưu ý"),
        note("Kiểu mở: hiệu ứng trong " + Config.DIR_OPENINGS),
        note("hoặc selective_1 / selective_3"),
        note("để người chơi tự chọn quà."),
        "",
        action("Chuột trái để đổi xem trước"),
        action("Chuột phải để đổi kiểu mở"),
        actionBad("Shift + Chuột trái để tắt xem trước"),
        actionBad("Shift + Chuột phải để tắt hiệu ứng")
    );

    public static final LangItem CRATE_KEY_REQUIREMENT = LangItem.of(PREFIX + "Crate.KeyRequirement",
        name(C_KEY, "Key yêu cầu"),
        desc("Cần 1 trong các key này"),
        desc("để mở crate."),
        "",
        section("Thông tin"),
        row("Cần key", CRATE_KEY_REQUIRED),
        row("Danh sách", ""),
        CRATE_KEYS,
        "",
        action("Chuột trái để thêm key"),
        actionBad("Chuột phải để xóa hết"),
        actionAlt("Phím Q để bật / tắt yêu cầu")
    );

    public static final LangItem CRATE_OPEN_COST = LangItem.of(PREFIX + "Crate.OpenCost",
        name(C_CRATE, "Phí mở"),
        desc("Tiền người chơi phải trả"),
        desc("cho mỗi lần mở."),
        "",
        section("Thông tin"),
        row("Hiện tại", Placeholders.CRATE_OPEN_COST),
        "",
        section("Lưu ý"),
        note("Nhập: <loại tiền> <số tiền>"),
        "",
        action("Chuột trái để thêm phí"),
        actionBad("Phím Q để xóa hết")
    );

    public static final LangItem CRATE_ITEM = LangItem.of(PREFIX + "Crate.InventoryItem",
        name(C_CRATE, "Item của crate"),
        desc("Item đại diện cho crate"),
        desc("trong menu và túi đồ."),
        "",
        section("Lưu ý"),
        note("Item ItemsAdder được lưu theo"),
        note("ID (xem nút màu tím)."),
        "",
        action("Kéo thả item vào để thay"),
        action("Chuột trái để lấy item crate"),
        actionAlt("Chuột phải để lấy bản gốc")
    );

    public static final LangItem CRATE_PLACEMENT_INFO = LangItem.of(PREFIX + "Crate.Placement.Info",
        name(C_PLACE, "Vị trí đặt"),
        desc("Block, furniture, hologram"),
        desc("và hiệu ứng của crate."),
        "",
        action("Chuột trái để mở")
    );

    public static final LangItem CRATE_LOCATIONS = LangItem.of(PREFIX + "Crate.Placement.Locations",
        name(C_PLACE, "Block đã gắn"),
        desc("Click block / furniture đã gắn"),
        desc("để mở hoặc xem trước crate."),
        "",
        section("Danh sách"),
        Placeholders.CRATE_LOCATIONS,
        "",
        section("Lưu ý"),
        note("Nhìn vào block rồi dùng"),
        note("/crate setblock để gắn,"),
        note("/crate removeblock để gỡ."),
        "",
        action("Chuột trái để gắn block mới"),
        actionBad("Chuột phải để xóa hết")
    );

    public static final LangItem CRATE_PUSHBACK = LangItem.of(PREFIX + "Crate.Placement.Pushback",
        name(C_PLACE, "Đẩy lùi"),
        desc("Đẩy người chơi ra nếu không"),
        desc("đủ điều kiện mở crate."),
        "",
        section("Thông tin"),
        row("Trạng thái", CRATE_PUSHBACK_ENABLED),
        "",
        action("Chuột trái để bật / tắt")
    );

    public static final LangItem CRATE_HOLOGRAM = LangItem.of(PREFIX + "Crate.Placement.Hologram",
        name(C_PLACE, "Hologram"),
        desc("Chữ nổi phía trên crate,"),
        desc("lấy theo mẫu trong config.yml."),
        "",
        section("Thông tin"),
        row("Trạng thái", CRATE_HOLOGRAM_ENABLED),
        row("Mẫu", CRATE_HOLOGRAM_TEMPLATE),
        row("Độ cao", CRATE_HOLOGRAM_Y_OFFSET),
        "",
        action("Chuột trái để bật / tắt"),
        action("Chuột phải để đổi mẫu"),
        actionAlt("Shift + Chuột trái để đổi độ cao")
    );

    public static final LangItem CRATE_EFFECTS = LangItem.of(PREFIX + "Crate.Placement.Effects",
        name(C_EFFECT, "Hiệu ứng hạt"),
        desc("Hiệu ứng hạt quanh crate."),
        "",
        section("Thông tin"),
        row("Kiểu", CRATE_EFFECT_MODEL),
        row("Hạt", CRATE_EFFECT_PARTICLE_NAME),
        row("Dữ liệu", CRATE_EFFECT_PARTICLE_DATA),
        "",
        actionAlt("Phím Q để đổi kiểu"),
        action("Chuột trái để đổi hạt"),
        action("Chuột phải để đổi dữ liệu")
    );

    public static final LangItem CRATE_PARTICLE_DATA_RED      = particleData("Red", "Màu đỏ");
    public static final LangItem CRATE_PARTICLE_DATA_GREEN    = particleData("Green", "Màu xanh lá");
    public static final LangItem CRATE_PARTICLE_DATA_BLUE     = particleData("Blue", "Màu xanh dương");
    public static final LangItem CRATE_PARTICLE_DATA_SIZE     = particleData("Size", "Kích thước");
    public static final LangItem CRATE_PARTICLE_DATA_NUMBER   = particleData("Number", "Giá trị số");

    public static final LangItem CRATE_PARTICLE_DATA_MATERIAL = LangItem.of(PREFIX + "Crate.Particle.Data.Material",
        name(C_EFFECT, "Vật liệu"),
        row("Hiện tại", GENERIC_VALUE),
        "",
        action("Kéo thả item vào để đổi")
    );

    public static final LangItem CRATE_REWARDS = LangItem.of(PREFIX + "Crate.Rewards",
        name(C_REWARD, "Phần thưởng"),
        desc("Tất cả phần thưởng của crate,"),
        desc("dùng cho cả lần mở và mốc."),
        "",
        section("Thông tin"),
        row("Số phần thưởng", CRATE_REWARDS_AMOUNT),
        "",
        action("Chuột trái để mở")
    );

    public static final LangItem CRATE_MILESTONES = LangItem.of(PREFIX + "Crate.Milestones",
        name(C_MILE, "Mốc mở crate"),
        desc("Thưởng thêm khi người chơi"),
        desc("mở crate đủ số lần."),
        "",
        section("Thông tin"),
        row("Số mốc", CRATE_MILESTONES_AMOUNT),
        row("Lặp lại", CRATE_MILESTONES_REPEATABLE),
        "",
        action("Chuột trái để mở"),
        actionAlt("Chuột phải để bật / tắt lặp lại")
    );

    public static final LangItem REWARD_OBJECT = LangItem.of(PREFIX + "Reward.Object",
        REWARD_NAME + " " + color(DARK, "[") + color(VALUE, REWARD_ID) + color(DARK, "]"),
        REWARD_INSPECT_CONTENT,
        "",
        section("Thông tin"),
        row("Trọng số", Placeholders.REWARD_WEIGHT),
        row("Tỉ lệ ra", REWARD_ROLL_CHANCE + "%"),
        "",
        action("Chuột trái để chỉnh sửa"),
        actionAlt("Shift + Chuột trái để dời lên"),
        actionAlt("Shift + Chuột phải để dời xuống"),
        actionBad("Phím Q để xóa (không hoàn tác)")
    );

    public static final LangItem REWARD_CREATE = LangItem.of(PREFIX + "Reward.Create",
        name(OK, "Tạo phần thưởng mới"),
        desc("Có 2 cách tạo phần thưởng."),
        "",
        section("Cách 1"),
        note("Cầm item trên con trỏ rồi click:"),
        note("tạo phần thưởng có item đó."),
        "",
        section("Cách 2"),
        note("Click khi tay không: tạo"),
        note("phần thưởng trống (dùng lệnh)."),
        "",
        action("Chuột trái để tạo")
    );

    public static final LangItem REWARD_SORT = LangItem.of(PREFIX + "Reward.Sort.Info",
        name(C_REWARD, "Sắp xếp phần thưởng"),
        desc("Tự động sắp xếp phần thưởng"),
        desc("theo thứ tự đã chọn."),
        "",
        action("Chuột trái để mở")
    );

    public static final LangItem REWARD_SORT_BY_WEIGHT = sortItem("ByWeight", "Theo trọng số");
    public static final LangItem REWARD_SORT_BY_RARITY = sortItem("ByRarity", "Theo độ hiếm");
    public static final LangItem REWARD_SORT_BY_CHANCE = sortItem("ByChance", "Theo tỉ lệ ra");
    public static final LangItem REWARD_SORT_BY_NAME   = sortItem("ByName", "Theo tên");
    public static final LangItem REWARD_SORT_BY_ITEM   = sortItem("ByItem", "Theo loại item");

    public static final LangItem REWARD_DISPLAY_NAME = LangItem.of(PREFIX + "Reward.DisplayName",
        name(C_REWARD, "Tên hiển thị"),
        desc("Tên phần thưởng trong tin"),
        desc("nhắn và placeholder."),
        "",
        section("Thông tin"),
        row("Hiện tại", REWARD_NAME),
        "",
        action("Chuột trái để đổi"),
        actionAlt("Shift + Chuột trái để lấy tên icon"),
        actionAlt("Shift + Chuột phải để gán cho icon")
    );

    public static final LangItem REWARD_PREVIEW = LangItem.of(PREFIX + "Reward.Preview",
        name(C_REWARD, "Icon phần thưởng"),
        desc("Item đại diện phần thưởng"),
        desc("trong menu xem trước."),
        "",
        section("Lưu ý"),
        note("Nên đặt icon riêng nếu phần"),
        note("thưởng có 0 hoặc nhiều item."),
        "",
        action("Kéo thả item vào để thay"),
        actionAlt("Chuột phải để lấy bản sao")
    );

    public static final LangItem REWARD_SET_PLACEHOLDERS = LangItem.of(PREFIX + "Reward.SetPlaceholders",
        name(C_REWARD, "Áp dụng placeholder"),
        desc("Thay placeholder trong tên và"),
        desc("lore của item khi trúng."),
        "",
        section("Thông tin"),
        row("Bật", REWARD_PLACEHOLDER_APPLY),
        "",
        section("Lưu ý"),
        note("Có thể làm hỏng item của"),
        note("plugin khác (ItemsAdder...)."),
        "",
        action("Chuột trái để bật / tắt")
    );

    public static final LangItem REWARD_WEIGHT = LangItem.of(PREFIX + "Reward.Weight",
        name(C_REWARD, "Trọng số"),
        desc("Trọng số càng cao thì"),
        desc("tỉ lệ ra càng lớn."),
        "",
        section("Thông tin"),
        row("Trọng số", Placeholders.REWARD_WEIGHT),
        row("Tỉ lệ ra", REWARD_ROLL_CHANCE + "%"),
        "",
        action("Chuột trái để đổi trọng số")
    );

    public static final LangItem REWARD_COMMANDS = LangItem.of(PREFIX + "Reward.Commands",
        name(C_REWARD, "Lệnh khi trúng"),
        desc("Console chạy các lệnh này"),
        desc("khi người chơi trúng."),
        "",
        section("Danh sách"),
        Placeholders.REWARD_EDITOR_COMMANDS,
        "",
        section("Lưu ý"),
        note("Dùng " + PLAYER_NAME + " cho tên người"),
        note("chơi, hỗ trợ PlaceholderAPI."),
        "",
        action("Chuột trái để thêm lệnh"),
        actionBad("Chuột phải để xóa hết")
    );

    public static final LangItem REWARD_ITEMS = LangItem.of(PREFIX + "Reward.Items",
        name(C_REWARD, "Item khi trúng"),
        desc("Các item được thêm vào"),
        desc("túi đồ khi trúng."),
        "",
        section("Danh sách"),
        Placeholders.REWARD_EDITOR_ITEMS,
        "",
        action("Chuột trái để chỉnh sửa")
    );

    public static final LangItem REWARD_BROADCAST = LangItem.of(PREFIX + "Reward.Broadcast",
        name(C_REWARD, "Thông báo toàn server"),
        desc("Thông báo cho cả server khi"),
        desc("có người trúng phần thưởng."),
        "",
        section("Thông tin"),
        row("Bật", Placeholders.REWARD_BROADCAST),
        "",
        action("Chuột trái để bật / tắt")
    );

    public static final LangItem REWARD_GLOBAL_LIMIT = LangItem.of(PREFIX + "Reward.Limit.Global",
        name(C_LIMIT, "Giới hạn toàn server"),
        desc("Số lần trúng và thời gian"),
        desc("chờ, tính chung cả server."),
        "",
        action("Chuột trái để mở")
    );

    public static final LangItem REWARD_PLAYER_LIMIT = LangItem.of(PREFIX + "Reward.Limit.Player",
        name(C_LIMIT, "Giới hạn mỗi người"),
        desc("Số lần trúng và thời gian"),
        desc("chờ, tính riêng từng người."),
        "",
        action("Chuột trái để mở")
    );

    public static final LangItem REWARD_LIMIT_TOGGLE = LangItem.of(PREFIX + "Reward.Limit.Toggle",
        name(C_LIMIT, "Bật giới hạn"),
        desc("Bật / tắt giới hạn này."),
        "",
        section("Thông tin"),
        row("Hiện tại", LIMIT_ENABLED),
        "",
        action("Chuột trái để bật / tắt")
    );

    public static final LangItem REWARD_LIMIT_AMOUNT = LangItem.of(PREFIX + "Reward.Limit.Amount",
        name(C_LIMIT, "Số lần trúng tối đa"),
        desc("Tổng số lần được trúng,"),
        desc("không reset theo thời gian chờ."),
        "",
        section("Thông tin"),
        row("Hiện tại", LIMIT_AMOUNT),
        "",
        action("Chuột trái để đổi"),
        actionAlt("Chuột phải để không giới hạn")
    );

    public static final LangItem REWARD_LIMIT_COOLDOWN = LangItem.of(PREFIX + "Reward.Limit.Cooldown",
        name(C_LIMIT, "Thời gian chờ"),
        desc("Trúng xong phải chờ hết thời"),
        desc("gian này mới ra lại."),
        "",
        section("Thông tin"),
        row("Hiện tại", LIMIT_COOLDOWN),
        "",
        action("Chuột trái để đặt (giây)"),
        actionAlt("Chuột phải để chờ đến 0h"),
        actionBad("Phím Q để tắt")
    );

    public static final LangItem REWARD_LIMIT_COOLDOWN_STEP = LangItem.of(PREFIX + "Reward.Limit.CooldownStep",
        name(C_LIMIT, "Chờ sau mỗi N lần trúng"),
        desc("Chỉ bắt đầu thời gian chờ"),
        desc("sau mỗi N lần trúng."),
        "",
        section("Thông tin"),
        row("Mỗi", LIMIT_COOLDOWN_STEP + " lần trúng"),
        "",
        action("Chuột trái để đổi")
    );

    public static final LangItem REWARD_LIMIT_RESET = LangItem.of(PREFIX + "Reward.Limit.Reset",
        name(BAD, "Đặt lại"),
        desc("Xóa số lần đã trúng và"),
        desc("thời gian chờ đang lưu."),
        "",
        actionBad("Chuột trái để đặt lại")
    );

    public static final LangItem REWARD_IGNORED_PERMISSIONS = LangItem.of(PREFIX + "Reward.IgnoredPermissions",
        name(C_REWARD, "Quyền bị loại trừ"),
        desc("Người có 1 trong các quyền"),
        desc("này không bao giờ trúng."),
        "",
        section("Danh sách"),
        REWARD_IGNORED_FOR_PERMISSIONS,
        "",
        action("Chuột trái để thêm quyền"),
        actionBad("Chuột phải để xóa hết")
    );

    public static final LangItem MILESTONE_CREATE = LangItem.of(PREFIX + "Milestone.Create",
        name(OK, "Tạo mốc mới"),
        "",
        action("Chuột trái để tạo")
    );

    public static final LangItem MILESTONE_OBJECT = LangItem.of(PREFIX + "Milestone.Object",
        name(C_MILE, "Mốc " + MILESTONE_OPENINGS + " lần mở"),
        MILESTONE_INSPECT_REWARD,
        "",
        section("Thông tin"),
        row("Số lần mở", MILESTONE_OPENINGS),
        row("ID phần thưởng", MILESTONE_REWARD_ID),
        "",
        action("Chuột trái để đổi số lần"),
        action("Chuột phải để đổi phần thưởng"),
        actionBad(DELETE)
    );

    public static final LangItem KEY_OBJECT = LangItem.of(PREFIX + "Key.Object",
        KEY_NAME + " " + color(DARK, "[") + color(VALUE, KEY_ID) + color(DARK, "]"),
        section("Thông tin"),
        row("Key ảo", Placeholders.KEY_VIRTUAL),
        "",
        action("Chuột trái để chỉnh sửa"),
        actionBad(DELETE)
    );

    public static final LangItem KEY_CREATE = LangItem.of(PREFIX + "Key.Create",
        name(OK, "Tạo key mới"),
        desc("Nhập ID (chữ không dấu, số)"),
        desc("cho key mới."),
        "",
        action("Chuột trái để tạo")
    );

    public static final LangItem KEY_DISPLAY_NAME = LangItem.of(PREFIX + "Key.DisplayName",
        name(C_KEY, "Tên hiển thị"),
        desc("Tên key trong tin nhắn,"),
        desc("placeholder và hologram."),
        "",
        section("Thông tin"),
        row("Hiện tại", KEY_NAME),
        "",
        action("Chuột trái để đổi")
    );

    public static final LangItem KEY_ITEM = LangItem.of(PREFIX + "Key.Item",
        name(C_KEY, "Item của key"),
        desc("Item đại diện cho key"),
        desc("trong túi đồ người chơi."),
        "",
        section("Lưu ý"),
        note("Item ItemsAdder được lưu theo"),
        note("ID (xem nút màu tím)."),
        "",
        action("Kéo thả item vào để thay"),
        action("Chuột trái để lấy key"),
        actionAlt("Chuột phải để lấy bản gốc")
    );

    public static final LangItem KEY_VIRTUAL = LangItem.of(PREFIX + "Key.Virtual",
        name(C_KEY, "Key ảo"),
        desc("Key ảo lưu trong dữ liệu,"),
        desc("không phải item trong túi đồ."),
        "",
        section("Thông tin"),
        row("Bật", Placeholders.KEY_VIRTUAL),
        "",
        action("Chuột trái để bật / tắt")
    );

    private static LangItem particleData(String key, String name) {
        return LangItem.of(PREFIX + "Crate.Particle.Data." + key,
            name(C_EFFECT, name),
            row("Hiện tại", GENERIC_VALUE),
            "",
            action("Chuột trái để đổi")
        );
    }

    private static LangItem sortItem(String key, String name) {
        return LangItem.of(PREFIX + "Reward.Sort." + key,
            name(C_REWARD, name),
            "",
            action("Chuột trái để sắp xếp")
        );
    }
}
