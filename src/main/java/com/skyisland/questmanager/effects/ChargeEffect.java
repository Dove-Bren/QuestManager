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

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Charging-like effect.
 * A circle of particles appears around the target, then moves up quickly
 * @author Skyler
 *
 */
public class ChargeEffect extends QuestEffect implements Runnable {
	
	private Effect effect;
	
	private Entity player;
	
	private int count;
	
	public ChargeEffect(Effect effect) {
		this.effect = effect;
	}
	
	@Override
	public void play(Entity player, Location location) {
		this.player = player;
		count = 0;
		spark();
		Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, 
				4);
	}

	@Override
	public void run() {
		count++;
		spark();
		if (count < 4) {
			Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, 
					4);			
		}
	}
	
	private void spark() {
		Location loc = player.getLocation().clone(),
				tmp;
		loc.add(0, .4 * count, 0);
		for (int i = 0; i < 6; i++) {
			tmp = loc.clone();
			tmp.add(Math.cos(i *Math.PI / 3), 0, Math.sin(i * Math.PI / 3));
			player.getWorld().playEffect(tmp, effect, 0);
		}
	}
}
