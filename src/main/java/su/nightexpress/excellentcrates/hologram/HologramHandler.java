package su.nightexpress.excellentcrates.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.Reward;

public interface HologramHandler {

    void setup();

    void shutdown();

    void refresh(@NotNull Crate crate);

    void create(@NotNull Crate crate);

    void remove(@NotNull Crate crate);

    /**
     * Port of 6.0.0 HologramListener#onQuit: forget the player as a viewer, so holograms spawn again on rejoin.
     */
    void handleQuit(@NotNull Player player);

//    default void update(@NotNull Crate crate) {
//        this.remove(crate);
//        this.create(crate);
//    }

    void createReward(@NotNull Player player, @NotNull Reward reward, @NotNull Location location);

    void removeReward(@NotNull Player player);
}
