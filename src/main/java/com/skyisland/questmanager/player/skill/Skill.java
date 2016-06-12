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

package com.skyisland.questmanager.player.skill;

import java.util.Collection;
import java.util.Random;

import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.PluginConfiguration;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.defaults.TwoHandedSkill;

/**
 * A player skill. Skills can pertain to any aspect of the game, from combat to crafting.
 * Implementations are responsible for catching events and acting on them. Implementations are <b>heavily
 * encouraged</b> to use the pre-built skill events rather than the bukkit ones, as they include the ability
 * to transfer information about success/failure between the potential many different skills that may
 * influence an action.
 * While an implementation may override the level-up and experience-gain mechanics of a skill, prebuilt ones
 * are included in this class to allow a uniform config-specified skill experience. For more information, see
 * the {@link #perform(QuestPlayer, int, boolean)} method.
 * @author Skyler
 *
 */
public abstract class Skill implements Comparable<Skill> {
	
	public enum Type {
		COMBAT,
		TRADE,
		OTHER
	}
	
	//Courtesy random
	public static final Random random = new Random();
	
	/**
	 * Adds experience to the skill, standardized as the specified amount for performing an action of
	 * level <i>actionLevel</i> and either succeeding or failing, as determiend by <i>fail</i>
	 * @param actionLevel The level of the action that was performed. Maybe cooking salmon is a lvl 30 task, for example
	 * @param fail whether or not the action was failed. May or not award xp on failure, as determined by config
	 */
	public void perform(QuestPlayer participant, int actionLevel, boolean fail) {
		//XP = (base * (1-remaining))
		
		PluginConfiguration config = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration();
		
		int levelDifference = participant.getSkillLevel(this) - actionLevel;
		//negative means the skill's a higher level than the player
		
		if (levelDifference > config.getSkillCutoff()) {
			return; //no xp, too far below the player's level!
		}
		
		if (fail && levelDifference < -config.getSkillUpperCutoff()) {
			//skill level is higher than the config's upper skill fail limit cutoff
			return;
		}
		
		double base;
		if (fail) {
			base = config.getSkillGrowthOnFail();
		} else {
			base = config.getSkillGrowthOnSuccess();
		}
		
		//every level difference between skill and player decreases xp. Each level differene is 
		//1/[cutoff] reduction. That way, we get a nice approach towards 0 at the cutoff
		float xp = (float) (base * (1-(Math.abs((double) levelDifference) / (double) config.getSkillCutoff())));
		
		//lulz now just add it to player xp
		participant.setSkillExperience(this, participant.getSkillExperience(this) + xp);
		
		
	}
	
	/**
	 * Awards experience equivalent of performing an action of the same level as the skill and either
	 * succeeding or failing.
	 */
	public void perform(QuestPlayer participant, int actionLevel) {
		perform(participant, actionLevel, false);
	}
	
	/**
	 * Awards experience equivalent of performing an action of the same level as the skill and either
	 * succeeding or failing.
	 */
	public void perform(QuestPlayer participant, boolean fail) {
		perform(participant, participant.getSkillLevel(this), fail);
	}
	
	/**
	 * Awards experience equivalent of performing an action of the same level as the skill and succeeding.
	 */
	public void perform(QuestPlayer participant) {
		perform(participant, participant.getSkillLevel(this), false);
	}
	
	public abstract Type getType();
	
	public abstract String getName();
	
	
	/**
	 * Returns an icon to be used as a display icon for the skill in the player skill menu.
	 * All names and lore are wiped away when rendering for the inventory. Enchantment, durability, etc are not
	 */
	public abstract ItemStack getIcon();
	
	/**
	 * Get a pre-formatted description that can be sent to the user.
	 * Message should contain a brief description followed by any specifics defined by the
	 * Skill. For example, the {@link TwoHandedSkill TwoHandedSkill}
	 * should have details about what the current hit chance and bonus damage is. 
	 */
	public abstract String getDescription(QuestPlayer player);
	
	public abstract int getStartingLevel();
	
	@Override
	public abstract boolean equals(Object o);
	
	/**
	 * Returns a key that can be used to figure out what skill information is being looked at in a player config file.
	 * The key can be simple, but should try to be unique. For example, two-handed skill could have "two_handed"
	 */
	public abstract String getConfigKey();
	
	/**
	 * Sets the provided attribute up with a modifier by the given name (overwriting one if it exists)
	 * for the given amount.
	 * <b>This is done</b> under the {@link AttributeModifier.Operation#ADD_SCALAR} operation!
	 */
	public static void setAttributeModifier(AttributeInstance attribute, String name, double amt) {
		Collection<AttributeModifier> mods = attribute.getModifiers();
		
		for (AttributeModifier mod : mods) {
			if (mod.getName().equals(name)) {
				attribute.removeModifier(mod);
				break;
			}
		}
		
		attribute.addModifier(new AttributeModifier(name, amt, AttributeModifier.Operation.ADD_SCALAR));
	}
	
	@Override
	public int compareTo(Skill o) {
		return getName().compareTo(o.getName());
	}
}
