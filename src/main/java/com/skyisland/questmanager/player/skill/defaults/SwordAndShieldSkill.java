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

import com.skyisland.questmanager.configuration.utils.YamlWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.player.skill.event.CombatEvent;
import com.skyisland.questmanager.ui.menu.action.ForgeAction;
import com.google.common.collect.Lists;

/**
 * Skill governing combat with a weapon in main hand, shield in offhand
 *
 */
public class SwordAndShieldSkill extends Skill implements Listener {
	
	public static final String CONFIG_NAME = "SwordAndShield.yml";

	public Type getType() {
		return Skill.Type.COMBAT;
	}
	
	public String getName() {
		return "Sword&Shield";
	}
	
	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.SHIELD);
	}
	
	public String getDescription(QuestPlayer player) {
		String ret = ChatColor.WHITE + "The Sword and Shield skill determines a player's offensive and "
				+ "defensive abilities while they have a weapon in their mainhand and a shield in their offhand";
		
		int lvl = player.getSkillLevel(this);
		
		ret += "\n\n" + ChatColor.GREEN + "Bonus Defense: " + (lvl / levelRate) + ChatColor.RESET;
		
		return ret;
	}

	@Override
	public int getStartingLevel() {
		return startingLevel;
	}
	
	@Override
	public String getConfigKey() {
		return "Sword_And_Shield";
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof SwordAndShieldSkill);
	}
	
	private int startingLevel;
	
	private int levelRate;
	
	public SwordAndShieldSkill() {
		File configFile = new File(QuestManagerPlugin.questManagerPlugin.getDataFolder(),
				QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillPath() + CONFIG_NAME);
		YamlConfiguration config = createConfig(configFile);
		
		if (!config.getBoolean("enabled", true)) {
			return;
		}
		
		this.startingLevel = config.getInt("startingLevel", 0);
		this.levelRate = config.getInt("levelsperdefenseincrease", 10);
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	private YamlConfiguration createConfig(File configFile) {
		if (!configFile.exists()) {
			YamlWriter writer = new YamlWriter();
			
			writer.addLine("enabled", true, Lists.newArrayList("Whether or not this skill is allowed to be used.", "true | false"))
				.addLine("startingLevel", 0, Lists.newArrayList("The level given to players who don't have this skill yet", "[int]"))
				.addLine("levelsperdefenseincrease", 10, Lists.newArrayList("How many levels are required to gain an additional", "point in defense", "[int], greater than 0"));
			
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
		if (!ForgeAction.Repairable.isRepairable(e.getWeapon().getType())
				|| (e.getOtherItem() == null || e.getOtherItem().getType() != Material.SHIELD)) {
			return;
		}
		
		int lvl = e.getPlayer().getSkillLevel(this);
		
		//just increase defense based on level
		//every n levels, one more defense point
		e.setModifiedDamage(e.getModifiedDamage() - (lvl / levelRate));
		
		this.perform(e.getPlayer(), e.isMiss());
		
	}
}
