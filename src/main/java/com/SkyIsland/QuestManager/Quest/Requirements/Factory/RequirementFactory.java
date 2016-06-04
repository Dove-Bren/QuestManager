package com.SkyIsland.QuestManager.Quest.Requirements.Factory;

import org.bukkit.configuration.ConfigurationSection;

import com.SkyIsland.QuestManager.Quest.Goal;
import com.SkyIsland.QuestManager.Quest.Requirements.Requirement;

public abstract class RequirementFactory<T extends Requirement> {
	
	public abstract T fromConfig(Goal goal, ConfigurationSection conf);
	
}
