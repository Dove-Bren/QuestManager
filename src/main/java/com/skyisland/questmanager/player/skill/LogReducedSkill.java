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

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.PluginConfiguration;
import com.skyisland.questmanager.player.QuestPlayer;

/**
 * Same as a log Skill, except the xp gain is further decreased. The idea is that common skills
 * that apply all over the place should level up less.
 * @see com.skyisland.questmanager.player.skill.Skill
 * @see LogReducedSkill#perform(QuestPlayer, int, boolean)
 */
public abstract class LogReducedSkill extends Skill implements StagedIncreaseSkill {

	/**
	 * Adds experience to the skill, standardized as the specified amount for performing an action of
	 * level <i>actionLevel</i> and either succeeding or failing, as determined by <i>fail</i>
	 * This implementation differs from the standard one in that xp gains generally decrease as the
	 * skill level increases. Performing an action with action-level matching skill level at level one gives
	 * much more xp than doing the same at a high level.
	 * @param actionLevel The level of the action that was performed. Maybe cooking salmon is a lvl 30 task, for example
	 * @param fail whether or not the action was failed. May or not award xp on failure, as determined by config
	 */
	@Override
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
		
		//Apply log dropoff as skill level increases
		xp = (float) (xp / (Math.max(1, Math.log10(Math.max(1, participant.getSkillLevel(this))))));
		
		xp /= 2f;
		
		//lulz now just add it to player xp
		participant.setSkillExperience(this, participant.getSkillExperience(this) + xp);
	}
	
	@Override
	public void performMinor(QuestPlayer participant, int actionLevel, boolean fail) {
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
		
		//Apply log dropoff as skill level increases
		xp = (float) (xp / (Math.max(1, Math.log10(Math.max(1, participant.getSkillLevel(this))))));
		
		xp /= 2f;
		
		//further reduce xp
		xp /= 4f;
		participant.setSkillExperience(this, participant.getSkillExperience(this) + xp);
	}
	


	@Override
	public void performMajor(QuestPlayer participant, int actionLevel, boolean fail) {
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
		float xp = (float) (4f * base * (1-(Math.abs((double) levelDifference) / (double) config.getSkillCutoff())));
		
		//Apply log dropoff as skill level increases
		xp = (float) (xp / (Math.max(1, Math.log10(Math.max(1, participant.getSkillLevel(this))))));
		
		xp /= 2;
		
		participant.setSkillExperience(this, participant.getSkillExperience(this) + xp);
	}
}
