package com.skyisland.questmanager.magic.spell.status;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.skyisland.questmanager.magic.MagicUser;
import com.skyisland.questmanager.scheduling.IntervalScheduler;

/**
 * A QM defined status effect, tied to magic. 
 * @author Skyler
 *
 */
public interface MagicStatusEffect extends ConfigurationSerializable {
	
	public static final long ticksPerUpdate = 20;
	
	public static final IntervalScheduler statusTicker = IntervalScheduler.createCustomScheduler(ticksPerUpdate);
	
	String getName();
	
	String getDescription();
	
	void apply(MagicUser target);
	
	void cancel();
	
	void cancel(MagicUser target);
	
	/**
	 * Return a deep copy of this effect scaled down by the given potential.
	 * @param potential Scale factor. 1.0 returns the same effect. 0.5 is half as powerful
	 * @return
	 */
	MagicStatusEffect copyAtPotential(double potential);
	
}
