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

package com.skyisland.questmanager.player.skill.defaults;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.LogSkill;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.player.skill.event.CombatEvent;

public class AxeSkill extends LogSkill implements Listener {
	
	public static final String CONFIG_NAME = "Axe.yml";

	public Type getType() {
		return Skill.Type.COMBAT;
	}
	
	public String getName() {
		return "Axe";
	}
	
	public String getDescription(QuestPlayer player) {
		String ret = "Axe Skill governs the player's ability to hit their target while "
				+ "using an axe.";
		
		int lvl = player.getSkillLevel(this);
		if (lvl < apprenticeLevel) {
			ret += "\n\n" + ChatColor.RED + "Chance to hit: " + (int) (-(rateDecrease) * (apprenticeLevel - lvl)) + "%";
		}
		
		ret += "\n" + ChatColor.GREEN + "Bonus Damage: " + (Math.max(0, lvl - apprenticeLevel) / levelRate) + ChatColor.RESET;
		
		return ret;
	}

	@Override
	public int getStartingLevel() {
		return startingLevel;
	}
	
	@Override
	public String getConfigKey() {
		return "Axe";
	}
	
	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.DIAMOND_AXE);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof AxeSkill);
	}
	
	private int startingLevel;
	
	private int levelRate;
	
	private int apprenticeLevel;
	
	private double rateDecrease;
	
	private boolean useEnemyLevel;
	
	public AxeSkill() {
		File configFile = new File(QuestManagerPlugin.questManagerPlugin.getDataFolder(),
				QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillPath() + CONFIG_NAME);
		YamlConfiguration config = createConfig(configFile);
		
		if (!config.getBoolean("enabled", true)) {
			return;
		}
		
		this.startingLevel = config.getInt("startingLevel", 0);
		this.levelRate = config.getInt("levelsperdamageincrease", 10);
		this.apprenticeLevel = config.getInt("apprenticeLevel", 15);
		this.rateDecrease = config.getDouble("hitchancePenalty", 3.0);
		this.useEnemyLevel = config.getBoolean("useEnemyLevel", false);
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	private YamlConfiguration createConfig(File configFile) {
		if (!configFile.exists()) {
			
			YamlWriter writer = new YamlWriter();
			
			writer.addLine("enabled", true, Lists.newArrayList("Whether or not this skill is allowed to be used.", "true | false"))
				.addLine("startingLevel", 0, Lists.newArrayList("The level given to players who don't have this skill yet", "[int]"))
				.addLine("levelsperdamageincrease", 10, Lists.newArrayList("How many levels are needed to gain an additional bonus damage,", "over apprentice level", "[int], greater than 0"))
				.addLine("apprenticeLevel", 15, Lists.newArrayList("The level at which the player's chance to hit is no", "longer is penalized", "[int], greater than 0"))
				.addLine("hitchancePenalty", 3.0, Lists.newArrayList("The penalty per level under apprentiveLevel given to the", "chance to hit. Maximum penalty is (apprenticeLevel * hitchancePenalty)", "[double]"))
				.addLine("useEnemyLevel", false, Lists.newArrayList("Should thiis skill use the damaged creature's level", "as the action level?", "Prevents farming low level stuff", "Level is defined after a monster's name: Wolf (Lvl 10)", "Enemies with an unparsable lvl (like no name) default to skill level", "[true|false]"));
			
			try {
				writer.save(configFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return writer.buildYaml();
		}
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		return config;
	}
	
	@EventHandler
	public void onCombat(CombatEvent e) {
		//Player p = e.getPlayer().getPlayer().getPlayer();
		
		if (!e.getWeapon().getType().name().toLowerCase().contains("axe")) {
			return;
		}
		
		int lvl = e.getPlayer().getSkillLevel(this);
		int actionLevel = lvl;
		if (useEnemyLevel) {
			actionLevel = -1;
			if (e.getTarget().getCustomName() != null)
			if (e.getTarget().getKiller() != null)
			if (e.getTarget().getCustomName().contains("(Lvl ")) {
				//level'ed entity!
				String cache = e.getTarget().getCustomName();
				int pos = cache.indexOf("(Lvl ");
				//advance pos by 5 to get the number
				pos += 5;
				String tail = cache.substring(pos);
				int length = 0;
				for (char c : tail.toCharArray()) {
					if (Character.isDigit(c)) {
						length += 1;
					} else {
						break;
					}
				}
				
				if (length == 0) {
					System.out.println("Error when finding level! Expected a number, got:  " + tail.charAt(0));
					return;
				}
				
				String slvl = tail.substring(0, length);
				actionLevel = Integer.valueOf(slvl);
			}
			
			if (actionLevel == -1)
				actionLevel = lvl;
			
			if (lvl - actionLevel > QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillCutoff()
				|| actionLevel - lvl > QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillUpperCutoff()) {
				return; //too big a difference
			}
		}
		
		//reduce chance to hit if level under apprentice level
		boolean causeMiss = false;
		if (lvl < apprenticeLevel) {
			//3% per level under apprentice -- up to 45%
			int miss = (int) (rateDecrease * (apprenticeLevel - lvl)); 
			int roll = Skill.RANDOM.nextInt(100);
			if (roll <= miss) {
				e.setMiss(true);
				causeMiss = true;
			}
		}
		
		
		//just increase damage based on level
		//every n levels, one more damage
		e.setModifiedDamage(e.getModifiedDamage() + (Math.max(0, lvl - apprenticeLevel) / levelRate));
		
		if (e.getFinalDamage() <= 0)
			return;
		
		this.perform(e.getPlayer(), actionLevel, causeMiss); //only get a 'cause miss' if this skill caused it 
		
	}
}
