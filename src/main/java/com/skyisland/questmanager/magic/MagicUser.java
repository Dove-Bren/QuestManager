package com.skyisland.questmanager.magic;

import java.util.List;

import org.bukkit.entity.Entity;

public interface MagicUser {
	
	Entity getEntity();
	
	double getMP();
	
	void addMP(double amount);
	
	void addSpellPylon(SpellPylon pylon);
	
	List<SpellPylon> getSpellPylons();
	
	void clearSpellPylons();
	
	/**
	 * Cast the currently set up spell weaving spell. Spell cast, cost, difficulty, etc
	 * are determined by the spell the player has created with their current pylons.<br />
	 * Pylons should be destroyed at this time, and checks should be made to player
	 * mana, skill level, etc
	 */
	void castSpellWeavingSpell();
}
