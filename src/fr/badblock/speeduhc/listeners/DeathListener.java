package fr.badblock.speeduhc.listeners;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.fakedeaths.FakeDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent.FightingDeaths;
import fr.badblock.gameapi.events.fakedeaths.NormalDeathEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import fr.badblock.speeduhc.PluginUHC;
import fr.badblock.speeduhc.UHCAchievementList;
import fr.badblock.speeduhc.players.UHCData;
import fr.badblock.speeduhc.players.UHCScoreboard;

public class DeathListener extends BadListener {
	@EventHandler
	public void onDeath(NormalDeathEvent e){
		death(e, e.getPlayer(), null, e.getLastDamageCause());
		e.setDeathMessage(GameMessages.deathEventMessage(e));
	}

	@EventHandler
	public void onDeath(FightingDeathEvent e){
		death(e, e.getPlayer(), e.getKiller(), e.getLastDamageCause());
		e.setDeathMessage(GameMessages.deathEventMessage(e));

		if(e.getKiller().getType() == EntityType.PLAYER){
			BadblockPlayer killer = (BadblockPlayer) e.getKiller();
			killer.getPlayerData().incrementAchievements(killer, UHCAchievementList.UHC_KILL_1, UHCAchievementList.UHC_KILL_2, UHCAchievementList.UHC_KILL_3, UHCAchievementList.UHC_KILL_4, UHCAchievementList.UHC_KILLER);

			if(e.getFightType() == FightingDeaths.BOW){
				killer.getPlayerData().incrementAchievements(killer, UHCAchievementList.UHC_SHOOTER, UHCAchievementList.UHC_USHOOTER);
			}
		}
	}

	private void death(FakeDeathEvent e, BadblockPlayer player, Entity killer, DamageCause last){
		Location respawnPlace = null;

		player.getPlayerData().incrementStatistic("uhcspeed", UHCScoreboard.DEATHS);
		player.inGameData(UHCData.class).killed(player, killer, last);
		player.getCustomObjective().generate();

		player.getPlayerData().incrementStatistic("uhcspeed", UHCScoreboard.LOOSES);
		BadblockTeam team = player.getTeam();

		player.sendTranslatedTitle("uhcspeed.player-loose-title");
		player.sendTimings(20, 80, 20);
		e.setLightning(true);
		
		if(team != null){

			e.setDeathMessageEnd(new TranslatableString("uhcspeed.player-loose-team", player.getName(), team.getChatName()));

			team.leaveTeam(player);

			if(team.getOnlinePlayers().size() == 0){
				GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
				GameAPI.getAPI().unregisterTeam(team);

				new TranslatableString("uhcspeed.team-loose", team.getChatName()).broadcast();;
			}
			
		} else {
			e.setDeathMessageEnd(new TranslatableString("uhcspeed.player-loose", player.getName()));
		}
		
		GameAPI.getAPI().getOnlinePlayers().forEach(p -> p.playSound(Sound.WITHER_SPAWN));
		
		player.setBadblockMode(BadblockMode.SPECTATOR);
		e.setTimeBeforeRespawn(0);

		if(killer == null){
			respawnPlace = PluginUHC.getInstance().getDefaultLoc();
		} else {
			respawnPlace = killer.getLocation();
		}

		if(killer != null && killer.getType() == EntityType.PLAYER){
			BadblockPlayer bKiller = (BadblockPlayer) killer;
			bKiller.getPlayerData().incrementStatistic("uhcspeed", UHCScoreboard.KILLS);
			bKiller.inGameData(UHCData.class).kills++;

			bKiller.getCustomObjective().generate();
		}

		player.getCustomObjective().generate();
		e.setRespawnPlace(respawnPlace);
	}
}
