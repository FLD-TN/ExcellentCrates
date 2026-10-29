package su.nightexpress.excellentcrates.command.basic;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.config.Perms;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.hooks.impl.ItemsAdderHook;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;

import java.util.List;

/**
 * Links a crate to the block or ItemsAdder furniture the player is looking at
 * (7.1.0 added a command to link a crate to any placed block/furniture).
 */
public class SetBlockCommand extends AbstractCommand<CratesPlugin> {

    private static final double DISTANCE = 6D;

    public SetBlockCommand(@NotNull CratesPlugin plugin) {
        super(plugin, new String[]{"setblock"}, Perms.COMMAND_SETBLOCK);
        this.setDescription(Lang.COMMAND_SETBLOCK_DESC);
        this.setUsage(Lang.COMMAND_SETBLOCK_USAGE);
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<@NotNull String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getCrateManager().getCrateIds(false);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.errorUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Crate crate = plugin.getCrateManager().getCrateById(result.getArg(1));
        if (crate == null) {
            Lang.ERROR_INVALID_CRATE.getMessage().send(sender);
            return;
        }

        RayTraceResult trace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(),
            DISTANCE, FluidCollisionMode.NEVER, true, 0.2D, entity -> entity != player);

        Location furniture = null;
        Block block = null;
        if (trace != null) {
            Entity entity = trace.getHitEntity();
            if (entity != null) {
                furniture = ItemsAdderHook.getFurnitureLocation(entity);
            }
            else if (trace.getHitBlock() != null) {
                block = trace.getHitBlock();
                furniture = ItemsAdderHook.getFurnitureLocation(block);
            }
        }

        if (furniture == null && (block == null || block.isEmpty())) {
            Lang.COMMAND_SETBLOCK_ERROR_TARGET.getMessage().send(sender);
            return;
        }

        Location location = furniture != null ? furniture : block.getLocation();
        Crate linked = plugin.getCrateManager().getCrateByLocation(location);
        if (linked != null) {
            Lang.COMMAND_SETBLOCK_ERROR_TAKEN.getMessage().replace(linked.replacePlaceholders()).send(sender);
            return;
        }

        if (furniture != null) {
            crate.addFurniturePosition(furniture);
        }
        else {
            crate.addBlockPosition(location);
        }
        crate.updateHologram();
        crate.saveSettings();

        Lang.COMMAND_SETBLOCK_DONE.getMessage()
            .replace(crate.replacePlaceholders())
            .replace(Placeholders.forLocation(location))
            .send(sender);
    }
}
