package fr.badblock.speeduhc.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapSelection;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.speeduhc.PluginUHC;

public class UHCCommand extends AbstractCommand {
	public UHCCommand() {
		super("uhcspeed", new TranslatableString("commands.speeduhc.usage"), GamePermission.ADMIN, "uhc");
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) return false;

		BadblockPlayer player = (BadblockPlayer) sender;
		PluginUHC plug   = PluginUHC.getInstance();


		switch(args[0].toLowerCase()){
			case "mainspawn":
				plug.getConfiguration().spawn = new MapLocation(player.getLocation());
				plug.saveJsonConfig();
				break;
			case "spawnzone":
				if (player.getSelection() == null) {
					player.sendTranslatedMessage("commands.noselection");
				} else {
					plug.getConfiguration().spawnZone = new MapSelection(player.getSelection());
					plug.saveJsonConfig();
				}
				break;
		}

		player.sendTranslatedMessage("commands.speeduhc.modified");

		return true;
	}
}
