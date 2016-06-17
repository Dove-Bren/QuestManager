package com.skyisland.questmanager.enemy.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;

import com.skyisland.questmanager.region.Region;

/**
 * Fired when a mob that as spawned as an Enemy is killed. 
 *
 */
public class EnemyDeathEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
		
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private EntityDeathEvent event;
	
	private Region region;
	
	public EnemyDeathEvent(Region owningRegion, EntityDeathEvent deathEvent) {
		this.region = owningRegion;
		this.event = deathEvent;
	}

	public EntityDeathEvent getEvent() {
		return event;
	}

	public Region getRegion() {
		return region;
	}

}
