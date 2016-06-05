package com.skyisland.questmanager.magic.spell.effect;

import com.skyisland.questmanager.magic.MagicUser;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

/**
 * An effect a spell might have.
 * @author Skyler
 *
 */
public abstract class SpellEffect implements ConfigurationSerializable {
	
	public abstract void apply(Entity e, MagicUser cause);
	
	public abstract void apply(Location loc, MagicUser cause);
}
