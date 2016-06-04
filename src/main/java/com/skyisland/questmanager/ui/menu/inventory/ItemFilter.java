package com.skyisland.questmanager.ui.menu.inventory;

import org.bukkit.inventory.ItemStack;

/**
 * Filters an item based on some criteria
 * @author Skyler
 *
 */
public interface ItemFilter {

	public boolean filterItem(ItemStack item);
	
}