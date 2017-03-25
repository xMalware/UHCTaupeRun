package fr.badblock.bukkit.games.speeduhc.runnables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.speeduhc.PluginUHC;
import fr.badblock.bukkit.games.speeduhc.UHCAchievementList;
import fr.badblock.bukkit.games.speeduhc.configuration.UHCConfiguration;
import fr.badblock.bukkit.games.speeduhc.runnables.game.GameRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.InGameKitData;
import fr.badblock.gameapi.players.kits.PlayerKit;
import lombok.AllArgsConstructor;

public class TeleportRunnable extends BukkitRunnable {
	private List<TeleportableEntity> toTeleport;
	
	public TeleportRunnable(){
		GameAPI.getAPI().getGameServer().setGameState(GameState.RUNNING);
		GameAPI.getAPI().getGameServer().saveTeamsAndPlayersForResult();

		Bukkit.getWorlds().forEach(world -> {
			world.getEntities().forEach(entity -> {
				if(entity.getType() != EntityType.PLAYER)
					entity.remove();
			});
		});

		for(BadblockPlayer p : GameAPI.getAPI().getOnlinePlayers()){
			boolean good = true;


			for(PlayerKit toUnlock : PluginUHC.getInstance().getKits().values()){
				if(!toUnlock.isVIP()){
					if(p.getPlayerData().getUnlockedKitLevel(toUnlock) < 2){
						good = false; break;
					}
				}
			}

			if(good && !p.getPlayerData().getAchievementState(UHCAchievementList.UHC_ALLKITS).isSucceeds()){
				p.getPlayerData().getAchievementState(UHCAchievementList.UHC_ALLKITS).succeed();
				UHCAchievementList.UHC_ALLKITS.reward(p);
			}

			PlayerKit kit = p.inGameData(InGameKitData.class).getChoosedKit();

			if(kit != null){
				kit.giveKit(p);
			} else {
				p.clearInventory();
			}
		}

		GameAPI.getAPI().getJoinItems().doClearInventory(false);
		GameAPI.getAPI().getJoinItems().end();
		
		UHCConfiguration config = PluginUHC.getInstance().getConfiguration();
		
		if(config.allowTeams)
			toTeleport = GameAPI.getAPI().getTeams().stream().map(team -> new TeleportableEntity(team.getOnlinePlayers(), null)).collect(Collectors.toList());
		else toTeleport = GameAPI.getAPI().getOnlinePlayers().stream().map(player -> new TeleportableEntity(Arrays.asList(player), null)).collect(Collectors.toList());
	
		toTeleport = generateLocations(toTeleport, config.map.overworldSize);
	}
	
	@Override
	public void run() {
		for(int i=0;i<2;i++){
			if(toTeleport.isEmpty())
				break;
			
			TeleportableEntity entity = toTeleport.remove(0);
			entity.players.stream().filter(player -> player.isOnline() && player.isValid()).forEach(player -> player.teleport(entity.loc));	
		}
		
		try {
			Thread.sleep(10L);
		} catch (InterruptedException e){}
		
		if(toTeleport.isEmpty()){
			cancel();
			PluginUHC.getInstance().removeSpawn();
			
			if(PluginUHC.getInstance().getConfiguration().allowTeams)
				new FriendsActionBarRunnable().runTaskTimer(GameAPI.getAPI(), 0, 10L);
			
			StartRunnable.gameTask = new GameRunnable();
			StartRunnable.gameTask.runTaskTimer(GameAPI.getAPI(), 0, 20L);
		}
	}
	
	public static List<TeleportableEntity> generateLocations(List<TeleportableEntity> teleportables, int size){
		Random random = new Random();
		List<Location> used = new ArrayList<Location>();

		int dsize = size * 2;
		
		teleportables.forEach(teleportable -> {
			boolean good = false;
			Location loc = null;
			
			while(!good){
				loc = new Location(Bukkit.getWorlds().get(0), random.nextInt(dsize) - size, 256, random.nextInt(dsize) - size);
				good = true;
				for(Location u : used){
					if(u.distance(loc) < 75.0d){
						good = false;
					}
				}
			}
			
			used.add(loc);
			teleportable.loc = loc;
		});
		
		return teleportables;
	}

	@AllArgsConstructor
	class TeleportableEntity {
		Collection<BadblockPlayer> players = new ArrayList<>();
		Location 			 	   loc	   = null;
	}
}
