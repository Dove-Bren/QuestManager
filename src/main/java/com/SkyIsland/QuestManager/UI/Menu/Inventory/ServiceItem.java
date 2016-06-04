package com.SkyIsland.QuestManager.UI.Menu.Inventory;

import org.bukkit.inventory.ItemStack;

import com.SkyIsland.QuestManager.NPC.Utils.Service;

public abstract class ServiceItem extends InventoryItem {

	protected ServiceItem(ItemStack displayItem) {
		super(displayItem);
	}
	
	public abstract Service getService();

}
