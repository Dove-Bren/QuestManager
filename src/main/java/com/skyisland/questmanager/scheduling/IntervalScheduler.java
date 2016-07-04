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

package com.skyisland.questmanager.scheduling;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Waits a prescribed amount of time, and then ticks all registered Tickables
 *
 */
public class IntervalScheduler extends Scheduler {

	/**
	 * List of Tickable entities, used when iterating
	 */
	private List<Tickable> list;
	
	/**
	 * The delay between two consequtive ticks, in minecraft ticks
	 */
	private long delay;
	
	/**
	 * How long to delay between ticks by default
	 */
	private static long defaultDelay = 100;
	
	private static IntervalScheduler scheduler = null;
	
	/**
	 * Return the current instanced DispersedScheduler.
	 * If a scheduler has yet to be created, it will be created with default values
	 * from this call.
	 */
	public static IntervalScheduler getScheduler() {
		if (scheduler == null) {
			scheduler = new IntervalScheduler();
		}
	
		return scheduler;
	}
	
	private IntervalScheduler() {
		this.delay = defaultDelay;
		
		this.list = new LinkedList<>();
		
		Bukkit.getScheduler().runTaskTimer(QuestManagerPlugin.questManagerPlugin
				, this, delay, delay);
	}
	
	/**
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * @return the list
	 */
	public List<Tickable> getRegisteredList() {
		return list;
	}

	@Override
	public void run() {
		//when run, just tick everything.
		//list.forEach(Tickable::tick);
		Iterator<Tickable> it = list.iterator();
		while (it.hasNext())
		if (it.next().tick())
			it.remove();
	}

	@Override
	public void register(Tickable tick) {
		this.list.add(tick);
	}
	
	@Override
	public void unregister(Tickable tick) {
		this.list.remove(tick);
	}
}
