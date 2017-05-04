package fr.badblock.bukkit.games.speeduhc.runnables.game;

import fr.badblock.bukkit.games.speeduhc.players.TimeProvider;

public class UHCTimeManager implements TimeProvider {

	@Override
	public String getId(int num) {
		if(!PvERunnable.pve)
			return "pve";
		
		if(!PvPRunnable.pvp)
			return "pvp";
		
		return "end";
	}

	@Override
	public int getTime(int num) {
		if(!PvERunnable.pve)
			return PvERunnable.ins == null ? 0 : PvERunnable.ins.time;
		
		if(!PvPRunnable.pvp)
			return PvPRunnable.ins == null ? 0 : PvPRunnable.ins.time;
		
		return GameRunnable.ins == null ? 0 : GameRunnable.ins.totalTime - GameRunnable.ins.pastTime;
	}

	@Override
	public int getProvidedCount() {
		return 1;
	}

	@Override
	public boolean displayed() {
		return true;
	}

}
