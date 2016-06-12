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

import com.skyisland.questmanager.npc.utils.Service;
import com.skyisland.questmanager.npc.utils.ServiceCraft;
import com.skyisland.questmanager.player.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.skyisland.questmanager.npc.utils.ServiceOffer;

/**
 * Inventory that has service deals.
 * The inventory can hold only service items, and not regular purchases.
 * @author Skyler
 *
 */
public class ServiceInventory extends GuiInventory {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(ServiceInventory.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(ServiceInventory.class);
	}
	

	private enum aliases {
		FULL("com.SkyIsland.QuestManager.UI.Inventory.ShopInventory"),
		DEFAULT(ServiceInventory.class.getName()),
		SHORT("ServiceInventory"),
		INFORMAL("SERVINV");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private Map<Integer, ServiceItem> items;
	
	public ServiceInventory() {
		super();
	}
	
	public ServiceInventory(Map<Integer, ServiceItem> items) {
		this.items = items;
	}
	
	@Override
	public Inventory getFormattedInventory(QuestPlayer player) {
		if (!player.getPlayer().isOnline()) {
			System.out.println("Cannot fetch inventory for offline player [GuiInventory@getFormattedInventory]");
			return null;
		}
		
		Player p = player.getPlayer().getPlayer();
		
		Inventory inv = Bukkit.createInventory(p, 45, p.getName() + "_qsr");
		if (!items.isEmpty()) {
			for (Entry<Integer, ServiceItem> e : items.entrySet()) {
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
		 * 	==: Service
		 * 8:
		 * 	""
		 */
		Map<String, Object> map = new HashMap<>();
		
		if (items.isEmpty()) {
			return map;
		}
		
		for (Entry<Integer, ServiceItem> e : items.entrySet()) {
			map.put(e.getKey().toString(), e.getValue().getService());
		}
		
		
		
		return map;
	}
	
	public static ServiceInventory valueOf(Map<String, Object> configMap) {
		/*
		 * 4:
		 * 	==: service
		 * 8:
		 * 	""
		 */
		
		YamlConfiguration config = new YamlConfiguration();
		config.createSection("top", configMap);
		
		Map<Integer, ServiceItem> map = new HashMap<>();
		ConfigurationSection conf = config.getConfigurationSection("top");
		
		for (String slotString : conf.getKeys(false)) {
			if (slotString.startsWith("==")) {
				continue;
			}
			int key = Integer.valueOf(slotString);
			Service service = (Service) conf.get(slotString);
			ServiceItem servItem;
			if (service instanceof ServiceCraft) {
				servItem = new ServiceCraftItem((ServiceCraft) service);
			} else if (service instanceof ServiceOffer) {
				servItem = new ServiceOfferItem((ServiceOffer) service);
			} else {
				System.out.println("Serious error when interpretting service inventory");
				return null;
			}
			map.put(key, servItem);
		}
		
		return new ServiceInventory(map);
		
	}

	@Override
	public InventoryItem getItem(int pos) {
		return items.get(pos);
	}
}
