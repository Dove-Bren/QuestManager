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

package com.skyisland.questmanager.quest.requirements;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.state.RequirementState;
import com.skyisland.questmanager.configuration.state.StatekeepingRequirement;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.event.CraftEvent;
import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;

/**
 * Requirement that a participant must talk to an npc.
 *
 */
public class CraftRequirement extends Requirement implements Listener, StatekeepingRequirement {
	
	public static class CraftRequirementFactory extends RequirementFactory<CraftRequirement> {
		
		public CraftRequirement fromConfig(Goal goal, ConfigurationSection config) {
			CraftRequirement req = new CraftRequirement(goal);
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	private String craftName;
	
	private Material craftType;
	
	private int count;
	
	private int targetCount;
	
	private CraftRequirement(Goal goal) {
		super(goal);
		count = 0;
	}
	
	public CraftRequirement(Goal goal, Material craftType, String craftName, int targetCount) {
		this(goal);
		this.state = false;
		this.craftName = craftName;
		this.craftType = craftType;
		this.targetCount = targetCount;
	}

	@Override
	public void activate() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	

	/**
	 * Catches player crafts and checks to see if they match what we need
	 */
	@EventHandler
	public void onCraft(CraftEvent e) {
		
		if (state) {
			HandlerList.unregisterAll(this);
			return;
		}

		for (QuestPlayer p : participants.getParticipants())
		if (e.getPlayer().equals(p)) {
			count++;
			update();
			break;
		}
		
	}
	
	/**
	 * Nothing to do
	 */
	@Override
	public void update() {
		if (state)
			return;
		
		if (count >= targetCount) {
			state = true;
			HandlerList.unregisterAll(this);
			updateQuest();
		}
	}

	@Override
	public void fromConfig(ConfigurationSection config) throws InvalidConfigurationException {
		/*
		 * type: craft
		 * crafttype: [MATERIAL]
		 * craftname: [string]
		 * count: [int]
		 */
		
		if (!config.contains("type") || !config.getString("type").equals("craft")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected 'craft' but got " + config.get("type", "null"));
		}
		
		this.craftType = Material.matchMaterial(config.getString("craftType"));
		this.craftName = config.getString("craftName");
		this.targetCount = config.getInt("count");
		
		this.desc = config.getString("description", "Craft " + targetCount + " "
				+ (craftName == null ? YamlWriter.toStandardFormat(craftType.name()) : craftName) + "(s)");
	}
	
	public void stop() {
		HandlerList.unregisterAll(this);
	}
	
	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public RequirementState getState() {
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("count", count);
		
		return new RequirementState(config);
	}

	@Override
	public void loadState(RequirementState state) throws InvalidConfigurationException {
		this.count = state.getConfig().getInt("count");
		
		update();
	}
}
