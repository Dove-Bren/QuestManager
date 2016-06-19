package com.skyisland.questmanager.ui.menu.action;

import org.bukkit.entity.Player;

import com.skyisland.questmanager.player.QuestPlayer;

/**
 * The action of purchasing an item or service from an NPC.
 * This event specifically details purchases done from within an InventoryMenu, where it has
 * an ItemStack to give to the player
 * @author Skyler
 *
 */
public class PurchaseSpellAction implements MenuAction {
	
	private int cost;
	
	private int fameCheck;
	
	private String spell;
	
	private QuestPlayer player;
	
	private static final String denialFame = "Not famous enough!";
	
	private static final String denialMoney = "Not enough money!";
	
	private static final String denialExists = "You've already learned this spell!";
	
	public PurchaseSpellAction(QuestPlayer player, String spellName, int cost, int fameRequirement) {
		this.player = player;
		this.spell = spellName;
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
		
		//make sure they don't already have it
		
		if (player.getSpells().contains(spell)) {
			p.sendMessage(denialExists);
			return;
		}
		
		//everything's good, so throw it in!
		player.addSpell(spell);
		player.addMoney(-cost);
	}
}
