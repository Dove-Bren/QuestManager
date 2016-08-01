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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.action.MenuAction;

/**
 * An inventory used with inventory gui's. Contains everything the rendered inventory needs
 *
 */
public class BasicInventory extends GuiInventory {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(BasicInventory.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(BasicInventory.class);
	}
	

	private enum aliases {
		FULL("com.SkyIsland.QuestManager.UI.Inventory.BasicInventory"),
		DEFAULT(BasicInventory.class.getName()),
		SHORT("BasicInventory"),
		INFORMAL("BINV");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private Map<Integer, BasicInventoryItem> items;
	
	public BasicInventory() {
		items = new HashMap<>();
	}
	
	public BasicInventory(Map<Integer, BasicInventoryItem> items) {
		this.items = items;
	}
	
	public void addInventoryItem(BasicInventoryItem item) {
		addInventoryItem(item,items.size());
	}
	
	public void addInventoryItem(BasicInventoryItem item, int position) {
		items.put(position, item);
	}
	
	@Override
	public Inventory getFormattedInventory(QuestPlayer player) {
		if (!player.getPlayer().isOnline()) {
			System.out.println("Cannot fetch inventory for offline player [GuiInventory@getFormattedInventory]");
			return null;
		}
		
		Player p = player.getPlayer().getPlayer();
		
		Inventory inv = Bukkit.createInventory(p, 45, p.getName() + "_qbi");
		if (!items.isEmpty()) {
			for (Entry<Integer, BasicInventoryItem> e : items.entrySet()) {
				Object key = e.getKey();
				if (key == null) {
					continue;
				}
				int val;
				if (key instanceof Integer) {
					val = (Integer) key;
				} else if (key instanceof String) {
					val = Integer.valueOf((String) key);
				} else {
					val = 0;
					System.out.println("invalid key! not string or int!");
				}
				inv.setItem(val, e.getValue().getDisplay(player));
			}
		}
		
		return inv;
	}

	@Override
	public Map<String, Object> serialize() {
		/*
		 * 4:
		 * 	display:
		 * 		==: itemstack
		 *  tooltip:
		 *    - String
		 *    - String
		 * 	action:
		 *      ==: MenuAction
		 * 8:
		 * 	""
		 */
		Map<String, Object> map = new HashMap<>();
		
		if (items.isEmpty()) {
			return map;
		}
		
		for (Entry<Integer, BasicInventoryItem> e : items.entrySet()) {
			Map<String, Object> subMap = new HashMap<>(4);
			
			//create subMap as specified in comments above
			subMap.put("display", e.getValue().getDisplay(null));
			subMap.put("action", e.getValue().getAction(null));
			ItemMeta meta = e.getValue().getDisplay(null).getItemMeta();
			subMap.put("tooltip", Lists.newArrayList(meta.getDisplayName(), meta.getLore()));
			
			map.put(e.getKey().toString(), subMap);
		}
		
		
		
		return map;
	}
	
	public static BasicInventory valueOf(Map<String, Object> configMap) {
		/*
		 * 4:
		 * 	display:
		 * 		==: itemstack
		 *  tooltip:
		 *    - String
		 *    - String
		 * 	action:
		 *     ==: MenuAction
		 * 8:
		 * 	""
		 */
		
		YamlConfiguration config = new YamlConfiguration();
		config.createSection("top", configMap);
		
		Map<Integer, BasicInventoryItem> map = new HashMap<>();
		ConfigurationSection conf = config.getConfigurationSection("top");
		
		for (String slotString : conf.getKeys(false)) {
			ConfigurationSection section = conf.getConfigurationSection(slotString);
			if (slotString.startsWith("==")) {
				continue;
			}
			int key = Integer.valueOf(slotString);
			
			ItemStack display;
			MenuAction action;
			
			display = section.getItemStack("display");
			action = (MenuAction) section.get("action");
			List<String> tooltip = section.getStringList("tooltip");

			BasicInventoryItem ii = new BasicInventoryItem(display, tooltip, action);

			map.put(key, ii);
			
		}
		
		return new BasicInventory(map);
		
	}

	@Override
	public InventoryItem getItem(int pos, InventoryAction action) {
		return items.get(pos);
	}
}
