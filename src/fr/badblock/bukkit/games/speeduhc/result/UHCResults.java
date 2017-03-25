package fr.badblock.bukkit.games.speeduhc.result;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.google.common.collect.Maps;

import fr.badblock.bukkit.games.speeduhc.players.UHCData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayerData;
import fr.badblock.gameapi.players.BadblockTeam;

public class UHCResults {
	public UHCResults(BadblockTeam winner, BadblockPlayer winnerPlayer){
		Collection<BadblockPlayerData> data  = GameAPI.getAPI().getGameServer().getSavedPlayers();
		Collection<BadblockTeam>	   teams = GameAPI.getAPI().getGameServer().getSavedTeams();
	
		List<BadblockPlayerData> inOrderPlayers = data.stream().sorted((a, b) -> {
			return Integer.compare( b.inGameData(UHCData.class).getScore(), a.inGameData(UHCData.class).getScore() );
		}).collect(Collectors.toList());
		
		Map<BadblockTeam, Integer> scoredTeams = Maps.newConcurrentMap();
		
		for(BadblockTeam team : teams){
			int score = 0;
			
			for(UUID uniqueId : team.getPlayersAtStart()){
				score += getScore(uniqueId, team, inOrderPlayers);	
			}
			
			scoredTeams.put(team, score);
		}
		
		Map<BadblockTeam, Integer> result = new LinkedHashMap<>();
		
		scoredTeams.entrySet().stream().sorted((a, b) -> {
			return a.getValue() < b.getValue() ? 1 : -1;
		}).forEach(team -> result.put(team.getKey(), team.getValue()));

		for(BadblockPlayerData playerData : inOrderPlayers){
			BadblockPlayer player = (BadblockPlayer) Bukkit.getPlayer(playerData.getUniqueId());
			
			if(player != null){
				UHCResult rushResult = new UHCResult(player);
				rushResult.doPlayersTop(inOrderPlayers);
				
				if(rushResult.isAllowTeams())
					rushResult.doTeamTop(result, winner);
				
				rushResult.doGeneral(teams.size(), inOrderPlayers.size());
				player.postResult(rushResult);
			}
		}
		
	}
	
	public int getScore(UUID player, BadblockTeam team, Collection<BadblockPlayerData> in){
		for(BadblockPlayerData p : in)
			if(p.getUniqueId().equals(player)){
				if(p.getTeam() == null)
					p.setTeam(team);
				return p.inGameData(UHCData.class).getScore();
			}
		
		return 0;
	}
}
