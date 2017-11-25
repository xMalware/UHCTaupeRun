package fr.badblock.bukkit.games.speeduhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.badblock.bukkit.games.speeduhc.PluginUHC;
import fr.badblock.bukkit.games.speeduhc.players.UHCScoreboard;
import fr.badblock.bukkit.games.speeduhc.runnables.StartRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.rankeds.RankedCalc;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class QuitListener extends BadListener {
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if (StartRunnable.gameTask == null && BukkitUtils.getPlayers().size() - 1 < PluginUHC.getInstance().getConfiguration().minPlayers) {
			StartRunnable.stopGame();
			StartRunnable.time = StartRunnable.time > 60 ? StartRunnable.time : 60;
		}
		
		BadblockPlayer player = (BadblockPlayer) e.getPlayer();
		if (!player.getGameMode().equals(GameMode.SPECTATOR) && !player.getBadblockMode().equals(BadblockMode.SPECTATOR))
		{
			GameMessages.quitMessage(GameAPI.getGameName(), player.getTabGroupPrefix().getAsLine(player) + player.getName(), Bukkit.getOnlinePlayers().size(), PluginUHC.getInstance().getMaxPlayers()).broadcast();
		}
		
		if(!inGame()) return;

		BadblockTeam   team   = player.getTeam();

		if(team == null) return;

		// Work with rankeds
		String rankedGameName = RankedManager.instance.getCurrentRankedGameName();
		player.getPlayerData().incrementTempRankedData(rankedGameName, UHCScoreboard.LOOSES, 1);
		RankedManager.instance.calcPoints(rankedGameName, player, new RankedCalc()
		{

			@Override
			public long done() {
				double kills = RankedManager.instance.getData(rankedGameName, player, UHCScoreboard.KILLS);
				double deaths = RankedManager.instance.getData(rankedGameName, player, UHCScoreboard.DEATHS);
				double wins = RankedManager.instance.getData(rankedGameName, player, UHCScoreboard.WINS);
				double looses = RankedManager.instance.getData(rankedGameName, player, UHCScoreboard.LOOSES);
				double total = 
						( (kills * 2) + (wins * 4) + 
								((kills / (deaths > 0 ? deaths : 1) ) ) )
						/ (1 + looses);
				return (long) total;
			}

		});
		RankedManager.instance.fill(rankedGameName);


		if(team.getOnlinePlayers().size() == 0){
			GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
			GameAPI.getAPI().unregisterTeam(team);

			new TranslatableString("speeduhc.team-loose", team.getChatName()).broadcast();
		}
	}
}
