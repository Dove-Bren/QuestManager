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

package com.skyisland.questmanager.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Stores player options for easier access and modification
 * @see QuestPlayer
 */
public class PlayerOptions implements ConfigurationSerializable {
	
	public enum Key {
		CHAT_COMBAT_DAMAGE("chat.combat.damage", new ItemStack(Material.IRON_SWORD), 
				Lists.newArrayList("Displays damage information in chat,", "like how much damage you did")),
		CHAT_COMBAT_RESULT("chat.combat.result", new ItemStack(Material.BOOK), 
				Lists.newArrayList("Shows spell failures, melee", "misses, etc in chat")),
		CHAT_PET_DISMISSAL("chat.summon.dismissal", new ItemStack(Material.APPLE), 
				Lists.newArrayList("Lets you know when your", "summon has died or expired")),
		SKILL_REVEAL("skill.showAll", new ItemStack(Material.EMPTY_MAP),
				Lists.newArrayList("Displays all skills in the", "skill table regardless of if", 
						"you've experienced the skill yet", "(spoilers)"), false),
		SKILL_LIST("skill.inLog", new ItemStack(Material.WRITTEN_BOOK),
				Lists.newArrayList("Display skill levels in", "the questlog. If off, you", "can still use the command", "/player skills", "to see your skill levels"), true),
		SKILL_RECIPES_ALL("skill.recipes.all", new ItemStack(Material.SHEARS),
				Lists.newArrayList("Shows all recipes for", "a skill even if it is", "way more difficult than", "what you could achieve")),
		SHOW_PLAYER_MENU("player.menu", new ItemStack(Material.SKULL_ITEM), 
				Lists.newArrayList("Right click players to see", "a player menu"));
		
		private String key;
		
		private Boolean def;
		
		private ItemStack icon;
		
		private List<String> hint;
		
		Key(String key, ItemStack icon, List<String> hint, Boolean def) {
			this.key = key;
			this.def = def;
			this.icon = icon;
			this.hint = hint;
		}
		
		Key(String key, ItemStack icon, List<String> hint) {
			this(key, icon, hint, true);
		}
		
		public String getKey() {
			return key;
		}
		
		@Override
		public String toString() {
			return key;
		}
		
		public Boolean getDefault() {
			return def;
		}
		
		public ItemStack getIcon() {
			return icon;
		}
		
		public List<String> getHint() {
			return hint;
		}
		
	}
	
	private Map<Key, Boolean> opts;
	
	private boolean dirty;
	
	protected PlayerOptions() {
		opts = new HashMap<>();
		
		for (Key key : Key.values()) {
			opts.put(key, key.getDefault());
		}
		
		this.dirty = true;
	}
	
	/**
	 * Returns the currently registered option for the provided key.
	 * @return True or false, depending on the users settings
	 */
	public boolean getOption(Key key) {
		return opts.get(key);
	}
	
	/**
	 * Sets the specified option to be set to <i>value</i>.
	 * @param key The option to set
	 * @param value The value to set
	 * @return The old value in the provided option
	 */
	public boolean setOption(Key key, boolean value) {
		return opts.put(key,  value);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new TreeMap<>();
		
		for (Key key : Key.values()) {
			map.put(key.getKey(), opts.get(key));
		}
		
		return map;		
	}
	
	public static PlayerOptions valueOf(Map<String, Object> map) {
		PlayerOptions po = new PlayerOptions();
		po.dirty = false;
		
		for (Key key : Key.values()) {
			if (map.containsKey(key.getKey())) {
				Object o = map.get(key.getKey());
				
				if (!(o instanceof Boolean)) {
					QuestManagerPlugin.logger.info("Wrong data type for player"
							+ " options: " + o);
					continue;
				}
				
				po.setOption(key, (boolean) map.get(key.getKey()));
			} else
				po.dirty = true;
		}
		
		return po;
	}

	/**
	 * Returns whether this config has options it couldn't find when deserializing
	 * it's configuration section. Useful for telling players when there are new options
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @see #isDirty()
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
