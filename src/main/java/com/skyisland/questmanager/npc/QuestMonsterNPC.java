package com.skyisland.questmanager.npc;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * An NPC that is a monster for a quest.
 * @author Skyler
 *
 */
public class QuestMonsterNPC extends NPC {
	
	@Override
	public Map<String, Object> serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void tick() {
		;
	}

	@Override
	protected void interact(Player player) {
		;
	}
	
	@Override
	@EventHandler
	public void onEntityHurt(EntityDamageEvent e) {
		
	}
}
