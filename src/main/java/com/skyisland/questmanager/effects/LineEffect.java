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
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.skyisland.questmanager.QuestManagerPlugin;

public class LineEffect extends QuestEffect implements Runnable {

	private Location cur;
	
	private Location target;
	
	private Vector dir;
	
	private double speed;
	
	private static final int TICKS_PER_SECOND = 20;
	
	private int delay;
	
	private int perTick;
	
	private Effect effect;

	public LineEffect(Effect effect, double blocksPerSecond) {
		this.speed = blocksPerSecond;
		this.effect = effect;
	}
	
	@Override
	public void play(Entity player, Location location) {
		
		// TODO Auto-generated method stub
		if (player instanceof LivingEntity) {
			cur = ((LivingEntity) player).getEyeLocation();
		} else {
			cur = player.getLocation().clone().add(0, 1.5, 0);
		}
		
		dir = location.toVector().subtract(
				player.getLocation().toVector());
		dir.normalize();
		target = location;
		
		double rate = TICKS_PER_SECOND / speed;
		
		if (rate >= 1) {
			perTick = 1;
			delay = (int) Math.round(rate);
		} else {
			delay = 1;
			perTick = (int) Math.round(1 / rate);
		}
		
		Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, delay);
	}

	@Override
	public void run() {
		
		for (int i = 0; i < perTick; i++) {	
			cur.add(dir);
			spark();
			if (cur.distance(target) < 2) {
				break;
			}
		}
		if (cur.distance(target) > 2) {
			Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, delay);
		}
	}
	
	private void spark() {
		cur.getWorld().playEffect(cur, effect, 0);
	}
}
