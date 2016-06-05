package com.skyisland.questmanager.effects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * An effect used by the QuestManager to signal something to the player.
 * @author Skyler
 *
 */
public abstract class QuestEffect {

	public abstract void play(Entity player, Location location);
}
