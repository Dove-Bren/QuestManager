package com.skyisland.questmanager.quest.requirements.factory;

import com.skyisland.questmanager.quest.Goal;
import org.bukkit.configuration.ConfigurationSection;

import com.skyisland.questmanager.quest.requirements.Requirement;

public abstract class RequirementFactory<T extends Requirement> {
	
	public abstract T fromConfig(Goal goal, ConfigurationSection conf);
	
}