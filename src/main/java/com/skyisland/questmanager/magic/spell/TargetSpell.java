package com.skyisland.questmanager.magic.spell;

import com.skyisland.questmanager.magic.MagicUser;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public abstract class TargetSpell extends Spell {
	
	protected TargetSpell(int cost, int difficulty, String name, String description) {
		super(cost, difficulty, name, description);
	}
	
	protected abstract void onBlockHit(MagicUser caster, Location loc);
	
	protected abstract void onEntityHit(MagicUser caster, LivingEntity target);
}
