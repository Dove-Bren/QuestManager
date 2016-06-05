package com.skyisland.questmanager.scheduling;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import com.skyisland.questmanager.QuestManagerPlugin;

public class Alarm {
	
private static final int ticksPerSecond = 20;
	
	private static Alarm scheduler;
	
	private Map<Alarmable<?>, Reminder<?>> map;
	
	private class Reminder<E> extends BukkitRunnable {
		
		private E key;
		
		private Alarmable<E> owner;
		
		private Reminder(Alarmable<E> owner, E key) {
			this.owner = owner;
			this.key = key;
		}

		@Override
		public void run() {
			scheduler.notify(this);
		}
		
		private E getKey() {
			return key;
		}
		
		private Alarmable<E> getOwner() {
			return owner;
		}
	}
	
	
	/**
	 * Returns the scheduler that can be used to registered {@link Tickable} objects
	 * @return
	 */
	public static Alarm getScheduler() {
		if (scheduler == null) {
			scheduler = new Alarm();
		}
		
		return scheduler;
	}
	
	private Alarm() {
		map = new HashMap<>();
	}
	
	/**
	 * Internal reminder mechanism that allows the scheduler to know 
	 * @param reminder
	 */
	private <E> void notify(Reminder<E> reminder) {
		map.remove(reminder.getOwner());
		reminder.getOwner().alarm(reminder.getKey());
	}
	
	/**
	 * Schedules the provided tickable object to be reminded in (<i>seconds</i>) seconds via the {@link Tickable#tick(Object)}
	 * method.<br />
	 * Note that the object provided as a 'reference' object is passed back to the tickable object, possibly as a way to
	 * distinguish between alert events.
	 * @param tickable The instance to 'tick' when the time is up
	 * @param reference An object that can be identified and acted upon when the instance if 'ticked'
	 * @param seconds How many seconds to remind the instance after. <b>Please Note:</b> values that
	 * are not divisible by .05 will be rounded to the nearest .05 (a server tick).
	 * @return True if there was already a scheduled event for this tickable instance that was overwritten, false otherwise
	 */
	public <E> boolean schedule(Alarmable<E> alarmable, E reference, double seconds) {
		if (alarmable == null || seconds < .0001) {
			return false;
		}

		boolean exists = map.containsKey(alarmable);
		
		Reminder<E> reminder = new Reminder<E>(alarmable, reference);
		
		map.put(alarmable, reminder);
		
		long ticks = Math.round(seconds * Alarm.ticksPerSecond);
		
		reminder.runTaskLater(QuestManagerPlugin.questManagerPlugin, ticks);
		
		return exists;
		
		
	}
	
	/**
	 * Attempts to unregister the tickable instance.
	 * @param tickable
	 * @return Whether or not this was successful, including whther there was something waiting
	 */
	public boolean unregister(Alarmable<?> tickable) {
		if (map.containsKey(tickable)) {
			map.get(tickable).cancel();
			return map.remove(tickable) != null;
		}
		
		return false;
	}
}
