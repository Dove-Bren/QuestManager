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

package com.skyisland.questmanager.ui.menu.inventory;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;

import com.skyisland.questmanager.player.QuestPlayer;

/**
 * An inventory used with inventory gui's. Contains everything the rendered inventory needs.
 * Implementations should provide a way to load and save their information, as well as how to format the inventory for
 * display.
 *
 */
public abstract class GuiInventory implements ConfigurationSerializable {
	
	public abstract InventoryItem getItem(int pos, InventoryAction action);
	
	public abstract Inventory getFormattedInventory(QuestPlayer player);
}
