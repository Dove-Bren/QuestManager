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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import com.skyisland.questmanager.player.Participant;
import com.skyisland.questmanager.quest.Goal;

/**
 * Represents a specific requirement needed to achieve a goal in a quest.
 * Requirements are the gnitty-gritty details of how to achieve a goal. Examples inlcude:
 * <ul>
 * <li>Kill a boss</li>
 * <li>Reach the village</li>
 * <li>etc</li>
 * </ul>
 * Requirements are <b>required</b> to perform their own event checking and are required to update
 * their containing goal when upon state change. In addition, requirements must 
 *
 */
public abstract class Requirement {
	
	protected String desc;
	
	private Goal goal;
	
	protected boolean state;
	
	protected Participant participants;
	

	/**
	 * Creates a requirement with an empty string for a description
	 */
	public Requirement(Goal goal) {
		this(goal, "");
	}
	
	/**
	 * Creates the parameterized requirement
	 */
	public Requirement(Goal goal, String description) {
		this.goal = goal;
		this.desc = description;
		participants = goal.getQuest().getParticipants();
	}
	
	public abstract void fromConfig(ConfigurationSection config) throws InvalidConfigurationException;
	
	public void sync() {
		this.participants = goal.getQuest().getParticipants();
	}
	
	/**
	 * Returns the goal this requirement belongs to
	 */
	public Goal getGoal() {
		return goal;
	}
	
	/**
	 * Returns the description of this requirement
	 */
	public abstract String getDescription();
	
	/**
	 * Returns whether or not the current requirement is completed.
	 * Requirements that may change back and forth may return false even after a call
	 * to this method had previously returned true. As a result, this method should always 
	 * be called each time a parent goal is checking its own completion status.
	 * This method makes an internal call to update state information to make sure that the
	 * value returned is current.
	 */
	public boolean isCompleted() {
		update();
		return state;
	}
	
	/**
	 * Sets this requirement to be active, listening for events and updating based on them
	 */
	public abstract void activate();
	
	/**
	 * Notifies the parent goal of a status chain, usually causing a re-evaluation of criteria
	 * to update the goal's status
	 */
	protected void updateQuest() {
		RequirementUpdateEvent e = new RequirementUpdateEvent(this);
		Bukkit.getPluginManager().callEvent(e);
	}
	
	/**
	 * Perform a check against requirement criteria to update state information with correct
	 * value.
	 */
	protected abstract void update();
}
