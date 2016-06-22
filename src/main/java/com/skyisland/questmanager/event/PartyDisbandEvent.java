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

package com.skyisland.questmanager.event;

import com.skyisland.questmanager.party.Party;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/** An event triggered by a party being disbanded */
public class PartyDisbandEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	/** The disbanded party. */
	private final Party party;

	public PartyDisbandEvent(Party party) {
		this.party = party;
	}

	/**
	 * Returns the disbanded party for this event.
	 * @return the disbanded party.
	 */
	public Party getParty() {
		return party;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
