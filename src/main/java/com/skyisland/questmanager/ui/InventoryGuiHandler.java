package com.skyisland.questmanager.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.skyisland.questmanager.ui.menu.InventoryMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Gui handler for inventory menus
 * @author Skyler
 *
 */
public class InventoryGuiHandler implements Listener {
	
	private Map<UUID, InventoryMenu> menus;
	
	
	
	public InventoryGuiHandler() {
		this.menus = new HashMap<>();
	}
	
	/**
	 * Shows an inventory menu to the player, registering it with the handler.
	 */
	public void showMenu(Player player, InventoryMenu menu) {

		System.out.println("Inventory attempting registration: " + player.getUniqueId());
		QuestManagerPlugin plugin = QuestManagerPlugin.questManagerPlugin;
		
		if (menus.containsKey(player.getUniqueId())) {
			//menu already registered!
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Duplicate inventory menu attempting"
					+ " to be shown to player: [" + player.getName() + "]");
			return;
		}
		
		menus.put(player.getUniqueId(), menu);
		//TODO puytting constant stuff here for future 'different inventory menu types' expansion
		//just remove this stuff, put in specific subdivision of inv menu, and make constant method 
		//like 'showMenu' etc
		Bukkit.getPluginManager().registerEvents(menu, plugin);
		player.openInventory(menu.getInventory());
	}
	
	public void closeMenu(Player player) {
		System.out.print("Inventory attempting unregistration: " + player.getUniqueId() + ": ");
		System.out.println(menus.remove(player.getUniqueId()));
	}
}
