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
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.player.QuestPlayer;

/**
 * An inventory used with inventory gui's. Contains everything the rendered inventory needs
 *
 */
public class ShopInventory extends GuiInventory {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(ShopInventory.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(ShopInventory.class);
	}
	

	private enum aliases {
		FULL("com.SkyIsland.QuestManager.UI.Inventory.ShopInventory"),
		DEFAULT(ShopInventory.class.getName()),
		SHORT("ShopInventory"),
		INFORMAL("SHINV");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private Map<Integer, ShopItem> items;
	
	public ShopInventory() {
		items = new HashMap<>();
	}
	
	public ShopInventory(Map<Integer, ShopItem> items) {
		this.items = items;
	}
	
	@Override
	public Inventory getFormattedInventory(QuestPlayer player) {
		if (!player.getPlayer().isOnline()) {
			System.out.println("Cannot fetch inventory for offline player [GuiInventory@getFormattedInventory]");
			return null;
		}
		
		Player p = player.getPlayer().getPlayer();
		
		Inventory inv = Bukkit.createInventory(p, 45, p.getName() + "_qsh");
		if (!items.isEmpty()) {
			for (Entry<Integer, ShopItem> e : items.entrySet()) {
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
		 * 	item:
		 * 		==: itemstack
		 * 	cost: INTEGER
		 *  fame: INTEGER
		 * 8:
		 * 	""
		 */
		Map<String, Object> map = new HashMap<>();
		
		if (items.isEmpty()) {
			return map;
		}
		
		for (Entry<Integer, ShopItem> e : items.entrySet()) {
			Map<String, Object> subMap = new HashMap<>(4);
			
			//create subMap as specified in comments above
			subMap.put("display", e.getValue().getDisplay(null));
			subMap.put("item", e.getValue().getItem());
			subMap.put("cost", e.getValue().getCost());
			subMap.put("fame", e.getValue().getFamecost());
			
			map.put(e.getKey().toString(), subMap);
		}
		
		
		
		return map;
	}
	
	public static ShopInventory valueOf(Map<String, Object> configMap) {
		/*
		 * 4:
		 * 	display:
		 * 		==: itemstack
		 * 	item:
		 * 		==: itemstack
		 * 	cost: INTEGER
		 *  fame: INTEGER
		 * 8:
		 * 	""
		 */
		//TODO instead of jumping right into display, item, etc
		//make multiple inventory items. E.g. a purchase one, an inn one, etc
		
		YamlConfiguration config = new YamlConfiguration();
		config.createSection("top", configMap);
		
		Map<Integer, ShopItem> map = new HashMap<>();
		ConfigurationSection conf = config.getConfigurationSection("top");
		
		for (String slotString : conf.getKeys(false)) {
			ConfigurationSection section = conf.getConfigurationSection(slotString);
			if (slotString.startsWith("==")) {
				continue;
			}
			int key = Integer.valueOf(slotString);
			
			int cost, fame;
			ItemStack display;
			Object item;
			
			display = section.getItemStack("display");
			cost = section.getInt("cost");
			fame = section.getInt("fame");
			
			item = section.get("item");
			ShopItem ii;
			if (item instanceof String) {
				ii = new ShopSpell((String) item, display, cost, fame);
			} else {
				ii = new ShopItem((ItemStack) item, display, cost, fame);
			}
			
			map.put(key, ii);
			
		}
		
		return new ShopInventory(map);
		
	}

	@Override
	public InventoryItem getItem(int pos) {
		return items.get(pos);
	}
}
