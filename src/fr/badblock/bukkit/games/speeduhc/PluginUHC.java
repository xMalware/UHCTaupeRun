package fr.badblock.bukkit.games.speeduhc;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.badblock.bukkit.games.speeduhc.commands.GameCommand;
import fr.badblock.bukkit.games.speeduhc.commands.UHCCommand;
import fr.badblock.bukkit.games.speeduhc.configuration.UHCConfiguration;
import fr.badblock.bukkit.games.speeduhc.listeners.CraftListener;
import fr.badblock.bukkit.games.speeduhc.listeners.DamageListener;
import fr.badblock.bukkit.games.speeduhc.listeners.DeathListener;
import fr.badblock.bukkit.games.speeduhc.listeners.JoinListener;
import fr.badblock.bukkit.games.speeduhc.listeners.MoveListener;
import fr.badblock.bukkit.games.speeduhc.listeners.PartyJoinListener;
import fr.badblock.bukkit.games.speeduhc.listeners.QuitListener;
import fr.badblock.bukkit.games.speeduhc.listeners.UHCMapProtector;
import fr.badblock.bukkit.games.speeduhc.runnables.PreStartRunnable;
import fr.badblock.gameapi.BadblockPlugin;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.game.GameServer.WhileRunningConnectionTypes;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.run.BadblockGameData;
import fr.badblock.gameapi.run.RunType;
import fr.badblock.gameapi.utils.BorderUtils;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.GameRules;
import fr.badblock.gameapi.utils.general.JsonUtils;
import lombok.Getter;
import lombok.Setter;

public class PluginUHC extends BadblockPlugin {
	@Getter private static PluginUHC instance;

	public static 	     File   MAP;

	private static final String CONFIG 		   		   = "config.json";
	private static final String TEAMS_CONFIG 		   = "teams.yml";
	private static final String TEAMS_CONFIG_INVENTORY = "teamsInventory.yml";
	private static final String KITS_CONFIG_INVENTORY  = "kitInventory.yml";

	@Getter@Setter
	private int 			     maxPlayers;
	@Getter
	private UHCConfiguration    configuration;

	@Getter
	private Map<String, PlayerKit> kits;

	@Getter
	private Location defaultLoc;
	
	@Override
	public void onEnable(RunType runType){
		AchievementList list = UHCAchievementList.instance;
		
		BadblockGame.UHCSPEED.setGameData(new BadblockGameData() {
			@Override
			public AchievementList getAchievements() {
				return list;
			}
		});

		instance = this;
		
		if(runType == RunType.LOBBY)
			return;

		this.defaultLoc = new Location(Bukkit.getWorlds().get(0), 0, 90, 0);
		
		try {
			if(!getDataFolder().exists()) getDataFolder().mkdir();

			/**
			 * Chargement de la configuration du jeu
			 */

			// Modification des GameRules
			GameRules.doDaylightCycle.setGameRule(false);
			GameRules.spectatorsGenerateChunks.setGameRule(false);
			GameRules.doFireTick.setGameRule(false);

			// Lecture de la configuration du jeu

			BadblockGame.UHCSPEED.use();

			File configFile    = new File(getDataFolder(), CONFIG);
			this.configuration = JsonUtils.load(configFile, UHCConfiguration.class);

			JsonUtils.save(configFile, configuration, true);

			if(configuration.allowTeams){

				File 			  teamsFile 	= new File(getDataFolder(), TEAMS_CONFIG);
				FileConfiguration teams 		= YamlConfiguration.loadConfiguration(teamsFile);

				getAPI().registerTeams(configuration.maxPlayersInTeam, teams);
			
				try { teams.save(teamsFile); } catch (IOException unused){}
			}

			getAPI().setDefaultKitContentManager(true);

			maxPlayers = !configuration.allowTeams ? configuration.maxPlayersInTeam : getAPI().getTeams().size() * configuration.maxPlayersInTeam;
			try {
				BukkitUtils.setMaxPlayers(maxPlayers);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			kits	   = getAPI().loadKits(GameAPI.getInternalGameName());

			// Chargement des fonctionnalités de l'API non utilisées par défaut

			getAPI().getBadblockScoreboard().doBelowNameHealth();
			getAPI().getBadblockScoreboard().doTabListHealth();
			if(configuration.allowTeams)
				getAPI().getBadblockScoreboard().doTeamsPrefix();
			else getAPI().getBadblockScoreboard().doGroupsPrefix();;
			
			getAPI().getBadblockScoreboard().doOnDamageHologram();

			getAPI().getJoinItems().registerKitItem(0, kits, new File(getDataFolder(), KITS_CONFIG_INVENTORY));
			
			getAPI().formatChat(true, true);
			
			if(configuration.allowTeams)
				getAPI().getJoinItems().registerTeamItem(3, new File(getDataFolder(), TEAMS_CONFIG_INVENTORY));
			getAPI().getJoinItems().registerAchievementsItem(configuration.allowTeams ? 5 : 4, BadblockGame.UHCSPEED);
			getAPI().getJoinItems().registerLeaveItem(8, configuration.fallbackServer);

			getAPI().setMapProtector(new UHCMapProtector());
			getAPI().enableAntiSpawnKill();

			getAPI().getGameServer().whileRunningConnection(WhileRunningConnectionTypes.SPECTATOR);

			new MoveListener();
			new DeathListener();
			new JoinListener();
			new QuitListener();
			new PartyJoinListener();
			new CraftListener();
			new DamageListener();
			new QuitListener();

			new PreStartRunnable().runTaskTimer(GameAPI.getAPI(), 0, 30L);
			new GameCommand();
			new UHCCommand();

			BorderUtils.setBorder(configuration.map.overworldSize);
			
			if(configuration.map.manageNether)
				BorderUtils.setBorder(configuration.getNether(), configuration.map.netherSize);
			
			Bukkit.getWorlds().forEach(world -> {
				world.setTime(2000L);
				world.getEntities().forEach(entity -> entity.remove());
			});
		} catch(Throwable e){
			e.printStackTrace();
		}
	}

	public void saveJsonConfig(){
		File configFile = new File(getDataFolder(), CONFIG);
		JsonUtils.save(configFile, configuration, true);
	}
	
	
	public void removeSpawn(){
		getConfiguration().spawnZone.getHandle().getBlocks().forEach(block -> block.setType(Material.AIR));
		
	}
}
