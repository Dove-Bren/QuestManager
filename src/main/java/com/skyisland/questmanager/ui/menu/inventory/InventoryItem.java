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

import com.skyisland.questmanager.player.QuestPlayer;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.ui.menu.action.MenuAction;

/**
 * An item used in an inventory menu.
 * This is composed of an actual item stack, a display item type, and the cost and fame requirement
 * of the item.
 *
 */
public abstract class InventoryItem {
	
	private ItemStack displayItem;
	
	protected InventoryItem(ItemStack displayItem) {
		this.displayItem = displayItem;
	}
	
	/**
	 * Returns the display item without any modification to the lore, etc. This is like the unformatted version
	 */
	public ItemStack getRawDisplayItem() {
		return displayItem;
	}
	
	/**
	 * Returns a nice, pretty display item ocmplete with lore and naming magic
	 */
	public abstract ItemStack getDisplay(QuestPlayer player);
	
	/**
	 * Return the action that should be performed when this menu item is clicked/activated
	 */
	public abstract MenuAction getAction(QuestPlayer player);
}
