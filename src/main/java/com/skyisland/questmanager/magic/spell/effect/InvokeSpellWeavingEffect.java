package com.skyisland.questmanager.magic.spell.effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;

import com.skyisland.questmanager.magic.MagicUser;

/**
 * This effect is intended to be used as a bridge between the spell holder interface and the spell weaving
 * system. Since Spell Weaving spells have their own difficulty and mana cost, it's encouraged that any
 * spells made out of this effect are either very little to low cost and difficulty.<br />
 * This effect does not take into consideration location or entity it's being applies on. It simple
 * uses the caster in both cases.
 */
public class InvokeSpellWeavingEffect extends SpellEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(InvokeSpellWeavingEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(InvokeSpellWeavingEffect.class);
	}
	

	private enum aliases {
		DEFAULT(InvokeSpellWeavingEffect.class.getName()),
		OLD("com.SkyIsland.QuestManager.Magic.Spell.Effect." + InvokeSpellWeavingEffect.class.getSimpleName()),
		LONGI("SpellWeavingInvoke"),
		LONG("InvokeSpellWeavingSpell"),
		SHORT("SInvoke");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static InvokeSpellWeavingEffect valueOf(Map<String, Object> map) {
		return new InvokeSpellWeavingEffect();
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		return map;
	}
	
	public InvokeSpellWeavingEffect() {
		;
	}
	
	@Override
	public void apply(Entity e, MagicUser cause) {
		cause.castSpellWeavingSpell();
	}
	
	@Override
	public void apply(Location loc, MagicUser cause) {
		cause.castSpellWeavingSpell();
	}
}
