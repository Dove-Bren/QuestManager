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
package com.skyisland.questmanager.party;

import com.skyisland.questmanager.QuestManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

/**
 * Handles Events which affect parties.
 */
class PartyEventListener implements Listener {

	PartyEventListener() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}

	/**
	 * Update the party scoreboard if a party member is damaged.
	 * @param e an {@link EntityDamageEvent}.
	 */
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {

		if (e.isCancelled() || !(e.getEntity() instanceof Player)) {
			return;
		}

		PartyRepository.get(e.getEntity().getUniqueId()).ifPresent(PartyManager::updateScoreboard);
	}

	/**
	 * Update the party scoreboard if a party member regenerates health.
	 * @param e an {@link EntityRegainHealthEvent}.
	 */
	@EventHandler
	public void onPlayerRegen(EntityRegainHealthEvent e) {

		if (e.isCancelled() || !(e.getEntity() instanceof Player)) {
			return;
		}

		PartyRepository.get(e.getEntity().getUniqueId()).ifPresent(PartyManager::updateScoreboard);
	}
}
