package su.nightexpress.excellentcrates.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.excellentcrates.Placeholders.*;
import static su.nightexpress.excellentcrates.config.Ui.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;

/**
 * Messages, BoxPvP style (BoxCore standard): ✔ success, ✘ error, (!) warning, one line, no trailing full stop.
 */
public class Lang extends CoreLang {

    public static final LangString COMMAND_EDITOR_DESC = LangString.of("Command.Editor.Desc",
        "Mở trình chỉnh sửa crate và key.");


    public static final LangString COMMAND_DROP_USAGE = LangString.of("Command.Drop.Usage",
        "<crate> <x> <y> <z> [world]");

    public static final LangString COMMAND_DROP_DESC = LangString.of("Command.Drop.Desc",
        "Thả crate tại tọa độ chỉ định.");

    public static final LangText COMMAND_DROP_DONE = LangText.of("Command.Drop.Done",
        success("Đã thả " + hl(CRATE_NAME) + " tại " + hl(LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z) + " " + color(DARK, "(" + LOCATION_WORLD + ")")));


    public static final LangString COMMAND_DROP_KEY_USAGE = LangString.of("Command.DropKey.Usage",
        "<key> <x> <y> <z> [world]");

    public static final LangString COMMAND_DROP_KEY_DESC = LangString.of("Command.DropKey.Desc",
        "Thả key tại tọa độ chỉ định.");

    public static final LangText COMMAND_DROP_KEY_DONE = LangText.of("Command.DropKey.Done",
        success("Đã thả " + hl(KEY_NAME) + " tại " + hl(LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z) + " " + color(DARK, "(" + LOCATION_WORLD + ")")));


    public static final LangString COMMAND_OPEN_DESC = LangString.of("Command.Open.Desc",
        "Mở một crate.");

    public static final LangString COMMAND_OPEN_USAGE = LangString.of("Command.Open.Usage",
        "<crate>");


    public static final LangString COMMAND_OPEN_FOR_DESC = LangString.of("Command.OpenFor.Desc",
        "Mở crate cho người chơi.");

    public static final LangString COMMAND_OPEN_FOR_USAGE = LangString.of("Command.OpenFor.Usage",
        "<player> <crate> [-f] [-s]");

    public static final LangText COMMAND_OPEN_FOR_DONE = LangText.of("Command.OpenFor.Done",
        success("Đã mở " + hl(CRATE_NAME) + " cho " + hl(PLAYER_NAME)));

    public static final LangText COMMAND_OPEN_FOR_NOTIFY = LangText.of("Command.OpenFor.Notify",
        success("Bạn được mở " + hl(CRATE_NAME)));


    public static final LangString COMMAND_GIVE_USAGE = LangString.of("Command.Give.Usage",
        "<player> <crate> [amount] [-s]");

    public static final LangString COMMAND_GIVE_DESC = LangString.of("Command.Give.Desc",
        "Tặng crate cho người chơi.");

    public static final LangText COMMAND_GIVE_DONE = LangText.of("Command.Give.Done",
        success("Đã tặng " + color(VALUE, "x" + GENERIC_AMOUNT) + " " + hl(CRATE_NAME) + " cho " + hl(PLAYER_NAME)));

    public static final LangText COMMAND_GIVE_NOTIFY = LangText.of("Command.Give.Notify",
        success("Bạn nhận được " + color(VALUE, "x" + GENERIC_AMOUNT) + " " + hl(CRATE_NAME)));


    public static final LangString COMMAND_KEY_DESC = LangString.of("Command.Key.Desc",
        "Quản lý key của người chơi.");

    public static final LangString COMMAND_KEY_USAGE = LangString.of("Command.Key.Usage",
        "[help]");


    public static final LangString COMMAND_KEY_GIVE_USAGE = LangString.of("Command.Key.Give.Usage",
        "<player> <key> <amount> [-s] [-nosave]");

    public static final LangString COMMAND_KEY_GIVE_DESC = LangString.of("Command.Key.Give.Desc",
        "Tặng key cho người chơi.");

    public static final LangText COMMAND_KEY_GIVE_DONE = LangText.of("Command.Key.Give.Done",
        success("Đã tặng " + color(VALUE, "x" + GENERIC_AMOUNT) + " " + hl(KEY_NAME) + " cho " + hl(PLAYER_NAME)));

    public static final LangText COMMAND_KEY_GIVE_NOTIFY = LangText.of("Command.Key.Give.Notify",
        success("Bạn nhận được " + color(VALUE, "x" + GENERIC_AMOUNT) + " " + hl(KEY_NAME)));


    public static final LangString COMMAND_KEY_GIVE_ALL_USAGE = LangString.of("Command.Key.GiveAll.Usage",
        "<player> <key> <amount> [-s]");

    public static final LangString COMMAND_KEY_GIVE_ALL_DESC = LangString.of("Command.Key.GiveAll.Desc",
        "Tặng key cho tất cả người chơi online.");

    public static final LangText COMMAND_KEY_GIVE_ALL_DONE = LangText.of("Command.Key.GiveAll.Done",
        success("Đã tặng " + color(VALUE, "x" + GENERIC_AMOUNT) + " " + hl(KEY_NAME) + " cho " + hl("tất cả người chơi")));


    public static final LangString COMMAND_KEY_TAKE_USAGE = LangString.of("Command.Key.Take.Usage",
        "<player> <key> <amount> [-s] [-nosave]");

    public static final LangString COMMAND_KEY_TAKE_DESC = LangString.of("Command.Key.Take.Desc",
        "Lấy key của người chơi.");

    public static final LangText COMMAND_KEY_TAKE_DONE = LangText.of("Command.Key.Take.Done",
        success("Đã lấy " + color(VALUE, "x" + GENERIC_AMOUNT) + " " + hl(KEY_NAME) + " của " + hl(PLAYER_NAME)));

    public static final LangText COMMAND_KEY_TAKE_NOTIFY = LangText.of("Command.Key.Take.Notify",
        warn("Bạn bị trừ " + color(VALUE, "x" + GENERIC_AMOUNT) + " " + color(BAD, KEY_NAME)));


    public static final LangString COMMAND_KEY_SET_USAGE = LangString.of("Command.Key.Set.Usage",
        "<player> <key> <amount> [-s] [-nosave]");

    public static final LangString COMMAND_KEY_SET_DESC = LangString.of("Command.Key.Set.Desc",
        "Đặt số key của người chơi.");

    public static final LangText COMMAND_KEY_SET_DONE = LangText.of("Command.Key.Set.Done",
        success("Đã đặt " + hl(KEY_NAME) + " của " + hl(PLAYER_NAME) + " thành " + color(VALUE, "x" + GENERIC_AMOUNT)));

    public static final LangText COMMAND_KEY_SET_NOTIFY = LangText.of("Command.Key.Set.Notify",
        warn("Số " + hl(KEY_NAME) + " của bạn đã được đặt thành " + color(VALUE, "x" + GENERIC_AMOUNT)));


    public static final LangString COMMAND_KEY_INSPECT_DESC = LangString.of("Command.Key.Show.Desc",
        "Xem key ảo [của người chơi].");

    public static final LangString COMMAND_KEY_INSPECT_USAGE = LangString.of("Command.Key.Show.Usage",
        "[player]");

    public static final LangText COMMAND_KEY_INSPECT_LIST = LangText.of("Command.Key.Show.Format.List",
        TAG_NO_PREFIX,
        " ",
        section("Key ảo của " + PLAYER_NAME),
        GENERIC_ENTRY,
        " "
    );

    public static final LangString COMMAND_KEY_INSPECT_ENTRY = LangString.of("Command.Key.Show.Format.Entry",
        color(LABEL, " • ") + color(VALUE, "x" + GENERIC_AMOUNT + " ") + KEY_NAME
    );


    public static final LangString COMMAND_PREVIEW_DESC = LangString.of("Command.Preview.Desc",
        "Mở menu xem trước crate.");

    public static final LangString COMMAND_PREVIEW_USAGE = LangString.of("Command.Preview.Usage",
        "<crate> [player]");

    public static final LangText COMMAND_PREVIEW_DONE_OTHERS = LangText.of("Command.Preview.Done.Others",
        success("Đã mở xem trước " + hl(CRATE_NAME) + " cho " + hl(PLAYER_DISPLAY_NAME)));


    public static final LangString COMMAND_RESET_LIMIT_DESC = LangString.of("Command.ResetLimit.Desc",
        "Đặt lại giới hạn trúng thưởng của người chơi.");

    public static final LangString COMMAND_RESET_LIMIT_USAGE = LangString.of("Command.ResetLimit.Usage",
        "<player> <crate> [reward]");

    public static final LangText COMMAND_RESET_LIMIT_DONE_CRATE = LangText.of("Command.ResetLimit.Done.Crate",
        success("Đã đặt lại giới hạn của " + hl(PLAYER_NAME) + " cho mọi phần thưởng của " + hl(CRATE_NAME)));

    public static final LangText COMMAND_RESET_LIMIT_DONE_REWARD = LangText.of("Command.ResetLimit.Done.Reward",
        success("Đã đặt lại giới hạn của " + hl(PLAYER_NAME) + " cho " + hl(REWARD_NAME) + " " + color(DARK, "(" + CRATE_NAME + ")")));


    public static final LangString COMMAND_RESET_COOLDOWN_DESC = LangString.of("Command.ResetCooldown.Desc",
        "Đặt lại thời gian chờ mở crate của người chơi.");

    public static final LangString COMMAND_RESET_COOLDOWN_USAGE = LangString.of("Command.ResetCooldown.Usage",
        "<player> <crate>");

    public static final LangString COMMAND_RESET_PITY_DESC = LangString.of("Command.ResetPity.Desc",
        "Đặt lại bộ đếm bảo hiểm crate của người chơi.");

    public static final LangString COMMAND_RESET_PITY_USAGE = LangString.of("Command.ResetPity.Usage",
        "<player> <crate>");

    public static final LangText COMMAND_RESET_PITY_DONE = LangText.of("Command.ResetPity.Done",
        success("Đã đặt lại bảo hiểm " + hl(CRATE_NAME) + " của " + hl(PLAYER_NAME)));

    public static final LangText COMMAND_RESET_COOLDOWN_DONE = LangText.of("Command.ResetCooldown.Done",
        success("Đã đặt lại thời gian chờ " + hl(CRATE_NAME) + " của " + hl(PLAYER_NAME)));


    public static final LangString COMMAND_SETBLOCK_DESC = LangString.of("Command.SetBlock.Desc",
        "Gắn crate vào block hoặc furniture đang nhìn.");

    public static final LangString COMMAND_SETBLOCK_USAGE = LangString.of("Command.SetBlock.Usage",
        "<crate>");

    public static final LangText COMMAND_SETBLOCK_DONE = LangText.of("Command.SetBlock.Done",
        success("Đã gắn " + hl(CRATE_NAME) + " tại " + hl(LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z) + " " + color(DARK, "(" + LOCATION_WORLD + ")")));

    public static final LangText COMMAND_SETBLOCK_ERROR_TARGET = LangText.of("Command.SetBlock.Error.Target",
        error("Hãy nhìn vào một block hoặc furniture (trong 6 block)"));

    public static final LangText COMMAND_SETBLOCK_ERROR_TAKEN = LangText.of("Command.SetBlock.Error.Taken",
        error("Vị trí này đã gắn với " + hl(CRATE_NAME)));

    public static final LangString COMMAND_REMOVEBLOCK_DESC = LangString.of("Command.RemoveBlock.Desc",
        "Gỡ crate khỏi block hoặc furniture đang nhìn.");

    public static final LangText COMMAND_REMOVEBLOCK_DONE = LangText.of("Command.RemoveBlock.Done",
        success("Đã gỡ " + hl(CRATE_NAME) + " tại " + hl(LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z) + " " + color(DARK, "(" + LOCATION_WORLD + ")")));

    public static final LangText COMMAND_REMOVEBLOCK_ERROR_NOT_LINKED = LangText.of("Command.RemoveBlock.Error.NotLinked",
        error("Block này chưa gắn với crate nào"));


    public static final LangString COMMAND_MENU_USAGE = LangString.of("Command.Menu.Usage",
        "[menu]");

    public static final LangString COMMAND_MENU_DESC = LangString.of("Command.Menu.Desc",
        "Mở menu crate.");

    public static final LangText COMMAND_MENU_DONE_OTHERS = LangText.of("Command.Menu.Done.Others",
        success("Đã mở menu crate cho " + hl(PLAYER_DISPLAY_NAME)));


    public static final LangText CRATE_OPEN_ERROR_INVENTORY_SPACE = LangText.of("Crate.Open.Error.InventorySpace",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        color(ERROR, "Túi đồ đã đầy"),
        desc("Dọn bớt ô trống để mở crate")
    );

    public static final LangText CRATE_OPEN_ERROR_COOLDOWN_TEMPORARY = LangText.of("Crate.Open.Error.Cooldown.Temporary",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        color(ERROR, "Đang chờ mở lại"),
        desc("Mở lại sau " + color(BAD, GENERIC_TIME))
    );

    public static final LangText CRATE_OPEN_ERROR_COOLDOWN_ONE_TIMED = LangText.of("Crate.Open.Error.Cooldown.OneTimed",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        color(ERROR, "Không thể mở"),
        desc("Crate này chỉ được mở 1 lần")
    );

    public static final LangText CRATE_OPEN_ERROR_NO_KEY = LangText.of("Crate.Open.Error.NoKey",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        color(ERROR, "Thiếu key"),
        desc("Bạn chưa có key để mở crate này")
    );

    public static final LangText CRATE_OPEN_ERROR_NO_HOLD_KEY = LangText.of("Crate.Open.Error.NoHoldKey",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        color(ERROR, "Chưa cầm key"),
        desc("Hãy cầm key trên tay để mở crate")
    );

    public static final LangText CRATE_OPEN_ERROR_NO_REWARDS = LangText.of("Crate.Open.Error.NoRewards",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        color(ERROR, "Hết phần thưởng"),
        desc("Hiện không còn phần thưởng cho bạn, thử lại sau")
    );

    public static final LangText CRATE_OPEN_ERROR_CANT_AFFORD = LangText.of("Crate.Open.Error.CantAfford",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        color(ERROR, "Không đủ tiền"),
        desc("Cần " + color(BAD, GENERIC_AMOUNT) + " để mở crate này")
    );

    public static final LangText CRATE_OPEN_ERROR_ALREADY = LangText.of("Crate.Open.Error.Already",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        color(ERROR, "Đang mở crate"),
        desc("Hãy đợi lần mở hiện tại xong")
    );

    public static final LangText CRATE_OPEN_REWARD_INFO = LangText.of("Crate.Open.Reward.Info",
        TAG_NO_PREFIX,
        success("Bạn nhận được " + REWARD_NAME + color(DESC, " từ ") + CRATE_NAME));

    public static final LangText CRATE_OPEN_BATCH_SUMMARY = LangText.of("Crate.Open.Batch.Summary",
        TAG_NO_PREFIX,
        success("Đã mở " + color(VALUE, "x" + GENERIC_AMOUNT) + " " + CRATE_NAME + color(DESC, ", nhận được:")),
        GENERIC_ENTRY
    );

    public static final LangString CRATE_OPEN_BATCH_ENTRY = LangString.of("Crate.Open.Batch.Entry",
        color(LABEL, " • ") + color(VALUE, "x" + GENERIC_AMOUNT + " ") + REWARD_NAME
    );

    public static final LangText CRATE_OPEN_MILESTONE_COMPLETED = LangText.of("Crate.Open.Milestone.Completed",
        TAG_NO_PREFIX,
        SOUND.enclose(Sound.ENTITY_PLAYER_LEVELUP),
        success("Đạt mốc " + hl(MILESTONE_OPENINGS + " lần mở") + ", nhận " + REWARD_NAME)
    );

    public static final LangText CRATE_OPEN_REWARD_BROADCAST = LangText.of("Crate.Open.Reward.Broadcast",
        SOUND.enclose(Sound.BLOCK_NOTE_BLOCK_BELL),
        color(SECTION, PLAYER_DISPLAY_NAME) + color(DESC, " vừa trúng ") + REWARD_NAME + color(DESC, " từ ") + CRATE_NAME
    );

    public static final LangText CRATE_OPEN_REWARD_REPLACED = LangText.of("Crate.Open.Reward.Replaced",
        warn(hl(GENERIC_NAME) + " vừa hết lượt, bạn nhận " + REWARD_NAME + " thay thế"));

    public static final LangText CRATE_OPEN_PITY = LangText.of("Crate.Open.Pity",
        success("Bảo hiểm kích hoạt, bạn chắc chắn nhận " + REWARD_NAME));

    public static final LangText CRATE_OPEN_REWARD_SOLD_OUT = LangText.of("Crate.Open.Reward.SoldOut",
        warn(hl(REWARD_NAME) + " vừa hết lượt, bạn được hoàn lại key"));

    public static final LangText CRATE_OPEN_ERROR_INTERACT_COOLDOWN = LangText.of("Crate.Open.Error.InteractCooldown",
        error("Mở lại sau " + color(BAD, GENERIC_TIME))
    );

    public static final LangText CRATE_PREVIEW_ERROR_COOLDOWN = LangText.of("Crate.Preview.Error.Cooldown",
        error("Xem trước lại sau " + color(BAD, GENERIC_TIME))
    );


    public static final LangText ERROR_INVALID_CRATE = LangText.of("Crate.Error.Invalid",
        error("Crate không tồn tại"));

    public static final LangText ERROR_INVALID_KEY = LangText.of("Crate.Key.Error.Invalid",
        error("Key không tồn tại"));

    public static final LangText ERROR_INVALID_MENU = LangText.of("Menu.Invalid",
        error("Menu không tồn tại"));


    public static final LangString OTHER_MIDNIGHT = LangString.of("Other.Midnight", "0h hằng ngày");
    public static final LangString OTHER_FREE     = LangString.of("Other.Free", "Miễn phí");
    public static final LangString OTHER_ITEM_SOURCE_NBT = LangString.of("Other.ItemSource.NBT", "Bản lưu NBT");


    public static final LangString EDITOR_ERROR_BAD_NAME = LangString.of("Editor.Error.BadName",
        error("Chỉ dùng chữ không dấu và số"));

    public static final LangString EDITOR_ERROR_DUPLICATED_CRATE = LangString.of("Crate.Error.Exists",
        error("Crate này đã tồn tại"));

    public static final LangString EDITOR_ERROR_DUPLICATED_KEY = LangString.of("Crate.Key.Error.Exists",
        error("Key này đã tồn tại"));

    public static final LangString EDITOR_ERROR_DUPLICATED_REWARD = LangString.of("Editor.Reward.Error.Create.Exist",
        error("Phần thưởng này đã tồn tại"));

    private static final String TITLE = "#3A3A3A";

    public static final LangString EDITOR_TITLE_MAIN             = LangString.of("Editor.Title.Main", color(TITLE, "Crate - Chỉnh sửa"));
    public static final LangString EDITOR_TITLE_CRATE_LIST       = LangString.of("Editor.Title.Crates", color(TITLE, "Danh sách crate"));
    public static final LangString EDITOR_TITLE_CRATE_SETTINGS   = LangString.of("Editor.Title.Crate.Settings", color(TITLE, "Cài đặt crate"));
    public static final LangString EDITOR_TITLE_CRATE_MILESTONES = LangString.of("Editor.Title.Crate.Milestones", color(TITLE, "Mốc mở crate"));
    public static final LangString EDITOR_TITLE_CRATE_EFFECT     = LangString.of("Editor.Title.Crate.Effect", color(TITLE, "Hiệu ứng crate"));
    public static final LangString EDITOR_TITLE_CRATE_PLACEMENT  = LangString.of("Editor.Title.Crate.Placement", color(TITLE, "Vị trí đặt crate"));
    public static final LangString EDITOR_TITLE_REWARD_LIST      = LangString.of("Editor.Title.Reward.List", color(TITLE, "Phần thưởng của crate"));
    public static final LangString EDITOR_TITLE_REWARD_SETTINGS  = LangString.of("Editor.Title.Reward.Settings", color(TITLE, "Cài đặt phần thưởng"));
    public static final LangString EDITOR_TITLE_REWARD_LIMITS    = LangString.of("Editor.Title.Reward.Limits", color(TITLE, "Giới hạn phần thưởng"));
    public static final LangString EDITOR_TITLE_REWARD_SORT      = LangString.of("Editor.Title.Reward.Sort", color(TITLE, "Sắp xếp phần thưởng"));
    public static final LangString EDITOR_TITLE_KEY_LIST         = LangString.of("Editor.Title.Keys", color(TITLE, "Danh sách key"));
    public static final LangString EDITOR_TITLE_KEY_SETTINGS     = LangString.of("Editor.Title.Key.Settings", color(TITLE, "Cài đặt key"));

    public static final LangString EDITOR_ENTER_DISPLAY_NAME = LangString.of("Editor.Enter.DisplayName",
        enter("tên hiển thị"));

    public static final LangString EDITOR_ENTER_AMOUNT = LangString.of("Editor.Enter.Amount",
        enter("số lượng"));

    public static final LangString EDITOR_ENTER_VALUE = LangString.of("Editor.Enter.Value",
        enter("giá trị"));

    public static final LangString EDITOR_ENTER_PITY_OPENINGS = LangString.of("Editor.Crate.Enter.PityOpenings",
        enter("số lượt"));

    public static final LangString EDITOR_ENTER_SECONDS = LangString.of("Editor.Crate.Enter.Seconds",
        enter("số giây"));

    public static final LangString EDITOR_ENTER_WEIGHT = LangString.of("Editor.Reward.Enter.Chance",
        enter("trọng số"));

    public static final LangString EDITOR_ENTER_COMMAND = LangString.of("Editor.Reward.Enter.Command",
        enter("lệnh"));

    public static final LangString EDITOR_ENTER_CRATE_ID = LangString.of("Editor.Crate.Enter.Id",
        enter("ID crate"));

    public static final LangString EDITOR_ENTER_PARTICLE_NAME = LangString.of("Editor.Crate.Enter.Particle.Name",
        enter("tên hạt"));

    public static final LangString EDITOR_ENTER_KEY_ID = LangString.of("Editor.Crate.Enter.KeyId",
        enter("ID key"));

    public static final LangString EDITOR_ENTER_BLOCK_LOCATION = LangString.of("Editor.Crate.Enter.BlockLocation",
        desc("Click vào " + color(OK, "block hoặc furniture") + " để gắn crate"));

    public static final LangString EDITOR_ENTER_HOLOGRAM_TEMPLATE = LangString.of("Editor.Crate.Enter.HologramTemplate",
        enter("mẫu hologram"));

    public static final LangString EDITOR_ENTER_ANIMATION_CONFIG = LangString.of("Editor.Crate.Enter.AnimationConfig",
        enter("kiểu mở"));

    public static final LangString EDITOR_ENTER_PREVIEW_CONFIG = LangString.of("Editor.Crate.Enter.PreviewConfig",
        enter("menu xem trước"));

    public static final LangString EDITOR_ENTER_OPEN_COST = LangString.of("Editor.Crate.Enter.Open_Cost",
        enter("<loại tiền> <số tiền>"));

    public static final LangString EDITOR_ENTER_REWARD_ID = LangString.of("Editor.Reward.Enter.Id",
        enter("ID phần thưởng"));

    public static final LangString EDITOR_ENTER_RARITY = LangString.of("Editor.Reward.Enter.Rarity",
        enter("độ hiếm"));

    public static final LangString EDITOR_ENTER_PERMISSION = LangString.of("Editor.Reward.Enter.Permissions",
        enter("quyền"));

    private static String enter(String what) {
        return desc("Nhập " + color(OK, what));
    }
}
