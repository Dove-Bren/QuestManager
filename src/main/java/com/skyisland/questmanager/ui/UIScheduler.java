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

package com.skyisland.questmanager.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Schedule and timing handler for UI elements
 *
 */
public class UIScheduler implements Runnable {
	
	/**
	 * How precise this scheduler runs. The value here represents how often the scheduler
	 * appraises its scheduled tasks and runs things.
	 * This value is in second. In other words, the scheduler cycles every 1 / <i>RESOLUTION</i>
	 * seconds.
	 */
	public static final float RESOLUTION = .5f;
	
	private static UIScheduler scheduler;
	
	public static UIScheduler getScheduler() {
		if (scheduler == null) {
			scheduler = new UIScheduler();
		}
		
		return scheduler;
	}
	
	private static class Record {
		
		private UITickable task;
		
		private int cycles;
		
		public Record(UITickable task, int cycles) {
			this.task = task;
			this.cycles = cycles;
		}

		/**
		 * @return the task
		 */
		public UITickable getTask() {
			return task;
		}

		/**
		 * @return the cycles
		 */
		public int getCycles() {
			return cycles;
		}
		
	}
	
	private Map<Integer, Record> records;
	
	private Random rand;
	
	private int cycle;
	
	private UIScheduler() {
		records = new HashMap<>();
		rand = new Random();
		cycle = 0;
		
		//schedule ourselves
		Bukkit.getScheduler().runTaskTimer(QuestManagerPlugin.questManagerPlugin, this,
				(long) Math.round(20 * UIScheduler.RESOLUTION) ,
				(long) Math.round(20 * UIScheduler.RESOLUTION));
	}
	
	/**
	 * Registers the task to be executed once over <i>n</i> cycles.
	 * Once cycle is defined as 1 / {@link #RESOLUTION RESOLUTION} seconds.
	 */
	public int schedule(UITickable task, int n) {
		Record record = new Record(task, n);
		int key = rand.nextInt();
		
		while (records.containsKey(key)) {
			key = rand.nextInt();
		}
		
		records.put(key, record);
		
		return key;
	}
	
	/**
	 * Registers the task to be executed every <i>n</i> seconds.
	 * The precision of the scheduling is limited by the {@link #RESOLUTION RESOLUTION}.
	 * This method will round to the nearest number of cycles, and instead only serves as a
	 * convenience method to allow scheduling for a target amount of time instead of an abstract
	 * number of cycles.
	 * @return an integer key used for unregistering the task
	 */
	public int schedule(UITickable task, float n) {
		
		int cycles = Math.max(1, Math.round(n / UIScheduler.RESOLUTION));
		return schedule(task, cycles);
		
	}
	
	/**
	 * Unregisters the task assigned to the given Identifying key
	 */
	public void unschedule(int key) {
		records.remove(key);
	}
	
	public void run() {
		
		cycle++;
		
		if (records.isEmpty()) {
			return;
		}
		
		for (Record record : records.values()) {
			if (cycle % record.getCycles() == 0) {
				record.getTask().tick();
			}
		}
		
	}
}
