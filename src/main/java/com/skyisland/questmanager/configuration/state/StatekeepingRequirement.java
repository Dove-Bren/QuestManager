package com.skyisland.questmanager.configuration.state;

import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Keeps state information
 * @author Skyler
 *
 */
public interface StatekeepingRequirement {
	
	
	RequirementState getState();
	
	void loadState(RequirementState state) throws InvalidConfigurationException;
	
	/**
	 * Perform a stop to the requirement. This usually entails getting rid of entities, etc
	 * whose information is stored in the state information.
	 */
	void stop();
}
