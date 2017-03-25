package fr.badblock.bukkit.games.speeduhc.configuration;

import org.bukkit.Bukkit;
import org.bukkit.World;

import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapSelection;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UHCConfiguration {
	public String 	   		   fallbackServer    = "lobby";
	public int    	   		   maxPlayersInTeam  = 4;
	public boolean			   allowTeams		 = true;
	public MapConfig		   map				 = new MapConfig();
	public TimeConfig		   time				 = new TimeConfig();
	public MapLocation 		   spawn;
	public MapSelection		   spawnZone		 = new MapSelection();
	
	public int				   minPlayers		 = 10;
	public boolean			   manageNether		 = false;

	public World getNether(){
		return Bukkit.getWorld( Bukkit.getWorlds().get(0).getName() + "_nether" );
	}
	
	public class TimeConfig {
		public boolean			   teleportAtPrepEnd = true;
		public int				   totalTime		 = 35;
		public int				   pveTime			 = 1;
		public int				   pvpTime			 = 20;
		public int				   prepTime			 = 15;
	}
	
	public class MapConfig {
		public int     overworldSize 		= 500;
		public int     overworldSizeAfterTp	= 200;
		public int     netherSize			= 150;
		public boolean manageNether 	    = false;
	}
}
