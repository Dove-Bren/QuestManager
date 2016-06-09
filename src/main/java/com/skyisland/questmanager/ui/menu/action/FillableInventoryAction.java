package com.skyisland.questmanager.ui.menu.action;

import org.bukkit.inventory.ItemStack;

public interface FillableInventoryAction extends MenuAction {

	void provideItems(ItemStack[] objects);
}
