package su.nightexpress.excellentcrates.command.basic;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.config.Perms;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;

/**
 * Unlinks the crate from the block or furniture the player is looking at, keeping its other positions.
 * Same unlink as 7.0 BlockLinkService#unlinkCrateBlock (upstream does it on sneak + break); the command is BoxPvP's own.
 */
public class RemoveBlockCommand extends AbstractCommand<CratesPlugin> {

    public RemoveBlockCommand(@NotNull CratesPlugin plugin) {
        super(plugin, new String[]{"removeblock"}, Perms.COMMAND_SETBLOCK);
        this.setDescription(Lang.COMMAND_REMOVEBLOCK_DESC);
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;

        SetBlockCommand.Target target = SetBlockCommand.findTarget(player);
        if (target == null) {
            Lang.COMMAND_SETBLOCK_ERROR_TARGET.getMessage().send(sender);
            return;
        }

        Location location = target.location();
        Crate crate = plugin.getCrateManager().getCrateByLocation(location);
        if (crate == null || !crate.removeBlockPosition(location)) {
            Lang.COMMAND_REMOVEBLOCK_ERROR_NOT_LINKED.getMessage().send(sender);
            return;
        }

        crate.resetHologram();
        crate.saveSettings();

        Lang.COMMAND_REMOVEBLOCK_DONE.getMessage()
            .replace(crate.replacePlaceholders())
            .replace(Placeholders.forLocation(location))
            .send(sender);
    }
}
