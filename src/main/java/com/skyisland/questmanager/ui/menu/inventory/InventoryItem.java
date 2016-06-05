package com.skyisland.questmanager.ui.menu.inventory;

import com.skyisland.questmanager.player.QuestPlayer;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.ui.menu.action.MenuAction;

/**
 * An item used in an inventory menu.<br />
 * This is composed of an actual item stack, a display item type, and the cost & fame requirement
 * of the item.
 * @author Skyler
 *
 */
public abstract class InventoryItem {
	
	private ItemStack displayItem;
	
	protected InventoryItem(ItemStack displayItem) {
		this.displayItem = displayItem;
	}
	
	/**
	 * Returns the display item without any modification to the lore, etc. This is like the unformatted version
	 * @return
	 */
	public ItemStack getRawDisplayItem() {
		return displayItem;
	}
	
	/**
	 * Returns a nice, pretty display item ocmplete with lore and naming magic
	 * @param player
	 * @return
	 */
	public abstract ItemStack getDisplay(QuestPlayer player);
	
	/**
	 * Return the action that should be performed when this menu item is clicked/activated
	 * @param player
	 * @return
	 */
	public abstract MenuAction getAction(QuestPlayer player);
}
