package com.SkyIsland.QuestManager.Scheduling;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;

import com.SkyIsland.QuestManager.QuestManagerPlugin;

/**
 * Waits a prescribed amount of time, and then ticks all registered Tickables
 * @author Skyler
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
	 * Return the current instanced DispersedScheduler.<br />
	 * If a scheduler has yet to be created, it will be created with default values
	 * from this call.
	 * @return
	 */
	public static IntervalScheduler getScheduler() {
		if (scheduler == null) {
			scheduler = new IntervalScheduler();
		}
	
		return scheduler;
	}
	
	private IntervalScheduler() {
		this.delay = defaultDelay;
		
		this.list = new LinkedList<Tickable>();
		
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
		for (Tickable t : list) {
			t.tick();
		}
	}

	@Override
	public void register(Tickable tick) {
		this.list.add(tick);
	}

	
}
