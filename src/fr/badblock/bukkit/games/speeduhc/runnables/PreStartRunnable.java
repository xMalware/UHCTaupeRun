package fr.badblock.bukkit.games.speeduhc.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.speeduhc.PluginUHC;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class PreStartRunnable extends BukkitRunnable {
	@Override
	public void run(){
		if(StartRunnable.task != null){
			cancel();
		} else {
			doJob();
		}
	}
	
	public static void doJob(){
		TranslatableString actionbar = GameMessages.missingPlayersActionBar( PluginUHC.getInstance().getMaxPlayers() - Bukkit.getOnlinePlayers().size() );
		
		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bPlayer = (BadblockPlayer) player;
			bPlayer.sendTranslatedActionBar(actionbar.getKey(), actionbar.getObjects());
		}
	}
}
