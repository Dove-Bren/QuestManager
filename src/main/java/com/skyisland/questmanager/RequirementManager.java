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

package com.skyisland.questmanager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.requirements.Requirement;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;

/**
 * Keeps track of requirement keys and registered factories
 *
 */
public class RequirementManager {
	
	private Map<String, RequirementFactory<?>> factories;
	
	/**
	 * Creates a new, empty RequirementManager
	 */
	public RequirementManager() {
		factories = new HashMap<>();
	}
	
	/**
	 * Registers the provided factory with the provided key.
	 * @param uniqueKey The key to register the factory to. This is the key used in the configuration file to
	 * invoke this factory
	 * @param factory The factory
	 * @return Whether the registration was successful. Failed registration usually is from non-unique keys.
	 */
	public boolean registerFactory(String uniqueKey, RequirementFactory<?> factory) {
		if (factories.containsKey(uniqueKey)) {
			QuestManagerPlugin.logger
				.warning("Unable to register requirement factory: key already exists [" + uniqueKey + "]");
			return false;
		}
		
		factories.put(uniqueKey, factory);
		
		return true;
	}
	
	/**
	 * Uses registered factories to instantiate a requirement from the given key and configuration file.
	 * Keys must first be registered using {@link #registerFactory(String, RequirementFactory)}
	 * @param uniqueKey The key to look up, usually from the configuration file being loaded
	 * @param conf The configuration section used to instantiate the requirement. 
	 * @return A newly created requirement, or <b>null</b> on error.
	 */
	public Requirement instanceRequirement(String uniqueKey, Goal goal, ConfigurationSection conf) {
		if (!factories.containsKey(uniqueKey)) {
			QuestManagerPlugin.logger
			.warning("Unable to find registered requirement factory for key: [" + uniqueKey + "]");
			return null;
		}
		
		RequirementFactory<?> factory = factories.get(uniqueKey);
		
		return factory.fromConfig(goal, conf);
		
		
	}
}
