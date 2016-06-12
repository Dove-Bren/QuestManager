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

package com.skyisland.questmanager.configuration.state;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Holds state information about a requirement.
 * It's worth noting that most requirements don't need to store state information! They'll
 * usually be re-evaluated constantly, so there's no need unless they need something
 * special when restarting-from-state. Examples include:
 * <ul>
 * <li>A boss requirement, which needs to recreate teh boss in the same locations (and same hp?)
 * <li>A time limit requirement, which needs to store how much time is left</li>
 * </ul>
 * 
 *
 */
public class RequirementState {
	
	private ConfigurationSection config;
	
	public RequirementState(ConfigurationSection config2) {
		this.config = config2;
	}
	
	public ConfigurationSection getConfig() {
		return config;
	}
}
