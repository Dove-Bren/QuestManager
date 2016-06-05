package com.skyisland.questmanager.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyDisbandEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * Keeps track of the party that disbanded
	 */
	private Party party;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	/**
	 * Constructs an event with given party<br />
	 * @param requirement
	 */
	public PartyDisbandEvent(Party party) {
		this.party = party;
	}
	
	/**
	 * Returns the party that disbanded.
	 * @return
	 */
	public Party getParty() {
		return party;
	}
}
