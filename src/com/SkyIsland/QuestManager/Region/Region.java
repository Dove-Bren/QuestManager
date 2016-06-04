package com.SkyIsland.QuestManager.Region;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

/**
 * Specifies some region of land.<br />
 * Regions are defined to be the range of blocks that mobs can spawn ON TOP OF;
 * When finding safe spawning locations, a region will try to look upwards to see if it's
 * safe and travel upwards to find a suitable location.
 * @author Skyler
 * @todo Overlapping regions are a problem, and enemies spawn ANYWHERE in the region
 * instead of near the player
 */
public abstract class Region implements ConfigurationSerializable {
	
	/**
	 * Checks whether the provided entity is in the region
	 * @param e
	 * @return
	 */
	public abstract boolean isIn(Entity e);
	
	/**
	 * Checks whether a specified location falls within this region
	 * @param loc
	 * @return
	 */
	public abstract boolean isIn(Location loc);
	
	/**
	 * Returns a random location from within this region.
	 * @param safe Whether or not the location should be safe to spawn a normal mob (2x1).<br />
	 * Since regions define blocks mobs can spawn on top of, searching for safe locations will
	 * involve looking up for a suitable location. Regions should be defined to minimize the number
	 * of blocks above potential spawning locations to avoid overhead in spawning.
	 * @return
	 */
	public abstract Location randomLocation(boolean safe);
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object o);
}
