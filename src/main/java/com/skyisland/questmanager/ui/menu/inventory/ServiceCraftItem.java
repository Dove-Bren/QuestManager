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

import java.util.LinkedList;
import java.util.List;

import com.skyisland.questmanager.npc.utils.Service;
import com.skyisland.questmanager.npc.utils.ServiceCraft;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.action.CraftServiceAction;
import com.skyisland.questmanager.ui.menu.message.Message;
import com.skyisland.questmanager.ui.menu.message.PlainMessage;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.ui.menu.action.MenuAction;

/**
 * Represents a craft the service NPC can perform.
 * @author Skyler
 *
 */
public class ServiceCraftItem extends ServiceItem {
	
	private static final Message denialMessage = new PlainMessage(ChatColor.RED + "You were missing some components of the craft");
	
	private ServiceCraft craft;
	
	public ServiceCraftItem(ServiceCraft craft) {
		super(craft.getResult());
		this.craft = craft;
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
		List<String> lore = new LinkedList<>();
		lore.add(
				ChatColor.DARK_PURPLE + "Craft  " + (craft.getCost() <= player.getMoney() ? ChatColor.GOLD : ChatColor.DARK_RED) + 
					"      Cost: " + craft.getCost());
		lore.add(ChatColor.DARK_RED + "Requires:");
		
		for (ItemStack item : craft.getRequired()) {
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				lore.add("" + ChatColor.GRAY + item.getAmount() + " x " 
						+ (player.hasItem(item) ? ChatColor.GREEN : ChatColor.RED) 
						+ item.getItemMeta().getDisplayName());
			} else {
				lore.add((item.getAmount() > 0 ? "" + ChatColor.GRAY + item.getAmount() + " x " : "") 
						+ (player.hasItem(item) ? ChatColor.GREEN : ChatColor.RED) 
						+ toCase(item.getType().toString()));
			}
		}
			
		meta.setLore(lore);
		ret.setItemMeta(meta);
		
		return ret;
	}
	
	public ItemStack getResult() {
		return craft.getResult();
	}
	
	public List<ItemStack> getRequired() {
		return craft.getRequired();
	}
	
	@Override
	public MenuAction getAction(QuestPlayer player) {
		return new CraftServiceAction(craft, player, denialMessage);
	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return craft.getCost();
	}

	@Override
	public Service getService() {
		return craft;
	}
	
	/**
	 * Removes underscores from passed string and convers to title case
	 */
	public static String toCase(String input) {
		input = input.replace("_", " ");
		input = input.toLowerCase();
		
		String result = "";
		boolean set = false;
		
		for (int pos = 0; pos < input.length(); pos++) {
			if (pos == 0) {
				result += input.substring(0, 1).toUpperCase();
				continue;
			}
			
			if (set) {
				set = false;
				result += input.substring(pos, pos+1).toUpperCase();
				continue;
			}
			
			if (input.charAt(pos) == ' ') {
				set = true;
			}
			
			result += input.charAt(pos);
			
			
			
		}

		return result;
	}
}
