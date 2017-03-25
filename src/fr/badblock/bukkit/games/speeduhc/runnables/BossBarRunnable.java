package fr.badblock.bukkit.games.speeduhc.runnables;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.players.BadblockPlayer;

public class BossBarRunnable extends BukkitRunnable {
	private UUID player;
	private int  lastBc = -1;
	private long lastXp = -1;
	
	public BossBarRunnable(UUID player){
		this.player = player;
	}
	
	@Override
	public void run(){
		BadblockPlayer player = (BadblockPlayer) Bukkit.getPlayer(this.player);
		
		if(player != null){
			
			if(player.getPlayerData().getBadcoins() != lastBc || player.getPlayerData().getXp() != lastXp){
				lastBc = player.getPlayerData().getBadcoins();
				lastXp = player.getPlayerData().getXp();
				
				player.sendTranslatedBossBar("uhcspeed.bossbar", lastBc, player.getPlayerData().getLevel(), lastXp, player.getPlayerData().getXpUntilNextLevel());
			}
			
		} else cancel();
	}
}
