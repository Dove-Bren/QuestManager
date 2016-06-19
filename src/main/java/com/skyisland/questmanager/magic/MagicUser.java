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

package com.skyisland.questmanager.magic;

import java.util.List;

import org.bukkit.entity.Entity;

public interface MagicUser {
	
	Entity getEntity();
	
	double getMP();
	
	void addMP(double amount);
	
	void addSpellPylon(SpellPylon pylon);
	
	List<SpellPylon> getSpellPylons();
	
	void clearSpellPylons();
	
	/**
	 * Cast the currently set up spell weaving spell. Spell cast, cost, difficulty, etc
	 * are determined by the spell the player has created with their current pylons.
	 * Pylons should be destroyed at this time, and checks should be made to player
	 * mana, skill level, etc
	 */
	void castSpellWeavingSpell();
}
