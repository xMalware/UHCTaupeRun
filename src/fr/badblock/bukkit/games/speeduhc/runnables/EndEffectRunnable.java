package fr.badblock.bukkit.games.speeduhc.runnables;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.particles.ParticleData.BlockData;
import fr.badblock.gameapi.particles.ParticleEffect;
import fr.badblock.gameapi.particles.ParticleEffectType;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;

public class EndEffectRunnable extends BukkitRunnable {
	private Location 	 location;
	private BadblockTeam team;

	public EndEffectRunnable(Location location, BadblockTeam winner){
		this.location = location.getWorld().getHighestBlockAt(location).getLocation().clone().add(0, 1, 0);
		this.team	  = winner;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(){
		ParticleEffect effect = GameAPI.getAPI().createParticleEffect(ParticleEffectType.BLOCK_DUST);
		effect.setSpeed(2);
		effect.setLongDistance(true);
		effect.setAmount(100);
		if (team != null)
			effect.setData(new BlockData(Material.WOOL, team.getDyeColor().getWoolData()));
		else
			effect.setData(new BlockData(Material.WOOL, DyeColor.GREEN.getWoolData()));

		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bplayer = (BadblockPlayer) player;
			bplayer.sendParticle(location, effect);
		}
	}
}
