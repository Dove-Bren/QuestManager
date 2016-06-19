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

package com.skyisland.questmanager.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyDisbandEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	/**
	 * Keeps track of the party that disbanded
	 */
	private Party party;
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	/**
	 * Constructs an event with given party
	 */
	public PartyDisbandEvent(Party party) {
		this.party = party;
	}
	
	/**
	 * Returns the party that disbanded.
	 */
	public Party getParty() {
		return party;
	}
}
