package fr.badblock.bukkit.games.speeduhc.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.games.speeduhc.PluginUHC;
import fr.badblock.bukkit.games.speeduhc.runnables.StartRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class GameCommand extends AbstractCommand {
	public GameCommand() {
		super("game", new TranslatableString("commands.gspeeduhc.usage"), "animation.gamecommand");
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) {
			return false;
		}
		
		BadblockPlayer player = (BadblockPlayer) sender;
		PluginUHC plug = PluginUHC.getInstance();

		switch(args[0].toLowerCase()){
			case "start":
				String msg = "commands.grush.start";
				
				StartRunnable.startGame(true);
				player.sendTranslatedMessage(msg);
			break;
			case "stop":
				msg = "commands.grush.stop";
				
				if(StartRunnable.started()){
					StartRunnable.stopGame();
				} else msg += "-fail";
				
				player.sendTranslatedMessage(msg);
			break;
			case "playersperteam":
				if (!plug.getConfiguration().allowTeams) {
					player.sendTranslatedMessage("commands.grush.unabletochangeplayersperteam");
					return true;
				}
				if(args.length != 2)
					return false;
				
				int perTeam = 4;
				
				try {
					perTeam = Integer.parseInt(args[1]);
				} catch(Exception e){
					return false;
				}
				
				for(BadblockTeam team : GameAPI.getAPI().getTeams())
					team.setMaxPlayers(perTeam);
				
				plug.getConfiguration().maxPlayersInTeam = perTeam;
				plug.setMaxPlayers(GameAPI.getAPI().getTeams().size() * perTeam);
				try {
					BukkitUtils.setMaxPlayers(GameAPI.getAPI().getTeams().size() * perTeam);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				player.sendTranslatedMessage("commands.grush.modifycount");
			break;
			case "players":
				if (plug.getConfiguration().allowTeams) {
					player.sendTranslatedMessage("commands.grush.unabletochangeplayers");
					return true;
				}
				if(args.length != 2)
					return false;
				
				int maxPlayers = 24;
				
				try {
					maxPlayers = Integer.parseInt(args[1]);
				} catch(Exception e){
					return false;
				}
				
				plug.getConfiguration().maxPlayersInTeam = maxPlayers;
				plug.getConfiguration().minPlayers = maxPlayers / 2;
				plug.setMaxPlayers(maxPlayers);
				try {
					BukkitUtils.setMaxPlayers(maxPlayers);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				player.sendTranslatedMessage("commands.grush.modifycount");
			break;
			default: return false;
		}
		
		return true;
	}
}