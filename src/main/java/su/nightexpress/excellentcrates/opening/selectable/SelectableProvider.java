package su.nightexpress.excellentcrates.opening.selectable;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.api.opening.Opening;
import su.nightexpress.excellentcrates.api.opening.OpeningProvider;
import su.nightexpress.excellentcrates.crate.impl.CrateSource;
import su.nightexpress.excellentcrates.key.CrateKey;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

/**
 * Port of ExcellentCrates 6.3.0 SelectableProvider. One provider per file in /openings/selectable/,
 * used as a crate "Opening" config like the animation ones (e.g. 'selective_1', 'selective_3').
 */
public class SelectableProvider implements OpeningProvider {

    private final CratesPlugin   plugin;
    private final int            selectionAmount;
    private final SelectableMenu menu;

    public SelectableProvider(@NotNull CratesPlugin plugin, @NotNull FileConfig config, int defaultAmount) {
        this.plugin = plugin;
        this.selectionAmount = Math.max(1, ConfigValue.create("Selection.Amount", defaultAmount,
            "Số phần thưởng người chơi được chọn trong mỗi lần mở."
        ).read(config));
        this.menu = new SelectableMenu(plugin, config);
    }

    @Override
    @NotNull
    public Opening createOpening(@NotNull Player player, @NotNull CrateSource source, @Nullable CrateKey key) {
        return new SelectableOpening(this.plugin, this, this.menu, player, source, key);
    }

    public int getSelectionAmount() {
        return this.selectionAmount;
    }

    @NotNull
    public SelectableMenu getMenu() {
        return this.menu;
    }
}
