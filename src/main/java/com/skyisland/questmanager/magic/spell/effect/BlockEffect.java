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
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;

import com.skyisland.questmanager.magic.MagicUser;

/**
 * Manipulates blocks. This effect replaces one type of block with another
 *
 */
public class BlockEffect extends SpellEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(BlockEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(BlockEffect.class);
	}
	

	private enum aliases {
		DEFAULT(BlockEffect.class.getName()),
		OLD("com.SkyIsland.QuestManager.Magic.Spell.Effect." + BlockEffect.class.getSimpleName()),
		LONGI("SpellBlock"),
		LONG("BlockSpell"),
		SHORT("SBlock");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static BlockEffect valueOf(Map<String, Object> map) {
		return new BlockEffect(
				Material.valueOf((String) map.get("typefrom")),
				Material.valueOf((String) map.get("typeto"))
				);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("typefrom", typeFrom.name());
		map.put("typeto", typeTo.name());
		
		return map;
	}
	
	private Material typeFrom, typeTo;
	
	public BlockEffect(Material typeFrom, Material typeTo) {
		this.typeFrom = typeFrom;
		this.typeTo = typeTo;
	}
	
	@Override
	public void apply(Entity e, MagicUser cause) {
		; //do nothing
	}
	
	@Override
	public void apply(Location loc, MagicUser cause) {
		if (loc.getBlock().getType() == typeFrom) {
			loc.getBlock().setType(typeTo);
		}
	}
}
