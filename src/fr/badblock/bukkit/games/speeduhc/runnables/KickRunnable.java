package fr.badblock.bukkit.games.speeduhc.runnables;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.speeduhc.PluginUHC;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;

public class KickRunnable extends BukkitRunnable {
	private int time = 15;

	@Override
	public void run(){
		if(time == -3){
			Bukkit.shutdown();
		}else if(time <= 5){

			for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
				player.sendPlayer(PluginUHC.getInstance().getConfiguration().fallbackServer);
			}

		} 

		time--;
	}
}