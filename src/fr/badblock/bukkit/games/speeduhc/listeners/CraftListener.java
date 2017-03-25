package fr.badblock.bukkit.games.speeduhc.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.badblock.bukkit.games.speeduhc.UHCAchievementList;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;

public class CraftListener extends BadListener {
	@EventHandler
	public void onCraft(InventoryClickEvent e){
		if(inGame() && ItemStackUtils.isValid(e.getCurrentItem()) && e.getWhoClicked().getType() == EntityType.PLAYER){
			BadblockPlayer player = (BadblockPlayer) e.getWhoClicked();
			
			if(e.getCurrentItem().getType() == Material.DIAMOND_SWORD){
				player.getPlayerData().incrementAchievements(player, UHCAchievementList.UHC_FORGERON);
			} else if(e.getCurrentItem().getType() == Material.FISHING_ROD){
				player.getPlayerData().incrementAchievements(player, UHCAchievementList.UHC_ARTISAN_FISHER);
			}
		}
	}
}
