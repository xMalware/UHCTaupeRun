package fr.badblock.bukkit.games.speeduhc.players;

import org.bukkit.Bukkit;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.scoreboard.BadblockScoreboardGenerator;
import fr.badblock.gameapi.players.scoreboard.CustomObjective;
import fr.badblock.gameapi.utils.BorderUtils;
import fr.badblock.gameapi.utils.BukkitUtils;

public class UHCScoreboard extends BadblockScoreboardGenerator {
	private static TimeProvider timeProvider;

	public static void setTimeProvider(TimeProvider provider){
		timeProvider = provider;

		BukkitUtils.forEachPlayers(player -> {
			if(player.getCustomObjective() == null){
				new UHCScoreboard(player);
			} else player.getCustomObjective().generate();
		});
	}

	public static final String WINS 	  = "wins",
			KILLS 	  = "kills",
			DEATHS 	  = "deaths",
			LOOSES 	  = "looses";

	private CustomObjective objective;
	private BadblockPlayer  player;

	public UHCScoreboard(BadblockPlayer player){
		this.objective = GameAPI.getAPI().buildCustomObjective("rush");
		this.player    = player;

		objective.showObjective(player);
		objective.setDisplayName("&b&o" + GameAPI.getGameName());
		objective.setGenerator(this);

		objective.generate();

		doBadblockFooter(objective);
		Bukkit.getScheduler().runTaskTimer(GameAPI.getAPI(), this::doTime, 0, 20L);
	}

	public int doTime(){
		int i = 14;

		if(timeProvider != null){
			for(int y=0;y<timeProvider.getProvidedCount();y++){
				String id = timeProvider.getId(y);
				int  time = timeProvider.getTime(y);

				if(id == null || time < 0 || !timeProvider.displayed()) {
					objective.removeLine(i);
					objective.removeLine(i - 1);
					i -= 2;
					continue;
				}	
				objective.changeLine(i, i18n("uhcspeed.scoreboard.time." + id));
				objective.changeLine(i - 1, i18n("uhcspeed.scoreboard.time", time(time)));

				i -= 2;
			}
		}

		objective.changeLine(i,  ""); i--;

		objective.changeLine(i,  i18n("uhcspeed.scoreboard.aliveplayers", alivePlayers())); i--;
		objective.changeLine(i,  i18n("uhcspeed.scoreboard.bordersize", "" + BorderUtils.getBorderSize() / 2)); i--;		

		return i;
	}


	@Override
	public void generate(){
		objective.changeLine(15, "&8&m----------------------");

		int i = doTime();

		if(player.getBadblockMode() != BadblockMode.SPECTATOR){
			objective.changeLine(i,  ""); i--;

			objective.changeLine(i,  i18n("uhcspeed.scoreboard.wins", stat(WINS))); i--;
			objective.changeLine(i,  i18n("uhcspeed.scoreboard.kills", stat(KILLS))); i--;
			objective.changeLine(i,  i18n("uhcspeed.scoreboard.deaths", stat(DEATHS))); i--;
		}

		for(int a=3;a<=i;a++)
			objective.removeLine(a);

		objective.changeLine(2,  "&8&m----------------------");
	}

	private int alivePlayers(){
		return (int) GameAPI.getAPI().getOnlinePlayers().stream().filter(p -> !p.inGameData(UHCData.class).isDeath()).count();
	}

	private int stat(String name){
		return (int) player.getPlayerData().getStatistics("uhcspeed", name);
	}

	private String time(int time){
		String res = "m";
		int    sec = time % 60;

		res = (time / 60) + res;
		if(sec < 10){
			res += "0";
		}

		return res + sec + "s";
	}

	private String i18n(String key, Object... args){
		return GameAPI.i18n().get(player.getPlayerData().getLocale(), key, args)[0];
	}

}
