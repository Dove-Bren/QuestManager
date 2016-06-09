package com.skyisland.questmanager.scheduling;

public interface Alarmable<T> {
	/**
	 * Called when the scheduler is trying to remind the registered Alarmable object of something.
	 * The reference passed back is the same provided when registered.
	 * Receipt of this method indicates that the object is no longer registered with the scheduler.
	 */
	void alarm(T reference);
	
	/**
	 * Checks whether this method is equal to the passed.
	 * Tickable objects NEED to be able to tell if another object is the same as them, for lookup in a map.
	 */
	@Override
	boolean equals(Object o);
}
