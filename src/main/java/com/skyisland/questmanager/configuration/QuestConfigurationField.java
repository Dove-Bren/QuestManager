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

package com.skyisland.questmanager.configuration;

import java.util.LinkedList;

import com.skyisland.questmanager.quest.Goal;
import org.bukkit.inventory.ItemStack;

/**
 * Holds key and default information for defined quest config fields.
 * Fields|Keys|Defaults are:
 * <ul>
 * <li>NAME | name | "Generated Quest"</li>
 * <li>DESCRIPTION | description | "No Description"</li>
 * <li>GOALS | goals | <i>Empty List</i></li>
 * <li>SAVESTATE | savestate | false</li>
 * <li>SESSION | issession | false</li>
 * <li>REPEATABLE | repeatable | false </li>
 * <li>USEPARTY | useparty | false </li>
 * <li>REQUIREPARTY | requireparty | false </li>
 * <li>FAILONDEATH | failondeath | false </li>
 * <li>STARTLOC | startlocation | <i>null</i></li>
 * <li>EXIT | exitlocation | <i>null</i></li>
 * <li>NPCS | npcs | <i>Empty List</i></li>
 * <li>START | start | <i>null</i> </li>
 * <li>PREREQS | requiredquests | <i>Empty List</i></li>
 * <li> END | end | same </li>
 * <li> FAME | fame | 100 </li>
 * <li> REWARDS | rewards | <i>Empty List</i> </li>
 * <li> MONEYREWARD | moneyreward | 0 </li>
 * <li> TITLEREWARD | titlereward | <i>null</i> </li>
 * <li> SPELLREWARD | spellreward | <i>null</i></li>
 * <li> ENDHINT | turninhint | "Turn In"</li>
 * </ul>
 *
 */
public enum QuestConfigurationField {
	
	NAME("name", "Generated Quest"),
	DESCRIPTION("description", "No Description"),
	GOALS("goals", new LinkedList<Goal>()),
	SAVESTATE("savestate", false),
	REPEATABLE("repeatable", false),
	SESSION("issession", false),
	NPCS("npcs", new LinkedList<Goal>()),
	START("start", null),
	PREREQS("requiredquests", new LinkedList<String>()),
	REQUIREPARTY("requireparty", false),
	FAILONDEATH("failondeath", false),
	STARTLOC("startlocation", null),
	EXIT("exitlocation", null),
	USEPARTY("useparty", false),
	END("end", "same"),
	FAME("fame", 100),
	REWARDS("rewards", new LinkedList<ItemStack>()),
	MONEYREWARD("moneyreward", 0),
	TITLEREWARD("titlereward", null),
	SPELLREWARD("spellreward", null),
	ENDHINT("turninhint", "Turn In");
	
	private Object def;
	
	private String key;
	
	QuestConfigurationField(String key, Object def) {
		this.def = def;
		this.key = key;
	}
	
	public Object getDefault() {
		return def;
	}
	
	public String getKey() {
		return key;
	}
}
