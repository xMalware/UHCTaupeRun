package fr.badblock.bukkit.games.speeduhc.result;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import fr.badblock.bukkit.games.speeduhc.PluginUHC;
import fr.badblock.bukkit.games.speeduhc.players.UHCData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.result.Result;
import fr.badblock.gameapi.game.result.ResultCategoryArray;
import fr.badblock.gameapi.game.result.ResultCategoryLined;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayerData;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.InGameKitData;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.utils.general.StringUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import lombok.Getter;

@Getter public class UHCResult extends Result {
	private transient BadblockPlayer player;

	private transient ResultCategoryArray players;
	private transient ResultCategoryArray teams;
	private transient ResultCategoryLined general;

	private transient boolean			  allowTeams;

	public UHCResult(BadblockPlayer player) {
		super(player.getTranslatedMessage("uhcspeed.result.title", player.getName())[0]);

		this.player 	= player;
		this.allowTeams = PluginUHC.getInstance().getConfiguration().allowTeams;

		general = registerCategory(CatNames.GENERAL.getName(), new ResultCategoryLined(
				get("uhcspeed.result.general.title")
				));

		if(allowTeams)
			teams = registerCategory(CatNames.TEAMS.getName(), new ResultCategoryArray(
					get("uhcspeed.result.teams.title"),
					new String[]{
							get("uhcspeed.result.teams.entry-players"),
							get("uhcspeed.result.teams.entry-score")
					}
					));

		players = registerCategory(CatNames.PLAYERS.getName(), new ResultCategoryArray(
				get("uhcspeed.result.players.title"),
				new String[]{
						get("uhcspeed.result.players.entry-score"),
						get("uhcspeed.result.players.entry-kills"),
						get("uhcspeed.result.players.entry-death"),
						get("survival.result.players.entry-gdamage"),
						get("survival.result.players.entry-rdamage"),
						get("uhcspeed.result.players.entry-rank"),
						get("uhcspeed.result.players.entry-kit")
				}
				));
	}

	public void doGeneral(int teams, int players){
		general.addLine(get("uhcspeed.result.general.entry-date"), GameAPI.getAPI().getGameServer().getGameBegin());
		general.addLine(get("uhcspeed.result.general.entry-server"), Bukkit.getServerName());
		
		if(allowTeams)
			general.addLine(get("uhcspeed.result.general.entry-teams"), Integer.toString(teams));
		general.addLine(get("uhcspeed.result.general.entry-players"), Integer.toString(players));
	}

	private transient int pos = 1;

	public void doTeamTop(Map<BadblockTeam, Integer> teams, BadblockTeam winner){
		teams.forEach((team, score) -> {
			String description = pos + " - " + team.getChatName().getAsLine(player);

			if(team.equals(winner)){
				description = pos + " - [img:winner.png] " + team.getChatName().getAsLine(player);
			}

			this.teams.addLine(description, StringUtils.join(team.getPlayersNameAtStart(), ", "), "" + score);
			pos++;
		});
	}

	public void doPlayersTop(List<BadblockPlayerData> players){
		int pos = 1;

		for(BadblockPlayerData player : players){
			String description = "[avatar:" + player.getName() + "] " + pos + " - " + player.getName();

			if(pos == 1){
				description += " [img:winner.png]";
			}

			UHCData  data = player.inGameData(UHCData.class);
			PlayerKit kit  = player.inGameData(InGameKitData.class).getChoosedKit();

			String    kitName = kit == null ? "-" : new TranslatableString("kits." + kit.getKitName() + ".itemdisplayname").getAsLine(this.player);

			this.players.addLine(description, "" + data.getScore(), "" + data.kills, "" + data.deathData(this.player), "" + data.givedDamage, "" + data.receivedDamage,
					player.getGroupPrefix().getAsLine(this.player), kitName);

			pos++;
		}
	}

	private String get(String key, Object... args){
		return player.getTranslatedMessage(key, args)[0];
	}

	public enum CatNames {
		GENERAL("general"),
		TEAMS("teams"),
		PLAYERS("players");

		@Getter
		private String name;

		CatNames(String name){
			this.name = name;
		}
	}
}
