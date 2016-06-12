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

import java.util.Random;

import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Effect where particles circle the player constantly.
 * @author Skyler
 *
 */
public class RingEffect extends EntityEffect implements ConstantEffect, Alarmable<Integer> {

	public static Random rand = new Random();
	
	/**
	 * The particle to create
	 */
	private Effect effect;
	
	private int data;
	
	/**
	 * How many per tick to create
	 */
	private int count; 
	
	/**
	 * How many points to create the particles at
	 */
	private int centroids;
	
	private Entity entity;
	
	private int counter;
	
	/**
	 * AuraEffect that creates the given particle effect.
	 * @param effect The particle to create
	 * @param points the number of points in the ring to spawn particles from
	 * @param count How many to create per 5 ticks (.25 seconds)
	 */
	public RingEffect(Effect effect, int points, int count, int data) {
		this.effect = effect;
		this.centroids = points;
		this.count = count;
		this.data = data;
		
		counter = 0;
	}
	
	public RingEffect(Effect effect, int points, int count) {
		this(effect, points, count, 0);
	}
	
	public RingEffect(Effect effect, int points) {
		this(effect, points, 5);
	}
	
	public RingEffect(Effect effect) {
		this(effect, 1);
	}
	
	@Override
	public void play(Entity entity) {
		Alarm.getScheduler().schedule(this, 1, .25);
		this.entity = entity;
	}

	@Override
	public void stop() {
		Alarm.getScheduler().unregister(this);
	}

	@Override
	public void alarm(Integer reference) {
		//disregard reference. We only have one alarm
		Location anchor = entity.getLocation().clone().add(0, 1, 0);//, tmp;
		float dx, dz;
		float offset = (float) ((2 * Math.PI) / centroids);
		
		//four secodns for full rotation
		float base = (float) (((2 * Math.PI) / (float) 16) * (float) (counter % 16));
		
		for (int i = 0; i < count; i++) {
			for (int c = 0; c < centroids; c++) {
				dx = (float) Math.cos(base + (offset * c));
				dz = (float) Math.sin(base + (offset * c));
				anchor.add(dx, 0, dz);
				anchor.getWorld().spigot().playEffect(anchor, effect, 0, data, 0, 0, 0, 0, count, 16);
				anchor.add(-dx, 0, -dz);
			}
		}
		
		counter++;
		Alarm.getScheduler().schedule(this, 1, .25);
	}
}
