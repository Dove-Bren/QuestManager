package com.skyisland.questmanager.magic.spell;

public abstract class SelfSpell extends Spell {
	
	protected SelfSpell(int cost, int difficulty, String name, String description) {
		super(cost, difficulty, name, description);
	}
	
}
