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

package com.skyisland.questmanager.configuration.utils;

/**
 * Group unique ID's! For ID'ing partys
 * @author Skyler
 *
 */
public class GUID {
	
	private static long nextID = 1L;
	
	private long ID;
	
	public static GUID generateGUID() {
		GUID n = new GUID(nextID);
		nextID++;
		return n;
	}
	
	private GUID(long ID) {
		this.ID = ID;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GUID)) {
			return false;
		}
		
		GUID other = (GUID) o;
		return (other.ID == ID);
	}
	
	@Override
	public String toString() {
		return "GI_" + this.ID;
	}
	
	public static GUID valueOf(String string) {
		if (!string.startsWith("GI_")) {
			return null;
		}
		
		return new GUID(Long.parseLong(string.substring(3)));
	}
}
