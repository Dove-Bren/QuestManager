package com.skyisland.questmanager.ui.menu.inventory;

import com.skyisland.questmanager.npc.utils.Service;
import org.bukkit.inventory.ItemStack;

public abstract class ServiceItem extends InventoryItem {

	protected ServiceItem(ItemStack displayItem) {
		super(displayItem);
	}
	
	public abstract Service getService();

}
