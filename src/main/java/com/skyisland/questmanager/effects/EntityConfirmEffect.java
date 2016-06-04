package com.skyisland.questmanager.effects;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Effect signaling to the player that the entity they just interacted with was correct. <br />
 * This effect was made with the 'slay' requirement in mind, where it'll display effects when you kill
 * the right kind of enemy.
 * @author Skyler
 *
 */
public class EntityConfirmEffect extends QuestEffect {
	
	private static final Effect effect = Effect.STEP_SOUND;
	
	@SuppressWarnings("deprecation")
	private static final int blockType = Material.EMERALD_BLOCK.getId();
	
	/**
	 * The number of particals
	 */
	private int magnitude;
	
	/**
	 * 
	 * @param magnitude The number of particals, roughly
	 */
	public EntityConfirmEffect(int magnitude) {
		this.magnitude = magnitude;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void play(Entity player, Location effectLocation) {
		
		if (!(player instanceof Player)) {
			return;
		}
		
		for (int i = 0; i < magnitude; i++)
		((Player) player ) .playEffect(effectLocation, effect, blockType);
	}
}
