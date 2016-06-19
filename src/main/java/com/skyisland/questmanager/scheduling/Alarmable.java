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

public interface Alarmable<T> {
	/**
	 * Called when the scheduler is trying to remind the registered Alarmable object of something.
	 * The reference passed back is the same provided when registered.
	 * Receipt of this method indicates that the object is no longer registered with the scheduler.
	 */
	void alarm(T reference);
	
	/**
	 * Checks whether this method is equal to the passed.
	 * Tickable objects NEED to be able to tell if another object is the same as them, for lookup in a map.
	 */
	@Override
	boolean equals(Object o);
}
