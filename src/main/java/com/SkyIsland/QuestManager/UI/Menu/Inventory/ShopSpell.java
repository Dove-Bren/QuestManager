package com.SkyIsland.QuestManager.UI.Menu.Inventory;

import org.bukkit.inventory.ItemStack;

import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.UI.Menu.Action.MenuAction;
import com.SkyIsland.QuestManager.UI.Menu.Action.PurchaseSpellAction;

/**
 * An item used in an inventory menu.<br />
 * This is composed of an actual item stack, a display item type, and the cost & fame requirement
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
