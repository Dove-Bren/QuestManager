package com.skyisland.questmanager.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.ui.menu.InventoryMenu;

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

		QuestManagerPlugin plugin = QuestManagerPlugin.questManagerPlugin;
		
		if (menus.containsKey(player.getUniqueId())) {
			//menu already registered!
			QuestManagerPlugin.logger.warning("Duplicate inventory menu attempting"
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
		menus.remove(player.getUniqueId());
	}
}
