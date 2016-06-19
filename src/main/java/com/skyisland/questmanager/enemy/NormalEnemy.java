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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.skyisland.questmanager.loot.Loot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.loot.Lootable;

/**
 * Enemy type with very limited, straightforward customization; namely attributes.
 * Also supports loot specification
 *
 */
public class NormalEnemy extends Enemy implements Lootable, Listener {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(NormalEnemy.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(NormalEnemy.class);
	}
	

	private enum aliases {
		DEFAULT(NormalEnemy.class.getName()),
		SIMPLE("NormalEnemy");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	protected double hp;
	
	protected double attack;
	
	protected List<Loot> loot;
	
//	protected String type;
	
	public NormalEnemy(String name, EntityType type, double hp, double attack) {
		super(name, type);
		this.hp = hp;
		this.attack = attack;
		this.loot = new LinkedList<>();
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	public NormalEnemy(String name, EntityType type, double hp, double attack, Collection<Loot> loot) {
		this(name, type, hp, attack);
		this.loot.addAll(loot);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("type", type.name());
		map.put("name", name);
		map.put("hp", hp);
		map.put("attack", attack);
		map.put("loot", loot);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static NormalEnemy valueOf(Map<String, Object> map) {
		
		EntityType type;
		try {
			type = EntityType.valueOf(((String) map.get("type")).toUpperCase());
		} catch (Exception e) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to get EntityType " + 
					(String) map.get("type") + ", so defaulting to ZOMBIE");
			type = EntityType.ZOMBIE;
		}
		String name = (String) map.get("name");
		Double hp = (Double) map.get("hp");
		Double attack = (Double) map.get("attack");
		
		List<Loot> loot = null;
		if (map.containsKey("loot")) {
			try {
				loot = (List<Loot>) map.get("loot");
			} catch (Exception e) {
				e.printStackTrace();
				QuestManagerPlugin.questManagerPlugin.getLogger().warning("Failed to get loot list from "
						+ "config for NormalEnemy " + type.name() + " - " + name + ". Resorting to default loot.");
			}
		}
		
		if (loot != null) {
			return new NormalEnemy(name, type, hp, attack, loot);
		}
		
		return new NormalEnemy(name, type, hp, attack);
	}
	

	@Override
	public void spawn(Location loc) {
				
		Entity e = loc.getWorld().spawnEntity(loc, type);
		e.setCustomName(name);
		e.setCustomNameVisible(true);
		
		if (!(e instanceof LivingEntity)) {
			return;
		}
		
		LivingEntity entity = (LivingEntity) e;
		entity.setMaxHealth(hp);
		entity.setHealth(hp);
		entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
		
		entity.getEquipment().setItemInMainHandDropChance(0f);
		
		entity.setMetadata(Enemy.classMetaKey, new FixedMetadataValue(
				QuestManagerPlugin.questManagerPlugin,
				this.enemyClassID
				));
		
		
	}
	
	public void addLoot(Loot loot) {
		this.loot.add(loot);
	}
	
	@EventHandler
	public void onEnemyDeath(EntityDeathEvent e) {
		List<MetadataValue> metas = e.getEntity().getMetadata(classMetaKey);
		if (metas == null || metas.isEmpty()) {
			return;
		}
		
		//eliminate those that have a different EntityType right away, for performance
		if (e.getEntityType() != this.type) {
			return;
		}
		
		for (MetadataValue meta : metas) {
			if (!meta.getOwningPlugin().getName().equals(QuestManagerPlugin.questManagerPlugin.getName())) {
				continue;
			}
			
			
			//same plugin and same key. Use it.
			if (meta.asString().equals(enemyClassID)) {
				handleDeath(e);
				return;
			}
		}

	}
	
	private void handleDeath(EntityDeathEvent event) {
		//on death, drop loot (if we have any). otherwise, don't
		if (loot != null && !loot.isEmpty()) {
			event.getDrops().clear();
			event.getDrops().add(loot().getItem());
		}
	}

	@Override
	public Loot loot(){
		return Loot.getRandomLoot(loot);
	}
}
