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

package com.skyisland.questmanager.enemy;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.loot.Loot;

/**
 * Enemy type with equipment customization in addition to NormalMob stuff
 *
 */
public class StandardEnemy extends NormalEnemy {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(StandardEnemy.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(StandardEnemy.class);
	}
	

	private enum aliases {
		DEFAULT(StandardEnemy.class.getName()),
		SIMPLE("StandardEnemy");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private ItemStack head, chest, legs, boots, mainhand, offhand;
	
	public StandardEnemy(String name, EntityType type, double hp, double attack) {
		this(name, type, hp, attack, null, null, null, null, null, null);
	}
	
	@Deprecated
	public StandardEnemy(String name, EntityType type, double hp, double attack,
			ItemStack head, ItemStack chest, ItemStack legs, ItemStack boots, ItemStack hands) {
		this(name, type, hp, attack, head, chest, legs, boots, hands, null);
	}
	
	public StandardEnemy(String name, EntityType type, double hp, double attack,
			ItemStack head, ItemStack chest, ItemStack legs, ItemStack boots, ItemStack mainhand, ItemStack offhand) {
		super(name, type, hp, attack);
		this.head = head;
		this.chest = chest;
		this.legs = legs;
		this.boots = boots;
		this.mainhand = mainhand;
		this.offhand = offhand;
		
	}
	
	public StandardEnemy(String name, EntityType type, double hp, double attack,
			ItemStack head, ItemStack chest, ItemStack legs, ItemStack boots, ItemStack mainhand, ItemStack offhand,
			Collection<Loot> loot) {
		this(name, type, hp, attack, head, chest, legs, boots, mainhand, offhand);
		this.loot.addAll(loot);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("type", type);
		map.put("name", name);
		map.put("hp", hp);
		map.put("attack", attack);
		map.put("head", head);
		map.put("chest", chest);
		map.put("legs", legs);
		map.put("boots", boots);
		map.put("mainhand", mainhand);
		map.put("offhand", offhand);
		map.put("loot", this.loot);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static StandardEnemy valueOf(Map<String, Object> map) {
		
		EntityType type;
		try {
			type = EntityType.valueOf(((String) map.get("type")).toUpperCase());
		} catch (Exception e) {
			QuestManagerPlugin.logger.warning("Unable to get EntityType " + 
					(String) map.get("type") + ", so defaulting to ZOMBIE");
			type = EntityType.ZOMBIE;
		}
		
		String name = (String) map.get("name");
		Double hp = (Double) map.get("hp");
		Double attack = (Double) map.get("attack");
		ItemStack head, chest, legs, boots, mainhand, offhand;
		head = chest = legs = boots = mainhand = offhand = null;
		
		if (map.containsKey("head")) {
			head = new ItemStack(Material.valueOf((String) map.get("head")));
		}
		
		if (map.containsKey("chest")) {
			chest = new ItemStack(Material.valueOf((String) map.get("chest")));
		}
		
		if (map.containsKey("legs")) {
			legs = new ItemStack(Material.valueOf((String) map.get("legs")));
		}
		
		if (map.containsKey("boots")) {
			boots = new ItemStack(Material.valueOf((String) map.get("boots")));
		}
		
		if (map.containsKey("hands")) {
			mainhand = new ItemStack(Material.valueOf((String) map.get("hands")));
		} else if (map.containsKey("mainhand") && map.containsKey("offhand")) {
			mainhand = new ItemStack(Material.valueOf((String) map.get("mainhand")));
			offhand = new ItemStack(Material.valueOf((String) map.get("offhand")));
			
		}
		
		List<Loot> loot = null;
		if (map.containsKey("loot")) {
			try {
				loot = (List<Loot>) map.get("loot");
			} catch (Exception e) {
				e.printStackTrace();
				QuestManagerPlugin.logger.warning("Failed to get loot list from "
						+ "config for NormalEnemy " + type.name() + " - " + name + ". Resorting to default loot.");
			}
		}
		
		if (loot != null) {
			return new StandardEnemy(name, type, hp, attack, head, chest, legs, boots, mainhand, offhand, loot);
		}
		
		return new StandardEnemy(name, type, hp, attack, head, chest, legs, boots, mainhand, offhand);
	}
	
	@Override
	public void spawnEntity(Entity base) {
		if (!(base instanceof LivingEntity)) {
			return;
		}
		
		LivingEntity entity = (LivingEntity) base;
		entity.setMaxHealth(hp);
		entity.setHealth(hp);
		entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
		
		EntityEquipment eq = entity.getEquipment();
		eq.setHelmet(head);
		eq.setChestplate(chest);
		eq.setLeggings(legs);
		eq.setBoots(boots);
		eq.setItemInMainHand(mainhand);
		eq.setItemInOffHand(offhand);
		eq.setHelmetDropChance(0f);
		eq.setChestplateDropChance(0f);
		eq.setLeggingsDropChance(0f);
		eq.setBootsDropChance(0f);
		eq.setItemInMainHandDropChance(0f);
		eq.setItemInOffHandDropChance(0f);
	}
}
