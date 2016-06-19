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

package com.skyisland.questmanager.magic.spell;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

public class SpellManager {
	
	private Map<String, Spell> spells;
	
	public SpellManager() {
		this.spells = new HashMap<>();
	}
	
	public SpellManager(File spellDirectory) {
		this();
		load(spellDirectory);
	}
	
	private void load(File directory) {
		if (directory == null || !directory.exists()) {
			return;
		}
		
		if (!directory.isDirectory()) {
			loadFile(directory);
		}
		
		//else loop through files
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				load(file);
			}
			
			String ln = file.getName().toLowerCase();
			
			if (ln.endsWith(".yml") || ln.endsWith(".yaml")) {
				loadFile(file);
			}
		}
	}
	
	private void loadFile(File spellFile) {
		//get config, grab all spells
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(spellFile);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to load spell file: " + spellFile.getAbsolutePath());
			return;
		}
		
		for (String key : config.getKeys(false)) {
			Spell spell = (Spell) config.get(key);
			addSpell(spell);
		}
	}
	
	/**
	 * Adds the spell to the manager, overwriting any with a conflicting name;
	 * @return true if there was a spell by that name before, false otherwise
	 */
	public boolean addSpell(Spell spell) {
		return spells.put(spell.getName(), spell) != null;
	}
	
	public Spell getSpell(String name) {
		return spells.get(name);
	}
	
	/**
	 * Returns a list of all registered spell names
	 */
	public Set<String> getSpells() {
		return spells.keySet();
	}
}
