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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.action.MenuAction;

import net.md_5.bungee.api.ChatColor;

/**
 * An item used in an inventory menu.
 * This is composed of an actual item stack, a display item type, and the cost and fame requirement
 * of the item.
 * @author Skyler
 *
 */
public class BasicInventoryItem extends InventoryItem {
	
	private ItemStack item;
	
	//private List<String> tooltip;
	
	private MenuAction action;
	
	/**
	 * Makes a simple wrapper item that runs the provided action when clicked. Tooltip can be null.
	 * @param tooltip The tooltip to display. If null, no tooltip is displayed
	 */
	public BasicInventoryItem(ItemStack icon, List<String> tooltip, MenuAction action) {
		super(icon);
		
		//set up tooltip information
		ItemMeta meta = icon.getItemMeta();
		if (tooltip == null) {
			meta.setDisplayName("");
		} else {
			meta.setDisplayName(ChatColor.GRAY + tooltip.get(0) + ChatColor.RESET);
		}
		
		if (tooltip.size() > 1) {
			List<String> lore = new ArrayList<>(tooltip.size() - 1);
			for (int i = 1; i < tooltip.size(); i++) {
				lore.add(ChatColor.GRAY + tooltip.get(i));
			}
			meta.setLore(lore);
		}
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
				ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		
		icon.setItemMeta(meta);
		
		
		this.item = icon;
		this.action = action;
	}
	
	/**
	 * Returns the item that should be used to display the item to the given player.
	 * This method formats the lore, etc to display correctly (and with correct colors) to
	 * the provided player given their fame and money.
	 * 
	 * If the passed player is null, the item without lore is returned.
	 */
	@Override
	public ItemStack getDisplay(QuestPlayer player) {
		return item;
	}
	
	@Override
	public MenuAction getAction(QuestPlayer player) {
		return action;
	}
}
