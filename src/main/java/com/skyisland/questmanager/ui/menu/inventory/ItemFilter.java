package com.skyisland.questmanager.ui.menu.inventory;

import org.bukkit.inventory.ItemStack;

/**
 * Filters an item based on some criteria.
 */
@FunctionalInterface
public interface ItemFilter {

	boolean filterItem(ItemStack item);
}
