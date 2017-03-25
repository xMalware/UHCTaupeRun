package fr.badblock.bukkit.games.speeduhc.runnables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.general.MathsUtils;
import fr.badblock.gameapi.utils.general.StringUtils;
import fr.badblock.gameapi.utils.selections.Vector3f;

public class FriendsActionBarRunnable extends BukkitRunnable {
	@Override
	public void run() {
		for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
			if(player.getTeam() != null)
				doPlayer(player);
		}
	}
	
	private void doPlayer(BadblockPlayer player){
		List<String> parts = new ArrayList<>();
		
		for(BadblockPlayer p : player.getTeam().getOnlinePlayers()){
			if(p.equals(player)) continue;
			
			Vector3f vector  = new Vector3f(p.getLocation()).sub( new Vector3f(player.getLocation()) ).setY(0);
			Vector   vector2 = player.getEyeLocation().getDirection().setY(0);
			
			double distance = MathsUtils.round(vector.length(), 1);
			double angle    = Math.toDegrees(Math.atan2(vector2.getZ(), vector2.getX()) - Math.atan2(vector.getZ(), vector.getX()));
			
			parts.add("&7" + p.getName() + " : &b" + distance + "&7d &c" + Directions.matchDirection(angle).car);
		}
		
		if(!parts.isEmpty())
			player.sendActionBar(StringUtils.join(parts, " &b| "));
	}

	enum Directions {
		BACKWARD_RIGHT(-157.5, -112.5, "\u2198"),
		BACKWARD_1(157.5, 180.0, "\u2193"),
		BACKWARD_2(-180.0, -157.5, "\u2193"),
		BACKWARD_LEFT(112.5, 157.5, "\u2199"),
		LEFT(67.5, 112.5, "\u2190"),
		FORWARD_LEFT(67.5, 22.5, "\u2196"),
		FORWARD(-22.5, 22.5, "\u2191"),
		FORWARD_RIGHT(-77.5, -22.5, "\u2197"),
		RIGHT(-112.5, -67.5, "\u2192");
		
		private double x, y;
		private String car;
		
		Directions(double x, double y, String car){
			this.x = Math.min(x, y);
			this.y = Math.max(x, y);
		
			this.car = car;
		}
		
		public boolean isIn(double angle){
			return angle >= x && angle <= y;
		}
		
		public static Directions matchDirection(double angle){
			for(Directions dir : values())
				if(dir.isIn(angle))
					return dir;
			
			if(angle < 360)
				return BACKWARD_1;
			
			return FORWARD;
		}
	}
}
