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

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Holds registered skills, configuration for skills in general, and some basic skill types.
 *
 */
public class SkillManager {
	
	private Set<Skill> skills;
	
	
	public SkillManager() {
		skills = new HashSet<>();
	}
	
	public void registerSkill(Skill skill) {
		skills.add(skill);
	}
	
	public Set<Skill> getAllSkills() {
		return skills;
	}
	
	public Set<Skill> getSkills(Skill.Type skillType) {
		Set<Skill> ret = new TreeSet<>();
		for (Skill skill : skills) {
			if (skill.getType() == skillType) {
				ret.add(skill);
			}
		}
		
		return ret;
	}
}
