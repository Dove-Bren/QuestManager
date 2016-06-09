package com.skyisland.questmanager.ui.menu.inventory;

import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.action.MenuAction;
import com.skyisland.questmanager.ui.menu.action.PurchaseSpellAction;

/**
 * An item used in an inventory menu.
 * This is composed of an actual item stack, a display item type, and the cost and fame requirement
 * of the item.
 * @author Skyler
 *
 */
public class ShopSpell extends ShopItem {
	
	private String spell;
	
	private int cost;
	
	private int famecost;
	
	public ShopSpell(String spellName, ItemStack displayItem, int cost, int famecost) {
		super(null, displayItem, cost, famecost);
		this.spell = spellName;
		this.cost = cost;
		this.famecost = famecost;
	}
	
	@Override
	public MenuAction getAction(QuestPlayer player) {
		return new PurchaseSpellAction(player, spell, cost, famecost);
	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * @return the famecost
	 */
	public int getFamecost() {
		return famecost;
	}
}
