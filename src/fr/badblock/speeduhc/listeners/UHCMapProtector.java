package fr.badblock.speeduhc.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.servers.MapProtector;
import fr.badblock.speeduhc.runnables.game.PvERunnable;
import fr.badblock.speeduhc.runnables.game.PvPRunnable;

public class UHCMapProtector implements MapProtector {
	private boolean inGame(){
		return GameAPI.getAPI().getGameServer().getGameState() == GameState.RUNNING;
	}

	@Override
	public boolean blockPlace(BadblockPlayer player, Block block) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean blockBreak(BadblockPlayer player, Block block) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean modifyItemFrame(BadblockPlayer player, Entity itemFrame) {
		return player.hasAdminMode();
	}

	@Override
	public boolean canLostFood(BadblockPlayer player) {
		return inGame() && PvERunnable.pve;
	}

	@Override
	public boolean canUseBed(BadblockPlayer player, Block bed) {
		return false;
	}

	@Override
	
	public boolean canUsePortal(BadblockPlayer player) {
		return false;
	}

	@Override
	public boolean canDrop(BadblockPlayer player) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canPickup(BadblockPlayer player) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canFillBucket(BadblockPlayer player) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canEmptyBucket(BadblockPlayer player) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canInteract(BadblockPlayer player, Action action, Block block) {
		if(player.getItemInHand() != null && !PvPRunnable.pvp){
			switch(player.getItemInHand().getType()){
				case LAVA_BUCKET:
				case FLINT_AND_STEEL:
					return false;
				default: ;
			}
		}
		
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canInteractArmorStand(BadblockPlayer player, ArmorStand entity) {
		return inGame(); // sait on jamais :o
	}
	
	@Override
	public boolean canInteractEntity(BadblockPlayer player, Entity entity) {
		return true; // à priori rien à bloquer ... :o
	}

	@Override
	public boolean canEnchant(BadblockPlayer player, Block table) {
		return true;
	}

	@Override
	public boolean canBeingDamaged(BadblockPlayer player) {
		return inGame() && PvERunnable.pve;
	}

	@Override
	public boolean healOnJoin(BadblockPlayer player) {
		return !inGame();
	}

	@Override
	public boolean canBlockDamage(Block block) {
		return true;
	}

	@Override
	public boolean allowFire(Block block) {
		return false;
	}

	@Override
	public boolean allowMelting(Block block) {
		return false;
	}

	@Override
	public boolean allowBlockFormChange(Block block) {
		return true; //TODO test
	}

	@Override
	public boolean allowPistonMove(Block block) {
		return true;
	}

	@Override
	public boolean allowBlockPhysics(Block block) {
		return true;
	}

	@Override
	public boolean allowLeavesDecay(Block block) {
		return inGame();
	}

	@Override
	public boolean allowRaining() {
		return false;
	}

	@Override
	public boolean modifyItemFrame(Entity itemframe) {
		return false;
	}

	@Override
	public boolean canSoilChange(Block soil) {
		return true;
	}

	@Override
	public boolean canSpawn(Entity entity) {
		return inGame() || !(entity instanceof Item);
	}

	@Override
	public boolean canCreatureSpawn(Entity creature, boolean isPlugin) {
		return true;
	}

	@Override
	public boolean canItemSpawn(Item item) {
		return inGame();
	}

	@Override
	public boolean canItemDespawn(Item item) {
		return true;
	}

	@Override
	public boolean allowExplosion(Location location) {
		return inGame();
	}

	@Override
	public boolean allowInteract(Entity entity) {
		return true;
	}

	@Override
	public boolean canCombust(Entity entity) {
		return true;
	}

	@Override
	public boolean canEntityBeingDamaged(Entity entity) {
		return inGame() && ((entity.getType().equals(EntityType.PLAYER) && PvERunnable.pve) || !entity.getType().equals(EntityType.PLAYER));
	}

	@Override
	public boolean destroyArrow() {
		return true;
	}
	
	@Override
	public boolean canEntityBeingDamaged(Entity entity, BadblockPlayer badblockPlayer) {
		return inGame() && PvERunnable.pve;
	}

}
