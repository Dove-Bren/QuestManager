package com.SkyIsland.QuestManager.NPC;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * An npc that doesn't move rather then being reset
 * @author Skyler
 *
 */
public abstract class SimpleStaticBioptionNPC extends SimpleBioptionNPC {

	protected SimpleStaticBioptionNPC(Location startingLoc) {
		super(startingLoc);
	}
	
	@Override
	/**
	 * Render this NPC imobile using slowness instead of teleporting them
	 */
	public void tick() {
		Entity e = getEntity();
		
		if (e == null) {
			return;
		}
		

		if (!e.getLocation().getChunk().isLoaded()) {
			return;
		}
		
		if (e instanceof LivingEntity) {
			((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999999, 10, false, false), true);
		}
	}
	
	@Override
	/**
	 * Sets the entity, making it immobile in the process
	 */
	public void setEntity(Entity entity) {
		super.setEntity(entity);
		if (entity instanceof LivingEntity) {
			((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999999, 10, false, false), true);
		}
	}

}
