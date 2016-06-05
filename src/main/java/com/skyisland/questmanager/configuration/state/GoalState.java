package com.skyisland.questmanager.configuration.state;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * holds goal state
 */
public class GoalState {
	
	private String name; //for comparison
	
	private List<RequirementState> requirementStates;
	
	public GoalState() {
		name = "";
		
		requirementStates = new LinkedList<>();
	}


	public void load(ConfigurationSection config) throws InvalidConfigurationException {
		if (!config.contains("type") || !config.getString("type").equals("goalstate") 
				|| !config.contains("name")) {
			throw new InvalidConfigurationException();
		}
		
		name = config.getString("name");
		
		requirementStates = new LinkedList<>();
		
		if (config.contains("requirementStates"))
		for (String reqKey : config.getConfigurationSection("requirementStates").getKeys(false)) {
			requirementStates.add(
					new RequirementState(
							config.getConfigurationSection("requirementStates")
							.getConfigurationSection(reqKey))
					);
		}
	}
	
	public void save(File file) throws IOException {
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("type", "goalstate");
		
		config.set("name", name);
		
		int i = 1;
		for (RequirementState state : requirementStates) {
			config.set("requirementStates." + i, state.getConfig());
			i++;
		}
		
		//config.set("goals", requirementStates);
		
		config.save(file);
	}
	
	public YamlConfiguration asConfig() {

		YamlConfiguration config = new YamlConfiguration();
		
		config.set("type", "goalstate");
		
		config.set("name", name);
		
		int i = 1;
		for (RequirementState state : requirementStates) {
			config.set("requirementStates." + i, state.getConfig());
			i++;
		}
		
		return config;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the requirementStates
	 */
	public List<RequirementState> getRequirementStates() {
		return requirementStates;
	}
	
	public void addRequirementState(RequirementState state) {
		requirementStates.add(state);
	}
}
