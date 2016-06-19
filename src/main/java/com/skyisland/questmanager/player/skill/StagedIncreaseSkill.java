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

package com.skyisland.questmanager.player.skill;

import com.skyisland.questmanager.player.QuestPlayer;

/**
 * A skill that doesn't just have one way to perform it -- and therefore, requires
 * more than one way to calculate xp gains.
 *
 */
public interface StagedIncreaseSkill {

	/**
	 * Performs a small action of the skill, resulting in smaller awarded xp.
	 * Adds experience to the skill, standardized as the specified amount for performing an action of
	 * level <i>actionLevel</i> and either succeeding or failing, as determiend by <i>fail</i>
	 *
	 * @param actionLevel The level of the action that was performed. Maybe cooking salmon is a lvl 30 task, for example
	 * @param fail        whether or not the action was failed. May or not award xp on failure, as determined by config
	 */
	void performMinor(QuestPlayer participant, int actionLevel, boolean fail);

	/**
	 * Performs a large action of the skill, resulting in larger awarded xp.
	 * Adds experience to the skill, standardized as the specified amount for performing an action of
	 * level <i>actionLevel</i> and either succeeding or failing, as determiend by <i>fail</i>
	 *
	 * @param actionLevel The level of the action that was performed. Maybe cooking salmon is a lvl 30 task, for example
	 * @param fail        whether or not the action was failed. May or not award xp on failure, as determined by config
	 */
	void performMajor(QuestPlayer participant, int actionLevel, boolean fail);
}
