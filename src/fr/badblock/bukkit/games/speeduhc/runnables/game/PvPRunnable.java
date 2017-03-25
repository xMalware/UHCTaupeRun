package fr.badblock.bukkit.games.speeduhc.runnables.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.speeduhc.PluginUHC;
import fr.badblock.bukkit.games.speeduhc.players.TimeProvider;
import fr.badblock.bukkit.games.speeduhc.players.UHCScoreboard;
import fr.badblock.bukkit.games.speeduhc.runnables.StartRunnable;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class PvPRunnable extends BukkitRunnable implements TimeProvider {
	public static boolean pvp = false;

	private int time;

	public PvPRunnable() {
		time = PluginUHC.getInstance().getConfiguration().time.pvpTime * 60;
		UHCScoreboard.setTimeProvider(this);
	}

	@Override
	public void run() {
		time--;
		for (BadblockPlayer bp : BukkitUtils.getPlayers())
		if (bp.getCustomObjective() != null)
			bp.getCustomObjective().generate();

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
		}
	}

	@Override
	public String getId(int num) {
		return num == 0 ? "deathmatch" : "pvp";
	}

	@Override
	public int getTime(int num) {
		return num == 0 ? GameRunnable.ins.totalTime - GameRunnable.ins.pastTime : time;
	}

	@Override
	public int getProvidedCount() {
		return 2;
	}

	@Override
	public boolean displayed() {
		return !pvp;
	}
}
