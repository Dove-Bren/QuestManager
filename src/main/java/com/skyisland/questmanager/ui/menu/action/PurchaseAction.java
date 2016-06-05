package com.skyisland.questmanager.ui.menu.action;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.player.QuestPlayer;

/**
 * The action of purchasing an item or service from an NPC.
 * This event specifically details purchases done from within an InventoryMenu, where it has
 * an ItemStack to give to the player
 * @author Skyler
 *
 */
public class PurchaseAction implements MenuAction {
	
	private int cost;
	
	private int fameCheck;
	
	private ItemStack item;
	
	private QuestPlayer player;
	
	private static final String denialFame = "Not famous enough!";
	
	private static final String denialMoney = "Not enough money!";
	
	private static final String denialSpace = "Not enough room in your inventory!";
	
	public PurchaseAction(QuestPlayer player, ItemStack item, int cost, int fameRequirement) {
		this.player = player;
		this.item = item;
		this.cost = cost;
		this.fameCheck = fameRequirement;
	}
	
	@Override
	public void onAction() {
		//check if they have enough fame and money. 
		// If they do, give them the item and subtract the cost
		// If they don't don't give them the item and tell them off those noobs
		if (!player.getPlayer().isOnline()) {
			return;
			//something fishy happened...
		}
		
		Player p = player.getPlayer().getPlayer();
		
		if (player.getFame() < fameCheck) {
			p.sendMessage(denialFame);
			return;
		}
		if (player.getMoney() < cost) {
			p.sendMessage(denialMoney);
			return;			
		}
		
		//make sure there's room in their inventory
		if (p.getInventory().firstEmpty() == -1) {
			p.sendMessage(denialSpace);
			return;			
		}
		
		//everything's good, so throw it in!
		p.getInventory().addItem(item);
		player.addMoney(-cost);
	}
}
