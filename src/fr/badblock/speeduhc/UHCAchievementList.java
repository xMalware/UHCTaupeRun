package fr.badblock.speeduhc;

import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.run.BadblockGame;

public class UHCAchievementList {
	public static AchievementList instance = new AchievementList(BadblockGame.UHCSPEED);
	
	/*
	 * Tuer X personnes
	 */
	public static final PlayerAchievement UHC_KILL_1 = instance.addAchievement(new PlayerAchievement("uhcspeed_kill_1", 10, 5, 10));
	public static final PlayerAchievement UHC_KILL_2 = instance.addAchievement(new PlayerAchievement("uhcspeed_kill_2", 50, 25, 100));
	public static final PlayerAchievement UHC_KILL_3 = instance.addAchievement(new PlayerAchievement("uhcspeed_kill_3", 250, 100, 1000));
	public static final PlayerAchievement UHC_KILL_4 = instance.addAchievement(new PlayerAchievement("uhcspeed_kill_4", 500, 250, 5000));
	
	/*
	 * Casser X lits
	 */
	public static final PlayerAchievement UHC_SURVI_1  = instance.addAchievement(new PlayerAchievement("uhcspeed_survi_1", 10, 5, 10));
	public static final PlayerAchievement UHC_SURVI_2  = instance.addAchievement(new PlayerAchievement("uhcspeed_survi_2", 50, 25, 100));
	public static final PlayerAchievement UHC_SURVI_3  = instance.addAchievement(new PlayerAchievement("uhcspeed_survi_3", 250, 100, 1000));
	public static final PlayerAchievement UHC_SURVI_4  = instance.addAchievement(new PlayerAchievement("uhcspeed_survi_4", 500, 250, 5000));

	/*
	 * Gagner X parties
	 */
	public static final PlayerAchievement UHC_WIN_1  = instance.addAchievement(new PlayerAchievement("uhcspeed_win_1", 10, 2, 10));
	public static final PlayerAchievement UHC_WIN_2  = instance.addAchievement(new PlayerAchievement("uhcspeed_win_2", 50, 25, 100));
	public static final PlayerAchievement UHC_WIN_3  = instance.addAchievement(new PlayerAchievement("uhcspeed_win_3", 250, 100, 1000));
	public static final PlayerAchievement UHC_WIN_4  = instance.addAchievement(new PlayerAchievement("uhcspeed_win_4", 500, 250, 5000));

	/*
	 * Gagner X parties avec 5 kills ou +
	 */
	public static final PlayerAchievement UHC_BKILL_1  = instance.addAchievement(new PlayerAchievement("uhcspeed_bkill_1", 50, 25, 10));
	public static final PlayerAchievement UHC_BKILL_2  = instance.addAchievement(new PlayerAchievement("uhcspeed_bkill_2", 250, 125, 100));
	public static final PlayerAchievement UHC_BKILL_3  = instance.addAchievement(new PlayerAchievement("uhcspeed_bkill_3", 400, 200, 1000));
	public static final PlayerAchievement UHC_BKILL_4  = instance.addAchievement(new PlayerAchievement("uhcspeed_bkill_4", 600, 350, 5000));
	
	/**
	 * Perdre 5000 parties
	 */
	public static final PlayerAchievement UHC_LOOSER = instance.addAchievement(new PlayerAchievement("uhcspeed_looser", 300, 100, 5000));
	
	/*
	 * Tuer 9 joueurs dans une même partie
	 */
	public static final PlayerAchievement UHC_KILLER = instance.addAchievement(new PlayerAchievement("uhcspeed_killer", 300, 100, 9, true));

	/*
	 * Tuer 3 joueurs à l'arc dans une même partie
	 */
	public static final PlayerAchievement UHC_SHOOTER = instance.addAchievement(new PlayerAchievement("uhcspeed_shooter", 100, 50, 3, true));
	
	/*
	 * Tuer 6 joueurs à l'arc dans une même partie
	 */
	public static final PlayerAchievement UHC_USHOOTER = instance.addAchievement(new PlayerAchievement("uhcspeed_ushooter", 300, 100, 6, true));
	
	/*
	 * Tuer 6 joueurs à l'arc dans une même partie
	 */
	public static final PlayerAchievement UHC_BSURVIVOR = instance.addAchievement(new PlayerAchievement("uhcspeed_bsurvivor", 300, 100, 1000, true));
	
	/**
	 * Forger une épée en diamant
	 */
	public static final PlayerAchievement UHC_FORGERON = instance.addAchievement(new PlayerAchievement("uhcspeed_forgeron", 300, 100, 1, true));
	
	/**
	 * Frabriquer une cânne à pêche
	 */
	public static final PlayerAchievement UHC_ARTISAN_FISHER = instance.addAchievement(new PlayerAchievement("uhcspeed_artisan_fisher", 100, 50, 1, true));

	/**
	 * Ne prenez pas de dégats au cours d'une partie
	 */
	public static final PlayerAchievement UHC_HALF_GOD = instance.addAchievement(new PlayerAchievement("uhcspeed_half_god", 300, 100, 1, true));

	/**
	 * Exploser 3 lits dans une même partie
	 */
	public static final PlayerAchievement UHC_ALLKITS = instance.addAchievement(new PlayerAchievement("uhcspeed_allkits", 300, 150, 3, true));
}
