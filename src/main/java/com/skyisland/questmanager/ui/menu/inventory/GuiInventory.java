package com.skyisland.questmanager.ui.menu.inventory;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;

import com.skyisland.questmanager.player.QuestPlayer;

/**
 * An inventory used with inventory gui's. Contains everything the rendered inventory needs.
 * Implementations should provide a way to load and save their information, as well as how to format the inventory for
 * display.
 * @author Skyler
 *
 */
public abstract class GuiInventory implements ConfigurationSerializable {
	
	
	public abstract InventoryItem getItem(int pos);
	
	public abstract Inventory getFormattedInventory(QuestPlayer player);
}
