/*
 *  QuestManager: An RPG plugin for the Bukkit API.
 *  Copyright (C) 2015-2016 Github Contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		menus.remove(player.getUniqueId());
	}
}
