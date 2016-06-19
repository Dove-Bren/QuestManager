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

package com.skyisland.questmanager.loot;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Holds information about a possible piece of loot.
 * Information stored included what the loot item will be, and the chance it'll be obtained.
 * <p>
 * Implementations of loot generation may vary, but the weight of the loot should universally be held
 * as the relative ratio of how often a piece of loot will show up, compared to a piece of loot with weight 1.
 * Specific drop chances per loot generation depends on how many pieces of loot are being generated, and how
 * big the pool is.
 * </p>
 * <p>
 * To better illustrate weight, consider a loot pool with only two possible items with weights <i>1.0</i> and 
 * <i>2.0</i>. For every attempt to generate a piece of loot, the object with weight <i>2.0</i> should have
 * <b>double</b> the chance of being selected compared to the other object. In this situation, the piece of 
 * loot would have a 66.6% chance of being drawn. 
 * </p>
 * <p>
 * The exact probability per loot generation is given as
 * &nbsp;&nbsp;&nbsp;&nbsp;(<i>weight</i>) / (<i>Pool weight total</i>)
 * </p>
 *
 */
public class Loot implements ConfigurationSerializable {
	
	public static final Material DEFAULT_MATERIAL = Material.STONE;
	
	public static final double DEFAULT_WEIGHT = 1.0;
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(Loot.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(Loot.class);
	}
	

	private enum aliases {
		UPPER("LOOT"),
		LOWER("loot"),
		FORMAL("Loot"),
		DEFAULT(Loot.class.getName());
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("weight", weight);
		map.put("item", item);
		
		return map;
	}
	
	public static Loot valueOf(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		double weight = DEFAULT_WEIGHT;
		ItemStack item;
		
		if (map.containsKey("weight")) {
			weight = (Double) map.get("weight");
		}
		
		if (map.containsKey("item")) {
			//either itemstack or material type. We'll take both
			Object o = map.get("item");
			if (o instanceof String) {
				Material type;
				try {
					type = Material.matchMaterial((String) o);
				} catch (Exception e) {
					QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to match material to "
							+ (String) o + ". Defaulting to " + DEFAULT_MATERIAL.name());
					type = DEFAULT_MATERIAL;
				}
				
				item = new ItemStack(type);
			} else {
				try {
					item = (ItemStack) o;
				} catch (Exception e) {
					e.printStackTrace();
					QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to get item from the "
							+ "provided object: "+ o.toString() + ". Defaulting to " + DEFAULT_MATERIAL.name());
					item = new ItemStack(DEFAULT_MATERIAL);
				}
			}
		} else {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to find item key in loot. "
					+ "Defaulting to " + DEFAULT_MATERIAL.name());
			item = new ItemStack(DEFAULT_MATERIAL);
		}
		
		return new Loot(item, weight);
		
		
	}
	
	private double weight;
	
	private ItemStack item;
	
	/**
	 * Creates a piece of loot with the given item and weight.
	 * If weight is less than or equal to 0, 1.0 is taken as weight instead.
	 */
	public Loot(ItemStack item, double weight) {
		this.item = item;
		this.weight = weight;
		
		if (weight <= 0) {
			this.weight = 1.0;
		}
	}

	public double getWeight() {
		return weight;
	}

	public ItemStack getItem() {
		return item;
	}

	/**
	 * Standard method of selecting a piece of loot from a list of loot items.
	 * This method will return null if the argument list is either null or empty.
	 * <p>
	 * The algorithm this method follows the definition of {@link Loot}'s weight member; the chance a piece
	 * of loot is selected is equal to
	 * &nbsp;&nbsp;&nbsp;&nbsp;(<i>weight</i>) / (<i>Pool weight total</i>)
	 * </p>
	 */
	public static Loot getRandomLoot(List<Loot> loot) {
		if (loot == null || loot.isEmpty()) {
			return null;
		}

		Random rand = new Random();
		Collections.shuffle(loot, rand);

		double max = 0;
		for (Loot l : loot) {
			max += l.getWeight();
		}

		double index = rand.nextDouble() * max;
		for (Loot l : loot) {
			index -= l.getWeight();
			if (index <= 0) {
				//reached piece of loot to select
				return l;
			}
		}

		//we reached end with a positive index. Something went wrong (probably double precious error)
		//return end of list.
		return loot.get(loot.size() - 1);
	}
}
