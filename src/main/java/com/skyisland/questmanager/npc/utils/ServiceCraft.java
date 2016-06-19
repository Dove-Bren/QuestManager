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

package com.skyisland.questmanager.npc.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

public class ServiceCraft extends Service {
	

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(ServiceCraft.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(ServiceCraft.class);
	}
	

	private enum aliases {
		FULL("com.SkyIsland.QuestManager.NPC.ServiceCraft"),
		DEFAULT(ServiceCraft.class.getName()),
		SHORT("ServiceCraft"),
		INFORMAL("CRAFT");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private String name;
	
	private int cost;
	
	private List<ItemStack> requiredItems;
	
	private ItemStack result;
	
	public ServiceCraft(String name, int cost, List<ItemStack> requiredItems, ItemStack result) {
		this.cost = cost;
		this.requiredItems = requiredItems;
		this.result = result;
	}
	
	public ItemStack getResult() {
		return result;
	}
	
	public List<ItemStack> getRequired() {
		return requiredItems;
	}
	
	public int getCost() {
		return cost;
	}
	
	public String getName() {
		return this.name;
	}
	
	@SuppressWarnings("unchecked")
	public static ServiceCraft valueOf(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		/*
		 * name: name of trade for tooltip
		 * cost: money cost
		 * requiredItems: 
		 * 	list of items
		 * result:
		 * 	==item
		 */
		
		String name = (String) map.get("name");
		int cost = (int) map.get("cost");
		
		List<ItemStack> required = (List<ItemStack>) map.get("requiredItems");
		ItemStack item = (ItemStack) map.get("result");
		
		return new ServiceCraft(name, cost, required, item);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("name", name);
		map.put("cost", cost);
		
		map.put("requiredItems", requiredItems);
		map.put("result", result);
		
		return map;
	}
	
}
