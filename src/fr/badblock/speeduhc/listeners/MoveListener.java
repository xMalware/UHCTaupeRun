package fr.badblock.speeduhc.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.badblock.gameapi.BadListener;
import fr.badblock.speeduhc.PluginUHC;

public class MoveListener extends BadListener {
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(e.getTo().getY() <= 95d && beforeGame()){
			Location spawn = PluginUHC.getInstance().getConfiguration().spawn.getHandle();
			
			Entity vehicle = null;
			
			if(e.getPlayer().isInsideVehicle()){
				vehicle = e.getPlayer().getVehicle();
				vehicle.eject();
				vehicle.teleport(spawn);
			}
			
			e.setCancelled(true);
			e.getPlayer().teleport(spawn);
			
			if(vehicle != null)
				vehicle.setPassenger(e.getPlayer());
		}
	}
}
