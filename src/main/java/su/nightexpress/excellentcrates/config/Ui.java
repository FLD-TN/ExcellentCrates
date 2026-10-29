package su.nightexpress.excellentcrates.config;

import org.jetbrains.annotations.NotNull;

/**
 * BoxPvP text style (BoxCore standard): no bold, description line, sections, "• Label: value" rows,
 * action lines last as "[Chuột trái để ...]", ✔ / ✘ / (!) chat marks.
 */
public final class Ui {

    public static final String DESC    = "#BFC7D5";
    public static final String SECTION = "#FFD166";
    public static final String LABEL   = "#AAAAAA";
    public static final String VALUE   = "#FFFFFF";
    public static final String DARK    = "#555555";
    public static final String OK      = "#7CFC9A";
    public static final String BAD     = "#FF8A8A";
    public static final String ALT     = "#7FDBFF";
    public static final String ERROR   = "#FF6B6B";

    /* Unique pastel colours per role. */
    public static final String C_CRATE   = "#FFB86B";
    public static final String C_KEY     = "#F9E07F";
    public static final String C_REWARD  = "#B39DFF";
    public static final String C_LIMIT   = "#FF9EC7";
    public static final String C_PLACE   = "#7FDBFF";
    public static final String C_EFFECT  = "#9CF6E1";
    public static final String C_MILE    = "#C3F584";
    public static final String C_ITEM    = "#E0C3FC";
    public static final String C_SELECT  = "#80E0B0";
    public static final String C_AMOUNT  = "#FFC4A3";

    private Ui() {}

    @NotNull
    public static String color(@NotNull String hex, @NotNull String text) {
        return "<" + hex + ">" + text + "</" + hex + ">";
    }

    @NotNull
    public static String name(@NotNull String hex, @NotNull String text) {
        return color(hex, text);
    }

    /** Item name with an extra dark tag: "Name [info]". */
    @NotNull
    public static String name(@NotNull String hex, @NotNull String text, @NotNull String tag) {
        return color(hex, text) + " " + color(DARK, "[") + color(VALUE, tag) + color(DARK, "]");
    }

    @NotNull
    public static String desc(@NotNull String text) {
        return color(DESC, text);
    }

    @NotNull
    public static String section(@NotNull String text) {
        return color(SECTION, text + ":");
    }

    @NotNull
    public static String row(@NotNull String label, @NotNull String value) {
        return color(LABEL, "• " + label + ": ") + color(VALUE, value);
    }

    @NotNull
    public static String bullet(@NotNull String text) {
        return color(LABEL, "• ") + color(VALUE, text);
    }

    @NotNull
    public static String note(@NotNull String text) {
        return color(LABEL, text);
    }

    @NotNull
    public static String action(@NotNull String text) {
        return action(OK, text);
    }

    @NotNull
    public static String actionBad(@NotNull String text) {
        return action(BAD, text);
    }

    @NotNull
    public static String actionAlt(@NotNull String text) {
        return action(ALT, text);
    }

    @NotNull
    public static String action(@NotNull String hex, @NotNull String text) {
        return color(DARK, "[") + color(hex, text) + color(DARK, "]");
    }

    @NotNull
    public static String hl(@NotNull String text) {
        return color(SECTION, text);
    }

    @NotNull
    public static String success(@NotNull String text) {
        return color(OK, "✔ ") + color(DESC, text);
    }

    @NotNull
    public static String error(@NotNull String text) {
        return color(ERROR, "✘ ") + color(DESC, text);
    }

    @NotNull
    public static String warn(@NotNull String text) {
        return color(SECTION, "(!) ") + color(DESC, text);
    }
}
