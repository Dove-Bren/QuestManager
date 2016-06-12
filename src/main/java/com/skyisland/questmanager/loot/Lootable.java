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

package com.skyisland.questmanager.loot;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Specifies a given object keeps a list of loot you can get from it.
 * Example application are enemies (which have loot on drop) or random chests.
 * @author Skyler
 *
 */
public interface Lootable {
	
	List<Loot> getLoot();
	
	/**
	 * Standard method of selecting a piece of loot from a list of loot items.
	 * This method will return null if the argument list is either null or empty.
	 * <p>
	 * The algorithm this method follows the definition of {@link Loot}'s weight member; the chance a piece
	 * of loot is selected is equal to
	 * &nbsp;&nbsp;&nbsp;&nbsp;(<i>weight</i>) / (<i>Pool weight total</i>)
	 * </p>
	 */
	static Loot pickLoot(List<Loot> loot) {
		if (loot == null || loot.isEmpty()) {
			return null;
		}
		
		Random rand = new Random();
		Collections.shuffle(loot, rand);
		
		double max = 0;
		for (Loot l : loot) {
			max += l.getWeight();
		}
		
		double index = rand.nextDouble() * max;
		for (Loot l : loot) {
			index -= l.getWeight();
			if (index <= 0) {
				//reached piece of loot to select
				return l;
			}
		}
		
		//we reached end with a positive index. Something went wrong (probably double precious error)
		//return end of list.
		return loot.get(loot.size() - 1);
		
	}
}
