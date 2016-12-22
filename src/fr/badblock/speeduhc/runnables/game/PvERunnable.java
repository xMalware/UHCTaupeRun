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

public class PvERunnable extends BukkitRunnable implements TimeProvider {
	public static boolean   pve   = false;
	public static boolean   isScd = false;
	
	public static int TIME;
	private int 			time = TIME / (isScd ? 4 : 1);
	
	public PvERunnable() {
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
			TranslatableString title = new TranslatableString("uhcspeed.pvein.title", time, color.getChar());

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;

				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
		} else if(time == 0){
			cancel();
			
			pve = true;
			TranslatableString title = new TranslatableString("uhcspeed.pve.title");

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				
				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
			
			if(!isScd){
				new PvPRunnable().runTaskTimer(GameAPI.getAPI(), 20L, 20L);
			} else {
				StartRunnable.gameTask = new DeathmatchRunnable();
				StartRunnable.gameTask.runTaskTimer(GameAPI.getAPI(), 20L, 20L);
			}
			
			isScd = true;
		}
	}
	
	@Override
	public String getId(int num) {
		return num == 0 ? "pve" : "pvp";
	}

	@Override
	public int getTime(int num) {
		return time + (num == 1 ? PvPRunnable.TIME : 0);
	}

	@Override
	public int getProvidedCount() {
		return 2;
	}
}