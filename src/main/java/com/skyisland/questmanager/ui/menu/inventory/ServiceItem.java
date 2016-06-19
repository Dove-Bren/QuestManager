package com.skyisland.questmanager.ui.menu.inventory;

import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.npc.utils.Service;

public abstract class ServiceItem extends InventoryItem {

	protected ServiceItem(ItemStack displayItem) {
		super(displayItem);
	}
	
	public abstract Service getService();
}
