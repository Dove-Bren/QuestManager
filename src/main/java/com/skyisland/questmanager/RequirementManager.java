package com.skyisland.questmanager;

import java.util.HashMap;
import java.util.Map;

import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;
import com.skyisland.questmanager.quest.requirements.Requirement;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Keeps track of requirement keys and registered factories
 * @author Skyler
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
			QuestManagerPlugin.questManagerPlugin.getLogger()
				.warning("Unable to register requirement factory: key already exists [" + uniqueKey + "]");
			return false;
		}
		
		factories.put(uniqueKey, factory);
		
		return true;
	}
	
	/**
	 * Uses registered factories to instantiate a requirement from the given key and configuration file.<br />
	 * Keys must first be registered using {@link #registerFactory(String, RequirementFactory)}
	 * @param uniqueKey The key to look up, usually from the configuration file being loaded
	 * @param conf The configuration section used to instantiate the requirement. 
	 * @return A newly created requirement, or <b>null</b> on error.
	 */
	public Requirement instanceRequirement(String uniqueKey, Goal goal, ConfigurationSection conf) {
		if (!factories.containsKey(uniqueKey)) {
			QuestManagerPlugin.questManagerPlugin.getLogger()
			.warning("Unable to find registered requirement factory for key: [" + uniqueKey + "]");
			return null;
		}
		
		RequirementFactory<?> factory = factories.get(uniqueKey);
		
		return factory.fromConfig(goal, conf);
		
		
	}
	
}
