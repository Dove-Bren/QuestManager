package com.skyisland.questmanager.magic.spell.effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.magic.MagicUser;

/**
 * Catches entities on fire
 * @author Skyler
 *
 */
public class FireEffect extends ImbuementEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(FireEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(FireEffect.class);
	}
	

	private enum aliases {
		DEFAULT(FireEffect.class.getName()),
		LONGI("SpellFire"),
		OLD("com.SkyIsland.QuestManager.Magic.Spell.Effect." + FireEffect.class.getSimpleName()),
		LONG("FireSpell"),
		SHORT("SFire");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static FireEffect valueOf(Map<String, Object> map) {
		return new FireEffect((int) map.get("duration"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("duration", duration);
		
		return map;
	}
	
	private int duration;
	
	public FireEffect(int fireDuration) {
		this.duration = fireDuration;
	}
	
	@Override
	public void apply(Entity e, MagicUser cause) {
		if (e instanceof LivingEntity) {
			LivingEntity targ = (LivingEntity) e;
			targ.setFireTicks(duration); 
			targ.setMetadata(DamageEffect.damageMetaKey, new FixedMetadataValue
					(QuestManagerPlugin.questManagerPlugin, true));
			targ.damage(0.0, cause.getEntity());
			targ.setMetadata(DamageEffect.damageMetaKey, new FixedMetadataValue
					(QuestManagerPlugin.questManagerPlugin, true));
		}
	}
	
	@Override
	public void apply(Location loc, MagicUser cause) {
		//can't damage a location
		//do nothing 
		;
	}
	
	@Override
	public FireEffect getCopyAtPotency(double potency) {
		FireEffect effect = new FireEffect((int) (duration * potency));
		effect.setDisplayName(getDisplayName());
		return effect;
	}
}
