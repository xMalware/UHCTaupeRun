package fr.badblock.bukkit.games.speeduhc.players;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.speeduhc.UHCAchievementList;
import fr.badblock.bukkit.games.speeduhc.runnables.game.GameRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.InGameData;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.utils.entities.CreatureType;
import fr.badblock.gameapi.utils.i18n.TranslatableWord;
import fr.badblock.gameapi.utils.i18n.Word.WordDeterminant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UHCData implements InGameData {
	public  int     kills     = 0;
	@Getter
	private boolean 		 death     = false;
	private int     		 deathTime = 0;
	private String  		 killed     = "";
	private TranslatableWord killedWord = null;
	
	public double   givedDamage    = 0;
	public double   receivedDamage = 0;

	public int getScore(){
		double ratio = (givedDamage * 5) / (receivedDamage == 0 ? 1 : receivedDamage);
		
		if(death)
			ratio /= 5;
		
		return (int) ( (kills * 5) * ratio );
	}
	
	public String deathData(BadblockPlayer player){
		String killed = killedWord == null ? this.killed : killedWord.getWord(player.getPlayerData().getLocale());
		
		return !death ? "-" : deathTime + " (" + killed + ")";
	}
	
	public void killed(BadblockPlayer bp, Entity by, DamageCause cause){
		this.death     = true;
		this.deathTime = GameRunnable.ins.totalTime - GameRunnable.ins.pastTime;
		
		if(deathTime <= 180){
			bp.getPlayerData().incrementAchievements(bp, UHCAchievementList.UHC_SURVI_1, UHCAchievementList.UHC_SURVI_2, UHCAchievementList.UHC_SURVI_3, UHCAchievementList.UHC_SURVI_4);
		}
		
		if(by != null){
			if(by.getType() == EntityType.PLAYER){
				this.killed     = ((BadblockPlayer) by).getName();
			} else {
				this.killedWord = CreatureType.getByBukkitEntity(by).getWord(false, WordDeterminant.SIMPLE);
			}
		} else this.killed = cause.name().toLowerCase();
	}
	
	public void doReward(BadblockPlayer bp, BadblockTeam winner, BadblockPlayer winnerPlayer, Location winnerLocation, Location looserLocation){
		double badcoins = getScore() / 4;
		double xp	    = getScore() / 2;

		if((winner != null && winner.equals(bp.getTeam())) || (winnerPlayer != null && winnerPlayer.equals(bp))){
			bp.teleport(winnerLocation);
			bp.setAllowFlight(true);
			bp.setFlying(true);
			
			new BukkitRunnable() {
				int count = 5;
				
				@Override
				public void run() {
					count--;
					
					bp.teleport(winnerLocation);
					bp.setAllowFlight(true);
					bp.setFlying(true);
					
					if(count == 0)
						cancel();
				}
			}.runTaskTimer(GameAPI.getAPI(), 5L, 5L);
			
			bp.sendTranslatedTitle("uhcspeed.title-win", winner == null ? winnerPlayer.getName() : winner.getChatName());
			bp.getPlayerData().incrementStatistic("uhcspeed", UHCScoreboard.WINS);

			bp.getPlayerData().incrementAchievements(bp, UHCAchievementList.UHC_WIN_1, UHCAchievementList.UHC_WIN_2, UHCAchievementList.UHC_WIN_3, UHCAchievementList.UHC_WIN_4);
			bp.getPlayerData().incrementAchievements(bp, UHCAchievementList.UHC_SURVI_1, UHCAchievementList.UHC_SURVI_2, UHCAchievementList.UHC_SURVI_3, UHCAchievementList.UHC_SURVI_4);
			
			if(bp.inGameData(UHCData.class).kills >= 5){
				bp.getPlayerData().incrementAchievements(bp, UHCAchievementList.UHC_BKILL_1, UHCAchievementList.UHC_BKILL_2, UHCAchievementList.UHC_BKILL_3, UHCAchievementList.UHC_BKILL_4);
			}
			
			if(bp.inGameData(UHCData.class).receivedDamage == 0){
				bp.getPlayerData().incrementAchievements(bp, UHCAchievementList.UHC_HALF_GOD);
			}
			
			if(bp.inGameData(UHCData.class).kills >= 8 && !bp.getPlayerData().getAchievementState(UHCAchievementList.UHC_BSURVIVOR).isSucceeds()){
				PlayerAchievementState state = bp.getPlayerData().getAchievementState(UHCAchievementList.UHC_BSURVIVOR);
				
				state.progress(1000.0d);
				state.trySucceed(bp, UHCAchievementList.UHC_BSURVIVOR);
			}
		} else {
			badcoins = ((double) badcoins) / 1.5d;

			bp.jailPlayerAt(looserLocation);
			bp.sendTranslatedTitle("uhcspeed.title-loose", winner == null ? winnerPlayer.getName() : winner.getChatName());

			bp.getPlayerData().incrementAchievements(bp, UHCAchievementList.UHC_LOOSER);
			
			if(bp.getBadblockMode() == BadblockMode.PLAYER)
				bp.getPlayerData().incrementStatistic("uhcspeed", UHCScoreboard.LOOSES);
		}

		if(badcoins > 20 * bp.getPlayerData().getBadcoinsMultiplier())
			badcoins = 20 * bp.getPlayerData().getBadcoinsMultiplier();
		if(xp > 50 * bp.getPlayerData().getXpMultiplier())
			xp = 50 * bp.getPlayerData().getXpMultiplier();
		
		int rbadcoins = badcoins < 2 ? 2 : (int) badcoins;
		int rxp		  = xp < 5 ? 5 : (int) xp;
		
		bp.getPlayerData().addBadcoins(rbadcoins, true);
		bp.getPlayerData().addXp(rxp, true);

		new BukkitRunnable(){

			@Override
			public void run(){
				if(bp.isOnline()){
					bp.sendTranslatedActionBar("uhcspeed.win", rbadcoins, rxp);
				}
			}

		}.runTaskTimer(GameAPI.getAPI(), 0, 30L);
	}
}
