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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.skyisland.questmanager.QuestManagerPlugin;

public final class SummonManager {
	
	private List<Summon> summons;
	
	private int summonLimit;
	
	private Map<UUID, Integer> playerCount;
	
	public SummonManager() {
		this.summons = new LinkedList<>();
		playerCount = new HashMap<>();
		summonLimit = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSummonLimit();
	}
	
	public boolean registerSummon(Player caster, Summon summon) {
		Integer count = playerCount.get(caster.getUniqueId());
		if (count == null) {
			count = 0;
		}
		
		if (count < summonLimit) {
			registerSummon(summon);
			count++;
			playerCount.put(caster.getUniqueId(), count);
			return true;
		}

		return false;
	}
	
	public void registerSummon(Summon summon) {
		summons.add(summon);
	}
	
	public void unregisterSummon(Summon summon) {
		summons.remove(summon);
		if (playerCount.containsKey(summon.getCasterID())) {
			int count = playerCount.get(summon.getCasterID());
			count = Math.max(count - 1, 0);
			playerCount.put(summon.getCasterID(), count);
		}
	}
	
	/**
	 * Goes through all summons and removes them, also clearing this manager's list
	 */
	public void removeSummons() {
		summons.forEach(Summon::remove);
		
		summons.clear();
	}
}
