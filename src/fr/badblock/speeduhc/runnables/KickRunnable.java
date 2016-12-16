package fr.badblock.speeduhc.runnables;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.Gson;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.sentry.SEntry;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.speeduhc.PluginUHC;

public class KickRunnable extends BukkitRunnable {
	private int time = 15;

	@Override
	public void run(){
		if(time <= 0 && time > -3){

			for(BadblockPlayer player : BukkitUtils.getPlayers()){
				player.sendTranslatedMessage("game.nonewgamefound");
				player.sendPlayer(PluginUHC.getInstance().getConfiguration().fallbackServer);
			}

		} else  if (time == 10) {
			Gson gson = GameAPI.getGson();
			for(BadblockPlayer player : BukkitUtils.getPlayers()){
				GameAPI.getAPI().getRabbitSpeaker().sendAsyncUTF8Publisher("networkdocker.sentry.join", gson.toJson(new SEntry(player.getName(), Bukkit.getServerName().split("_")[0], true)), 5000, false);
			}
		}else if(time == -3){
			Bukkit.shutdown();
		}

		time--;
	}
}
