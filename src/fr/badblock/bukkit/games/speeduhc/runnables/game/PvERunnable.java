package fr.badblock.bukkit.games.speeduhc.runnables.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.speeduhc.PluginUHC;
import fr.badblock.bukkit.games.speeduhc.runnables.StartRunnable;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class PvERunnable extends BukkitRunnable {
	public static boolean   pve   = false;
	public static PvERunnable ins;
	
	public int time;
	
	public PvERunnable(int div){
		ins = this;
		pve = false;
		time = PluginUHC.getInstance().getConfiguration().time.pveTime * 60 / div;
	}
	
	@Override
	public void run() {
		time--;

		for (BadblockPlayer bp : BukkitUtils.getPlayers())
		if (bp.getCustomObjective() != null)
			bp.getCustomObjective().generate();

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
			
		}
	}
}