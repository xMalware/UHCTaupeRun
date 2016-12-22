package fr.badblock.speeduhc.configuration;

import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapSelection;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UHCConfiguration {
	public String 	   		   fallbackServer    = "lobby";
	public int    	   		   maxPlayersInTeam  = 4;
	public boolean			   allowTeams		 = true;
	public MapLocation 		   spawn;
	public int				   mapSize			 = 500;
	public MapSelection		   spawnZone		 = new MapSelection();
	
	public int				   minPlayers		 = 10;
	
	public boolean			   teleportAtPrepEnd = true;
	public int				   totalTime		 = 35;
}
