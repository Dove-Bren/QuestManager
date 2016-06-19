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
 * Effect where particles are constantly created around a player, as if an aura.
 *
 */
public class AuraEffect extends EntityEffect implements ConstantEffect, Alarmable<Integer> {

	public static final Random RANDOM = new Random();
	
	/**
	 * The particle to create
	 */
	private Effect effect;
	
	private int data;
	
	/**
	 * How many per tick to create
	 */
	private int count; 
	
	private Entity entity;
	
	/**
	 * AuraEffect that creates the given particle effect.
	 * @param effect The particle to create
	 * @param count How many to create per 5 ticks (.25 seconds)
	 */
	public AuraEffect(Effect effect, int count, int data) {
		this.effect = effect;
		this.count = count;
		this.data = data;
	}
	
	public AuraEffect(Effect effect, int count) {
		this(effect, count, 0);
	}
	
	public AuraEffect(Effect effect) {
		this(effect, 5);
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
		Location anchor = entity.getLocation().clone().add(0, 1, 0), tmp;
		float dir;
		for (int i = 0; i < count; i++) {
			tmp = anchor.clone();
			dir = (float) (RANDOM.nextFloat() * (2 * Math.PI));
			tmp.add(Math.cos(dir), 0, Math.sin(dir));
			
			tmp.getWorld().playEffect(tmp, effect, data);
		}
		
		Alarm.getScheduler().schedule(this, 1, .25);
	}
}
