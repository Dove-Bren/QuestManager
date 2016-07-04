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

package com.skyisland.questmanager.npc;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * An NPC that is a monster for a quest.
 *
 */
public class QuestMonsterNPC extends NPC {
	
	@Override
	public Map<String, Object> serialize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean tick() {
		return false;
	}

	@Override
	protected void interact(Player player) {
		;
	}
	
	@Override
	@EventHandler
	public void onEntityHurt(EntityDamageEvent e) {
		
	}
}
