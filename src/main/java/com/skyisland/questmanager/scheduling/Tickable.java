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

package com.skyisland.questmanager.scheduling;

/**
 * An object is Tickable if they can be ticked.
 * All tickable objects can register with a scheduler and then be visited by
 * ticks.
 * <p>
 * Some easy applications of ticks are movement patterns, regular regeneration, etc
 *
 */
public interface Tickable {
	
	/**
	 * Performs a scheduled 'tick'.
	 * This method can mean anything a Tickable class wants it to be. Scheduled
	 * Tickable classes receive calls to this on a scheduled and regular basis
	 * @return <i>true</i> if the ticked instance no longer wishes to be ticked
	 */
	boolean tick();
}
