package com.skyisland.questmanager.scheduling;

/**
 * An object is Tickable if they can be ticked.<br />
 * All tickable objects can register with a scheduler and then be visited by
 * ticks.<br />
 * <p>
 * Some easy applications of ticks are movement patterns, regular regeneration, etc
 * @author Skyler
 *
 */
public interface Tickable {
	
	/**
	 * Performs a scheduled 'tick'.<br />
	 * This method can mean anything a Tickable class wants it to be. Scheduled
	 * Tickable classes receive calls to this on a scheduled and regular basis
	 */
	public void tick();
	
}
