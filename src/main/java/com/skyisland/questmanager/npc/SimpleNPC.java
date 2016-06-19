package com.skyisland.questmanager.npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.skyisland.questmanager.scheduling.DispersedScheduler;

/**
 * Describes NPCs with simple movement pattern: they occasionally attempt to
 * move back to their original spot
 * @author Skyler
 *
 */
public abstract class SimpleNPC extends NPC {
	
	private Location startingLoc;
	
	/**
	 * Defines how far an NPC can be when they are ticked before being teleported
	 * back to their original location
	 */
	private static final double range = 20.0;
	
	protected SimpleNPC(Location startingLoc) {
		super();
		this.startingLoc = startingLoc;
		
		DispersedScheduler.getScheduler().register(this);
	}
	
	/**
	 * Motivate entity to move back to the original location, if we hve one set
	 */
	@Override
	public void tick() {
		Entity e = getEntity();
		
		if (e == null || startingLoc == null) {
			return;
		}
		

		if (!e.getLocation().getWorld().isChunkLoaded(e.getLocation().getBlockX() / 16, e.getLocation().getBlockZ() / 16) 
			|| !startingLoc.getWorld().isChunkLoaded(startingLoc.getBlockX() / 16, startingLoc.getBlockZ() / 16)) {
			return;
		}
		
		if (!e.getLocation().getWorld().getName().equals(
				startingLoc.getWorld().getName()) 
				|| e.getLocation().distance(startingLoc) > range) {
			//if we're in a different world (whut?) or range is too big,
			//teleport them back!
			e.getLocation().getChunk().load();
			startingLoc.getChunk().load();
			e.teleport(startingLoc);
		}
	}

	/**
	 * @return the startingLoc
	 */
	public Location getStartingLoc() {
		return startingLoc;
	}

	/**
	 * @param startingLoc the startingLoc to set
	 */
	public void setStartingLoc(Location startingLoc) {
		this.startingLoc = startingLoc;
	}
}
