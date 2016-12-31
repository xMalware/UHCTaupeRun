package fr.badblock.speeduhc.runnables.game;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BorderUtils;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.speeduhc.PluginUHC;
import fr.badblock.speeduhc.configuration.UHCConfiguration;
import fr.badblock.speeduhc.players.TimeProvider;
import fr.badblock.speeduhc.players.UHCData;
import fr.badblock.speeduhc.runnables.EndEffectRunnable;
import fr.badblock.speeduhc.runnables.KickRunnable;

public class GameRunnable extends BukkitRunnable implements TimeProvider {
	public static GameRunnable ins;
	public static boolean forceEnd = false;

	public int pastTime;
	public int totalTime;
	boolean enabled = true;

	public GameRunnable(){
		ins = this;

		this.totalTime = PluginUHC.getInstance().getConfiguration().time.totalTime * 60;
		this.pastTime  = 0;
	}

	private int countEntities(){
		List<BadblockTeam> to = GameAPI.getAPI().getTeams().stream().filter(team -> team.getOnlinePlayers().isEmpty()).collect(Collectors.toList());

		for(BadblockTeam team : to){
			GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);

			new TranslatableString("uhcspeed.team-loose", team.getChatName()).broadcast();;
			GameAPI.getAPI().unregisterTeam(team);
		}

		if (forceEnd) return 0;

		if(PluginUHC.getInstance().getConfiguration().allowTeams){
			return (int) GameAPI.getAPI().getTeams().parallelStream().filter(team -> team.getOnlinePlayers().size() > 0).count();
		} else {
			return GameAPI.getAPI().getRealOnlinePlayers().size();
		}
	}

	private BadblockTeam getTeam(){
		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			if(team.getOnlinePlayers().size() == 0)
				continue;

			return team;
		}

		return null;
	}

	private BadblockPlayer getPlayer(){
		for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
			if(player.getBadblockMode() == BadblockMode.SPECTATOR)
				continue;

			return player;
		}

		return null;
	}

	private void doEnd(){
		int entities = countEntities();

		if(entities == 0){
			Bukkit.shutdown();
			return;
		}

		BadblockTeam   winner 		= getTeam();
		BadblockPlayer winnerPlayer = winner == null ? getPlayer() : null;
		if (winnerPlayer != null) {
			winnerPlayer.getPlayerData().addRankedPoints(3);
		}

		GameAPI.getAPI().getGameServer().setGameState(GameState.FINISHED);
		Location winnerLocation = PluginUHC.getInstance().getDefaultLoc();
		Location looserLocation = winnerLocation.clone().add(0d, 7d, 0d);

		for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
			try {
				BadblockPlayer bp = (BadblockPlayer) player;
				bp.heal();
				bp.clearInventory();
				bp.setInvulnerable(true);

				bp.inGameData(UHCData.class).doReward(bp, winner, winnerPlayer, winnerLocation, looserLocation);
			} catch(Exception e){
				e.printStackTrace();
			}
		}

		try {
			//new UHCResults(winner, winnerPlayer);
			new EndEffectRunnable(winnerLocation, winner).runTaskTimer(GameAPI.getAPI(), 0, 1L);
		} catch(Exception e){
			e.printStackTrace();
		}

		new KickRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);
	}

	@Override
	public void run() {
		UHCConfiguration conf = PluginUHC.getInstance().getConfiguration();

		if(pastTime == 0)
			new PvERunnable(1).runTaskTimer(GameAPI.getAPI(), 0, 20L);
		if(pastTime == conf.time.pveTime * 60) {
			new PvPRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);
		}if(pastTime == conf.time.prepTime * 60){
			if(conf.time.teleportAtPrepEnd){
				new EndTeleportRunnable().runTaskTimer(GameAPI.getAPI(), 0, 5L);
				new PvERunnable(4).runTaskTimer(GameAPI.getAPI(), 0, 20L);
			} else {
				BorderUtils.setBorder(5, totalTime - pastTime - 30);

				if(conf.manageNether)
					BorderUtils.setBorder(0, totalTime - pastTime - 30, conf.getNether());
			}
		}
		

		if (totalTime - pastTime == 0)
			if(PluginUHC.getInstance().getConfiguration().allowTeams)
				GameAPI.getAPI().getTeams().stream().forEach(team -> team.getOnlinePlayers().forEach(player -> player.playSound(Sound.AMBIENCE_CAVE)));
			else GameAPI.getAPI().getRealOnlinePlayers().stream().forEach(player -> player.playSound(Sound.AMBIENCE_CAVE));
		else if (totalTime - pastTime < 0) {
			if(PluginUHC.getInstance().getConfiguration().allowTeams)
				GameAPI.getAPI().getTeams().stream().forEach(team -> team.getOnlinePlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 86400 * 20, 1))));
			else GameAPI.getAPI().getRealOnlinePlayers().stream().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 86400 * 20, 1)));
		}

		if(countEntities() <= 1){
			doEnd();
			cancel();
		}

		pastTime++;
		for (BadblockPlayer bp : BukkitUtils.getPlayers())
		if (bp.getCustomObjective() != null)
			bp.getCustomObjective().generate();
	}

	@Override
	public String getId(int num) {
		return "deathmatch";
	}

	@Override
	public int getTime(int num) {
		return totalTime - pastTime;
	}

	@Override
	public int getProvidedCount() {
		return 1;
	}

	@Override
	public boolean displayed() {
		return enabled;
	}
}