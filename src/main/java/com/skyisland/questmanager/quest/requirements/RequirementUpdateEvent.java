package com.skyisland.questmanager.quest.requirements;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequirementUpdateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * Keeps track of the requirement that called the update
	 */
	private Requirement requirement;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
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
