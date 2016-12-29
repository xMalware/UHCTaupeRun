package fr.badblock.speeduhc.runnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import fr.badblock.speeduhc.PluginUHC;
import fr.badblock.speeduhc.players.UHCScoreboard;
import fr.badblock.speeduhc.runnables.game.GameRunnable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartRunnable extends BukkitRunnable {
	public    static final int 		          TIME_BEFORE_START = 300;
	protected static 	   StartRunnable      task 		       = null;
	public    static 	   GameRunnable 	  gameTask		   = null;

	private int time;

	@Override
	public void run() {
		GameAPI.setJoinable(time > 10);
		if(time == 0){
			for(BadblockPlayer player : BukkitUtils.getPlayers()){
				if (player.getCustomObjective() != null)
					new UHCScoreboard(player);
			}
			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				bPlayer.playSound(Sound.ORB_PICKUP);
			}

			GameAPI.getAPI().balanceTeams(false);

			new TeleportRunnable().runTaskTimer(GameAPI.getAPI(), 0, 5L);

			cancel();
		} else if(time % 10 == 0 || time <= 5){
			sendTime(time);
		}

		if(time == 3){
			GameAPI.getAPI().getBadblockScoreboard().endVote();

			for(Player player : Bukkit.getOnlinePlayers()){
				new UHCScoreboard((BadblockPlayer) player);
			}
		}

		sendTimeHidden(time);

		time--;
	}

	protected void start(){
		sendTime(time);

		runTaskTimer(GameAPI.getAPI(), 0, 20L);
	}

	private void sendTime(int time){
		ChatColor color = getColor(time);
		TranslatableString title = GameMessages.startIn(time, color);

		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bPlayer = (BadblockPlayer) player;

			bPlayer.playSound(Sound.NOTE_PLING);
			bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
			bPlayer.sendTimings(2, 30, 2);
		}
	}

	private void sendTimeHidden(int time){
		ChatColor color = getColor(time);
		TranslatableString actionbar = GameMessages.startInActionBar(time, color);

		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bPlayer = (BadblockPlayer) player;

			if(time > 0)
				bPlayer.sendTranslatedActionBar(actionbar.getKey(), actionbar.getObjects());
			bPlayer.setLevel(time);
			bPlayer.setExp(0.0f);
		}
	}

	public static ChatColor getColor(int time){
		if(time == 1)
			return ChatColor.DARK_RED;
		else if(time <= 5)
			return ChatColor.RED;
		else return ChatColor.AQUA;
	}

	public static void joinNotify(int currentPlayers, int maxPlayers){
		if (task != null) {
			int a = task.time - (TIME_BEFORE_START / Bukkit.getMaxPlayers());
			if ((a < task.time && task.time <= 10) || (a < 10 && task.time >= 10)) task.time = 10;
			else task.time = a;
		}
		int minPlayers = PluginUHC.getInstance().getConfiguration().minPlayers;

		if(currentPlayers >= minPlayers)
			startGame(false);
	}

	public static void startGame(boolean force){
		GameRunnable.forceEnd = false;

		if(task == null){
			task = new StartRunnable(force ? 5 : TIME_BEFORE_START);
			task.start();
		}
	}

	public static void stopGame(){
		GameRunnable.forceEnd = true;

		if(task != null){
			task.time = TIME_BEFORE_START;
			task.cancel();
		}

		task = null;
		gameTask = null;
	}

	public static boolean started(){
		return task != null;
	}
}
