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

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequirementUpdateEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	/**
	 * Keeps track of the requirement that called the update
	 */
	private Requirement requirement;
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	/**
	 * Creates an event with no requirement information.
	 * This is typically only used when an entire system-wide update is needed.
	 * To only call the neccessary and involved quests to update their information,
	 * use the {@link #RequirementUpdateEvent(Requirement)} constructor instead.
	 */
	public RequirementUpdateEvent() {
		this(null);
	}
	
	/**
	 * Constructs an event with given requirement information.
	 * This event triggers an update of quests involved with the given requirement only.
	 */
	public RequirementUpdateEvent(Requirement requirement) {
		this.requirement = requirement;
	}
	
	/**
	 * Returns the involved requirement, or null if none was passed on creation.
	 * Events with <i>no requirement information</i> are expected to perform system-wide
	 * updates and checks.
	 */
	public Requirement getRequirement() {
		return requirement;
	}
}
