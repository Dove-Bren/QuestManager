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

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.enemy.events.EnemyDeathEvent;
import com.skyisland.questmanager.region.Region;

/**
 * Depicts a QM enemy, which can be created or destroyed as the world loads and unloads.
 * Each enemy object represents a unique, spawnable enemy type. Each instance of an enemy is created from this
 * class when spawning things, but this class doesn't hold instances.
 */
public abstract class Enemy implements ConfigurationSerializable, Listener {
	
	protected EntityType type;
	
	protected String name;
	
	protected Region spawningRegion;
	
	/**
	 * Unique ID used to figure out who spawned an enemy. For example, was it NormalEnemy A, or StandardEnemy E?
	 * If the buried {@link #Enemy(String, EntityType)} constructor is not used, this should be set manually.
	 */
	protected String enemyClassID;
	
	private static int enemyClassIDIndex;
	
	{
		enemyClassIDIndex = ((new Random()).nextInt(4000));
	}
	
	public static final String CLASS_META_KEY = "QMEnemySpawnClass";
	
	public Enemy(String name, EntityType type) {
		this.enemyClassID = generateNewEnemyClassID();
		this.name = name;
		this.type = type;
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	public void spawn(Location loc) {
		Entity e = loc.getWorld().spawnEntity(loc, type);
		e.setMetadata(Enemy.CLASS_META_KEY, new FixedMetadataValue(
				QuestManagerPlugin.questManagerPlugin,
				this.enemyClassID
				));
		e.setCustomName(name);
		e.setCustomNameVisible(true);
		
		if (e instanceof LivingEntity)
			((LivingEntity) e).setRemoveWhenFarAway(false);
		
		spawnEntity(e);
	}
	
	public void setSpawningRegion(Region region) {
		this.spawningRegion = region;
	}
	
	/**
	 * Used to alter the spawned entity to represent the given enemy class.
	 * Enemies received through parameter input have been altered by parent classes already. This method
	 * is used to pass the entity down through the implementing classes and have it outfitted to match the
	 * enemy type and specification. The formatted entity should then be returned.
	 * @param base Base entity that is to be 'dressed up' by the implementing Enemy class. Already had a custom name and
	 * is of the right type, at the right location. In addition, the entity already possesses the classMetaKey.
	 */
	protected abstract void spawnEntity(Entity base);
	
	public String generateNewEnemyClassID() {
		return "_BASEenemyID_" + Enemy.enemyClassIDIndex++; 
	}
	
	@EventHandler
	public void onEnemyDeath(EntityDeathEvent e) {
		List<MetadataValue> metas = e.getEntity().getMetadata(CLASS_META_KEY);
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
				EnemyDeathEvent event = new EnemyDeathEvent(spawningRegion, e);
				Bukkit.getPluginManager().callEvent(event);
				handleDeath(e);
				return;
			}
		}

	}
	
	/**
	 * Called when an entity dies that has this class's metadata attached to it.
	 * @param e
	 */
	protected abstract void handleDeath(EntityDeathEvent e);
}
