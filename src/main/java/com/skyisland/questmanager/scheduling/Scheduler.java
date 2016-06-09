package com.skyisland.questmanager.scheduling;


/**
 * Keeps track of registered entities and delivers ticks in a regular fashion
 * @author Skyler
 *
 */
public abstract class Scheduler implements Runnable {
	
	/**
	 * Register a Tickable entity to be ticked 
	 */
	public abstract void register(Tickable tick);
}
