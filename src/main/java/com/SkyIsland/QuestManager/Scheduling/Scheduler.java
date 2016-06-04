package com.SkyIsland.QuestManager.Scheduling;


/**
 * Keeps track of registered entities and delivers ticks in a regular fashion
 * @author Skyler
 *
 */
public abstract class Scheduler implements Runnable {
	
	/**
	 * Register a Tickable entity to be ticked 
	 * @param tick
	 */
	public abstract void register(Tickable tick);
}
