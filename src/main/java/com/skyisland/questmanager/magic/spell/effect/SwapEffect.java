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

import com.skyisland.questmanager.magic.MagicUser;

/**
 * Swaps location of the caster and an entity target. Does nothing on contact with a solid. 
 * @author Skyler
 *
 */
public class SwapEffect extends SpellEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(SwapEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(SwapEffect.class);
	}
	

	private enum aliases {
		DEFAULT(SwapEffect.class.getName()),
		OLD("com.SkyIsland.QuestManager.Magic.Spell.Effect." + SwapEffect.class.getSimpleName()),
		LONGI("SpellTeleport"),
		LONG("TeleportSpell"),
		SHORT("STeleport");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static SwapEffect valueOf(Map<String, Object> map) {
		return new SwapEffect();
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		return map;
	}
	
	
	public SwapEffect() {
	}
	
	@Override
	public void apply(Entity e, MagicUser cause) {
		Location tmp = e.getLocation().clone();
		e.teleport(cause.getEntity());
		cause.getEntity().teleport(tmp);
	}
	
	@Override
	public void apply(Location loc, MagicUser cause) {
		; //do nothing
	}
}
