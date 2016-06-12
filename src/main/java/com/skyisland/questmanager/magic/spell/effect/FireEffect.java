/*
 *  QuestManager: An RPG plugin for the Bukkit API.
 *  Copyright (C) 2015-2016 Github Contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
