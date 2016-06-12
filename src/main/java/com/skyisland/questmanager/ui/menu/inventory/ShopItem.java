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

import java.util.Arrays;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.action.PurchaseAction;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.ui.menu.action.MenuAction;

/**
 * An item used in an inventory menu.
 * This is composed of an actual item stack, a display item type, and the cost and fame requirement
 * of the item.
 *
 */
public class ShopItem extends InventoryItem {
	
	private ItemStack item;
	
	private int cost;
	
	private int famecost;
	
	public ShopItem(ItemStack item, ItemStack displayItem, int cost, int famecost) {
		super(displayItem);
		this.item = item;
		this.cost = cost;
		this.famecost = famecost;
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
		if (player == null) {
			return getRawDisplayItem();
		}
		ItemStack ret = getRawDisplayItem().clone();
		ItemMeta meta = ret.getItemMeta();
		meta.setLore(Arrays.asList(
				(cost <= player.getMoney() ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + 
					"Cost:               " + cost,
				(famecost <= player.getFame() ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) +
					"Fame Required: " + famecost));
		ret.setItemMeta(meta);
		
		return ret;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	@Override
	public MenuAction getAction(QuestPlayer player) {
		return new PurchaseAction(player, item, cost, famecost);
	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * @return the famecost
	 */
	public int getFamecost() {
		return famecost;
	}
}
