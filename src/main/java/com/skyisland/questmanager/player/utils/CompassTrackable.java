package com.skyisland.questmanager.player.utils;

import org.bukkit.Location;

/**
 * A class that's compass trackable means that the quest player can seek it.<br />
 * This at the moment should only include requirements and NPCs, as they're the only thing
 * set up be automatically tracked.
 * @author Skyler
 *
 */
public interface CompassTrackable {
	
	Location getLocation();
}
