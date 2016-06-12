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

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Depicts a QM enemy, which can be created or destroyed as the world loads and unloads.
 * Each enemy object represents a unique, spawnable enemy type. Each instance of an enemy is created from this
 * class when spawning things, but this class doesn't hold instances.
 */
public abstract class Enemy implements ConfigurationSerializable {
	
	protected EntityType type;
	
	protected String name;
	
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
	}
	
	public void spawn(Location loc) {
		Entity e = loc.getWorld().spawnEntity(loc, type);
		e.setMetadata(Enemy.CLASS_META_KEY, new FixedMetadataValue(
				QuestManagerPlugin.questManagerPlugin,
				this.enemyClassID
				));
		e.setCustomName(name);
	}
	
	public String generateNewEnemyClassID() {
		return "_BASEenemyID_" + Enemy.enemyClassIDIndex++; 
	}
}
