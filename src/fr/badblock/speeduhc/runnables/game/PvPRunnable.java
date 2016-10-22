package fr.badblock.speeduhc.runnables.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.speeduhc.players.TimeProvider;
import fr.badblock.speeduhc.players.UHCScoreboard;
import fr.badblock.speeduhc.runnables.StartRunnable;

public class PvPRunnable extends BukkitRunnable implements TimeProvider {
	public static boolean pvp = false;
	
	public static final int TIME = 20 * 60;
	private 			int time = TIME;
	
	public PvPRunnable() {
		UHCScoreboard.setTimeProvider(this);
	}
	
	@Override
	public void run() {
		DeathmatchRunnable.generalTime--;
		time--;
		
		if(DeathmatchRunnable.countEntities() <= 1){
			cancel();
			DeathmatchRunnable.doEnd();
			
			return;
		}

		if( (time % 10 == 0 || time <= 5) && time > 0 && time <= 30){
			ChatColor 		   color = StartRunnable.getColor(time);
			TranslatableString title = new TranslatableString("uhcspeed.pvpin.title", time, color.getChar());

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;

				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
		} else if(time == 0){
			cancel();
			
			pvp = true;
			TranslatableString title = new TranslatableString("uhcspeed.pvp.title");

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				
				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
			
			StartRunnable.gameTask = new DeathmatchRunnable();
			StartRunnable.gameTask.runTaskTimer(GameAPI.getAPI(), 20L, 20L);
		}
	}

	@Override
	public String getId(int num) {
		return num == 0 ? "deathmatch" : "pvp";
	}

	@Override
	public int getTime(int num) {
		return num == 0 ? DeathmatchRunnable.generalTime : time;
	}

	@Override
	public int getProvidedCount() {
		return 2;
	}
}
