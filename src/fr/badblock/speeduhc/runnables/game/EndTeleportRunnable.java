package fr.badblock.speeduhc.runnables.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BorderUtils;
import fr.badblock.speeduhc.PluginUHC;
import fr.badblock.speeduhc.configuration.UHCConfiguration;
import lombok.AllArgsConstructor;

public class EndTeleportRunnable extends BukkitRunnable {
	private List<TeleportableEntity> toTeleport;
	
	public EndTeleportRunnable(){
		UHCConfiguration config = PluginUHC.getInstance().getConfiguration();
		
		if(config.allowTeams)
			toTeleport = GameAPI.getAPI().getTeams().stream().map(team -> new TeleportableEntity(team.getOnlinePlayers(), null)).collect(Collectors.toList());
		else toTeleport = GameAPI.getAPI().getRealOnlinePlayers().stream().map(player -> new TeleportableEntity(Arrays.asList(player), null)).collect(Collectors.toList());
	
		toTeleport = generateLocations(toTeleport, 200);
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
			
			GameRunnable.ins.enabled = false;
			UHCConfiguration conf = PluginUHC.getInstance().getConfiguration();
			
			int rest = GameRunnable.ins.totalTime - GameRunnable.ins.pastTime;
			BorderUtils.setBorder(conf.map.overworldSizeAfterTp, 0);
			BorderUtils.setBorder(5, rest - 30);
			
			if(conf.map.manageNether)
				BorderUtils.setBorder(conf.getNether(), 0);
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