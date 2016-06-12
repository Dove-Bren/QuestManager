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

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.magic.MagicUser;
import com.skyisland.questmanager.magic.spell.effect.SpellEffect;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;

public abstract class Spell implements ConfigurationSerializable {
	
	public static final String failMessage = ChatColor.RED + "You failed to cast the spell!" + ChatColor.RESET;
	
	private int cost;
	
	private String name;
	
	private String description;
	
	/**
	 * How hard is this spell to cast?
	 */
	private int difficulty;
	
	private List<SpellEffect> spellEffects;
	
	protected Spell(int cost, int difficulty, String name, String description) {
		this.cost = cost;
		this.difficulty = difficulty;
		this.name = name;
		this.description = description;
		this.spellEffects = new LinkedList<>();
	}

	public int getCost() {
		return cost;
	}
	
	public int getDifficulty() {
		return difficulty;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public void addSpellEffect(SpellEffect effect) {
		this.spellEffects.add(effect);
	}
	
	public List<SpellEffect> getSpellEffects() {
		return spellEffects;
	}
	
	protected void fail(MagicUser caster) {
		if (caster instanceof QuestPlayer) {
			QuestPlayer player = (QuestPlayer) caster;
			if (player.getPlayer().isOnline()) {
				Player p = player.getPlayer().getPlayer();
				if (player.getOptions().getOption(PlayerOptions.Key.CHAT_COMBAT_RESULT))
					p.sendMessage(Spell.failMessage);
				p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.SMOKE);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 2.0f);
				p.getWorld().playSound(p.getLocation(), Sound.BLOCK_WATERLILY_PLACE, 1.0f, 0.5f);
			}
		}
	}
	
	public abstract void cast(MagicUser caster);
}
