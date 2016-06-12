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

package com.skyisland.questmanager.magic.spell;

import java.util.Collection;
import java.util.Iterator;

import com.skyisland.questmanager.magic.MagicUser;
import com.skyisland.questmanager.QuestManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class SpellProjectile implements Runnable {
	
	private TargetSpell sourceSpell;
	
	private MagicUser caster;
	
	private int maxDistance;
	
	private int delay;
	
	private int perTick;
	
	private int distance;
	
	private Location location;
	
	private Vector direction;
	
	private Effect effect;
	
	public SpellProjectile(TargetSpell source, MagicUser caster, Location start, Vector direction,
			double speed, int maxDistance, Effect paceEffect) {
		this.sourceSpell = source;
		this.distance = 0;
		this.maxDistance = maxDistance;
		this.location = start.clone();
		this.direction = direction;
		this.caster = caster;
		this.effect = paceEffect;
		
		double rate = 20 / speed;
		
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
			distance++;
			//move forward a block, check for collision
			location.add(direction);
			
			if (effect != null) {
				location.getWorld().playEffect(location, effect, 0);
			}
			
			Collection<Entity> e = location.getWorld().getNearbyEntities(location, .5, .5, .5);
			filterLiving(e);
			
			if (!e.isEmpty()) {
				//hit an entity
				double rad = .5;
				while (e.size() > 1 && rad >= 0) {
					rad -= .1;
					e = location.getWorld().getNearbyEntities(location, rad, rad, rad);
					filterLiving(e);					
				}
				
				if (e.size() < 1) {
					//too far
					rad += .1; //step back
					e = location.getWorld().getNearbyEntities(location, rad, rad, rad);
					filterLiving(e);
				}
				
				if (e.isEmpty()) {
					//messed up
					System.out.println("Messed up on magic projectile!");
					continue;
					
				}
				
				LivingEntity hit = (LivingEntity) e.iterator().next();
				sourceSpell.onEntityHit(caster, hit);
				return;
			}
			
			//didn't hit entity. Did it hit a block?
			if (location.getBlock().getType().isSolid()) {
				sourceSpell.onBlockHit(caster, location);
				return;
			}
			
			//make sure we don't move too far
			if (distance > maxDistance) {
				return; //fizzle, reached end of line
			}
			
		}
		
		//end of loop, no return, so reschedule
		Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, delay);
	}
	
	private void filterLiving(Collection<Entity> set) {
		if (set == null || set.isEmpty()) {
			return;
		}
		
		Iterator<Entity> it = set.iterator();
		
		while (it.hasNext()) {
			Entity next = it.next();
			if (!(next instanceof LivingEntity) || next.equals(caster.getEntity())) {
				it.remove();
			}
		}
		
	}
}
