package com.skyisland.questmanager.scheduling;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Gathers a list of scheduled Tickable entities and goes through ticking each
 * after the others. Each registered Tickable entity causes a measured wait
 * before the next creature is ticked, causing a spread out tick chain.
 * @author Skyler
 *
 */
public class DispersedScheduler extends Scheduler {

	/**
	 * List of Tickable entities, used when iterating
	 */
	private List<Tickable> list;

	/**
	 * Iterator to keep track of where we are in our changing list
	 */
	private ListIterator<Tickable> iterator;
	
	/**
	 * The delay between two consequtive ticks, in minecraft ticks
	 */
	private long delay;
	
	/**
	 * How long to wait after finishing a tick run before starting another
	 */
	private long rest;
	
	/**
	 * Keeps track of whether the scheduler is currently in rest mode
	 */
	private boolean resting;
	
	/**
	 * How long to delay between ticks by default
	 */
	private static long defaultDelay = 40;
	
	private static long defaultRest = 200;
	
	private static DispersedScheduler scheduler = null;
	
	/**
	 * Return the current instanced DispersedScheduler.<br />
	 * If a scheduler has yet to be created, it will be created with default values
	 * from this call.
	 * @return
	 */
	public static DispersedScheduler getScheduler() {
		if (scheduler == null) {
			scheduler = new DispersedScheduler();
		}
	
		return scheduler;
	}
	
	private DispersedScheduler() {
		this.delay = defaultDelay;
		this.rest = defaultRest;
		
		this.list = new LinkedList<>();
		
		resting = true;
		Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin
				, this, rest);
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
	 * @return the rest
	 */
	public long getRest() {
		return rest;
	}

	/**
	 * @param rest the rest to set
	 */
	public void setRest(long rest) {
		this.rest = rest;
	}

	/**
	 * @return the list
	 */
	public List<Tickable> getRegisteredList() {
		return list;
	}

	@Override
	public void run() {
		//depends on whether we are resting or not
		//resting means we need to start the list
		//not resting means we need to keep stepping through the list
		if (resting) {
			//was resting, start list
			
			if (list == null || list.isEmpty()) {
				resting = true;
				Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin
						, this, rest);
				return;
			}
			
			resting = false;
			iterator = list.listIterator();
			
			iterator.next().tick();
			Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin
					, this, delay);
			
		} else {
			//already going through list. Keep going, or start rest
			if (!iterator.hasNext()) {
				//finished list
				resting = true;
				Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin
						, this, rest);
			} else {
				//still more in the list to go
				iterator.next().tick();
				Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin
						, this, delay);
			}
		}
	}

	@Override
	public void register(Tickable tick) {
		this.list.add(tick);
	}
}
