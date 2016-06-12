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

package com.skyisland.questmanager.effects;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Effect signaling to the player that the entity they just interacted with was correct.
 * This EFFECT was made with the 'slay' requirement in mind, where it'll display effects when you kill
 * the right kind of enemy.
 *
 */
public class EntityConfirmEffect extends QuestEffect {
	
	private static final Effect EFFECT = Effect.STEP_SOUND;
	
	@SuppressWarnings("deprecation")
	private static final int BLOCK_TYPE = Material.EMERALD_BLOCK.getId();
	
	/**
	 * The number of particals
	 */
	private int magnitude;
	
	/**
	 * 
	 * @param magnitude The number of particals, roughly
	 */
	public EntityConfirmEffect(int magnitude) {
		this.magnitude = magnitude;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void play(Entity player, Location effectLocation) {
		
		if (!(player instanceof Player)) {
			return;
		}
		
		for (int i = 0; i < magnitude; i++)
		((Player) player ) .playEffect(effectLocation, EFFECT, BLOCK_TYPE);
	}
}
