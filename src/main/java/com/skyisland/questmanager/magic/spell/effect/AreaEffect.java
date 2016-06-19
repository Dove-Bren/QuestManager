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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;

import com.skyisland.questmanager.effects.AuraEffect;
import com.skyisland.questmanager.magic.MagicUser;

public class AreaEffect extends SpellEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(AreaEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(AreaEffect.class);
	}
	

	private enum aliases {
		DEFAULT(AreaEffect.class.getName()),
		OLD("com.SkyIsland.QuestManager.Magic.Spell.Effect." + AuraEffect.class.getSimpleName()),
		LONGI("SpellAreaOfEffect"),
		LONG("AreaOfEffectSpell"),
		SHORT("SArea");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static AreaEffect valueOf(Map<String, Object> map) {
		AreaEffect ret = new AreaEffect((double) map.get("radius"));
		//load effects
		@SuppressWarnings("unchecked")
		List<SpellEffect> effects = (List<SpellEffect>) map.get("effects");

		effects.forEach(ret::addEffect);
		
		return ret;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("radius", radius);
		map.put("effects", effects);
		
		return map;
	}
	
	private List<SpellEffect> effects;
	
	private double radius;
	
	/**
	 * Makes an empty area of effect shell. It contains no spell effects.
	 * @see #addEffect(SpellEffect)
	 */
	public AreaEffect(double radius) {
		this.radius = radius;
		this.effects = new LinkedList<>();
	}
	
	public void addEffect(SpellEffect effect) {
		effects.add(effect);
	}
	
	@Override
	public void apply(Entity e, MagicUser cause) {
		apply(e.getLocation(), cause);
	}
	
	@Override
	public void apply(Location loc, MagicUser cause) {
		
		Collection<Entity> nearby = loc.getWorld().getNearbyEntities(
				loc, radius, radius, radius);
		
		for (Entity near : nearby)
		for (SpellEffect ef : effects) {
			ef.apply(near, cause);
		}

		loc = loc.getBlock().getLocation();
		loc.add(-radius, -radius, -radius);
		for (int i = 0; i < radius * 2; i++)
		for (int j = 0; j < radius * 2; j++)
		for (int k = 0; k < radius * 2; k++) {
			for (SpellEffect ef : effects) {
				ef.apply(loc.getWorld().getBlockAt(
						loc.getBlockX() + i, loc.getBlockY() + j, 
						loc.getBlockZ() + k).getLocation(),
						cause);
			}
		}
	}
}
