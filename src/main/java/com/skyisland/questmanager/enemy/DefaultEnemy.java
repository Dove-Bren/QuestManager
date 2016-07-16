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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Default enemy type for default mobs in minecraft.
 * Wrapper for QM enemies
 *
 */
public class DefaultEnemy extends Enemy {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link Aliases Aliases}
	 */
	public static void registerWithAliases() {
		for (Aliases alias : Aliases.values()) {
			ConfigurationSerialization.registerClass(DefaultEnemy.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(DefaultEnemy.class);
	}
	

	private enum Aliases {
		DEFAULT(DefaultEnemy.class.getName()),
		SIMPLE("DefaultEnemy");
		
		private String alias;
		
		Aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private String variantData;
	
	public DefaultEnemy(String name, EntityType type) {
		super(name, type);
		variantData = null;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("type", type.name());
		map.put("name", name);
		map.put("variantData", variantData);
		
		return map;
	}
	
	public static DefaultEnemy valueOf(Map<String, Object> map) {
		
		String type = (String) map.get("type");
		EntityType et;
		try {
			et = EntityType.valueOf(type.toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Invalid entity type: " + type + "! Defaulting to Zombie!");
			et = EntityType.ZOMBIE;
		}
		
		String name = (String) map.get("name");
		String variantData = null;
		if (map.containsKey("variantData"))
			variantData = map.get("variantData").toString();
		
		DefaultEnemy dent = new DefaultEnemy(name, et);
		dent.variantData = variantData;
		return dent;
	}

	@Override
	protected void spawnEntity(Entity base) {
		if (variantData != null) {
			switch (type){
			case ZOMBIE:
				Zombie zombie = (Zombie) base;
				try {
					zombie.setVillagerProfession(Profession.valueOf(variantData));
				} catch (Exception e) {
					QuestManagerPlugin.logger.warning("Failed to identify zombie type: " + variantData);
				}
				break;
			case SKELETON:
				Skeleton skele = (Skeleton) base;
				try {
					skele.setSkeletonType(SkeletonType.valueOf(variantData));
				} catch (Exception e) {
					QuestManagerPlugin.logger.warning("Failed to identify skeleton type: " + variantData);
				}
				break;
			case SLIME:
				int size = 0;
				try {
					size = Integer.parseInt(variantData);
				} catch (NumberFormatException e) {
					QuestManagerPlugin.logger.warning("Failed to parse slime size int: " + variantData + ". Defaulting to 0");
				}
				((Slime) base).setSize(size);
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	protected void handleDeath(EntityDeathEvent e) {
		; //nothing
	}
}
