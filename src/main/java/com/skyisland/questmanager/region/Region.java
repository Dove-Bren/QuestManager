package com.skyisland.questmanager.region;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

/**
 * Specifies some region of land.
 * Regions are defined to be the range of blocks that mobs can spawn ON TOP OF;
 * When finding safe spawning locations, a region will try to look upwards to see if it's
 * safe and travel upwards to find a suitable location.
 * @author Skyler
 * TODO: Overlapping regions are a problem, and enemies spawn ANYWHERE in the region
 * instead of near the player
 */
public interface Region extends ConfigurationSerializable {
	
	/**
	 * Checks whether the provided entity is in the region
	 */
	 boolean isIn(Entity e);
	
	/**
	 * Checks whether a specified location falls within this region
	 */
	boolean isIn(Location loc);
	
	/**
	 * Returns a random location from within this region.
	 * @param safe Whether or not the location should be safe to spawn a normal mob (2x1).
	 * Since regions define blocks mobs can spawn on top of, searching for safe locations will
	 * involve looking up for a suitable location. Regions should be defined to minimize the number
	 * of blocks above potential spawning locations to avoid overhead in spawning.
	 */
	Location randomLocation(boolean safe);
}
