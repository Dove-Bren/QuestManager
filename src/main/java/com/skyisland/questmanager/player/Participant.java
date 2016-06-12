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

import java.util.Collection;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * An entity involved in a quest.
 * Specifically, a participant can either be a single player or a collection of players. It's
 * up to specific implementations of quests and requirements to specify which are allowed.
 * @author Skyler
 *
 */
public interface Participant extends ConfigurationSerializable {
	
	/**
	 * Return the involved participants. This can either be a collection of one element,
	 * or a larger set of a group of participants.
	 * The returned collection is intended to be iterated over and a particular action/check
	 * performed on each member.
	 * TODO: decouple implementation :'(
	 */
	Collection<QuestPlayer> getParticipants();
	
	/**
	 * Get a string-version of the ID that can be used to identify this Participant.
	 */
	String getIDString();
}
