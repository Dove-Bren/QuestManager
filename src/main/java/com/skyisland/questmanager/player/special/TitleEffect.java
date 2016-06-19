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

package com.skyisland.questmanager.player.special;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;

public class TitleEffect implements Alarmable<Integer> {
	
	protected static class effectRunnable {
		
		public void run(Location loc) {
			;
		}
	}
	
	public enum TitleEffectType {
		
		SLIMEKING(new effectRunnable(){
			public void run(Location loc) {
				Location l = loc.clone();
				l.add(rand.nextDouble(), 0, rand.nextDouble());
				Slime s = (Slime) l.getWorld().spawnEntity(loc, EntityType.SLIME);
				s.setSize(1);
			}
		});
		
		private static Random rand = new Random();
		
		private effectRunnable effect;
		
		TitleEffectType(effectRunnable effect) {
			this.effect = effect;
		}
		
		public effectRunnable getEffect() {
			return effect;
		}
	}
	
	public TitleEffect() {
		Alarm.getScheduler().schedule(this, 1, 120);
	}
	
	@Override
	public void alarm(Integer key) {
		for (QuestPlayer qp : QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayers()) {
			if (qp.getPlayer().isOnline())
			if (qp.getTitle() != null)
			if (qp.getTitle().contains("Slime King")) {
				TitleEffectType.SLIMEKING.getEffect().run(qp.getPlayer().getPlayer().getLocation());
				continue;
			}
		}
		
		Alarm.getScheduler().schedule(this, 1, 120);
	}
}
