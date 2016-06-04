package com.skyisland.questmanager.ui.menu.inventory;

import org.bukkit.inventory.ItemStack;

/**
 * Inventory Gui that's expected to produce some sort of return or result
 * @author Skyler
 *
 */
public abstract class ReturnGuiInventory extends GuiInventory {
	
	public abstract ItemStack[] getResult();
		
}
