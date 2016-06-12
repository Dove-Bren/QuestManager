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

package com.skyisland.questmanager.configuration.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Holds an abstract chest, with a list of items and a location.
 * The inventory has to be instantiated, so getInventory call was made
 */
public class Chest implements ConfigurationSerializable {
	

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(Chest.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(Chest.class);
	}
	

	private enum aliases {
		FULL("com.SkyIsland.QuestManager.Configuration.Utils.Chest"),
		DEFAULT(Chest.class.getName()),
		SHORT("Chest"),
		QUALIFIED_INFORMAL("QMChest");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private List<ItemStack> inventory;
	
	private Location location;
	
	private Material material;

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		if (inventory == null) {
			return map;
		}
		
		map.put("location", new LocationState(location));
		
		map.put("items", inventory);
		
		map.put("material", material);
		
		return map;
	}

	@SuppressWarnings("unchecked")
	public static Chest valueOf(Map<String, Object> map) throws InvalidConfigurationException {
		if (!map.containsKey("location") || !map.containsKey("items")) {
			throw new InvalidConfigurationException();
		}
		
		LocationState loc = (LocationState) map.get("location");
		List<ItemStack> items = (List<ItemStack>) map.get("items");
		Material type = null;
		if (map.containsKey("material")) {
			type = Material.valueOf((String) map.get("material"));
		}
		return new Chest(items, loc.getLocation(), type);
	}
	
	public Chest(List<ItemStack> items, Location location, Material material) {
		this.inventory = items;
		this.location = location;
		this.material = material;
		
		if (material == null || !material.isBlock()) {
			this.material = Material.CHEST;
		}
	}
	
	public Chest(List<ItemStack> items, Location location) {
		this(items, location, Material.CHEST);
	}
	
	public Inventory getInventory(InventoryHolder holder) {
		Inventory inv = Bukkit.createInventory(holder, (int) (9 * (1 + Math.floor(inventory.size()/9))));
		inventory.forEach(inv::addItem);
		
		return inv;
	}
	
	public Location getLocation() {
		return this.location;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
}
