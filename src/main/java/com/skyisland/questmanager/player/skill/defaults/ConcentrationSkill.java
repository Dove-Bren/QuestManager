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
import com.skyisland.questmanager.magic.MagicRegenEvent;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.LogReducedSkill;
import com.skyisland.questmanager.player.skill.Skill;

public class ConcentrationSkill extends LogReducedSkill implements Listener {
	
	public static final String CONFIG_NAME = "Concentration.yml";

	public Type getType() {
		return Skill.Type.COMBAT;
	}
	
	public String getName() {
		return "Concentration";
	}
	
	public String getDescription(QuestPlayer player) {
		String ret = ChatColor.WHITE + "Concentration governs mana recharge rate";
		
		int lvl = player.getSkillLevel(this);
				
		ret += "\n\n" + ChatColor.GREEN + "Recharge Rate: " + (100 * (1 + lvl * levelRate)) + "%" + ChatColor.RESET;
		
		return ret;
	}

	@Override
	public int getStartingLevel() {
		return startingLevel;
	}
	
	@Override
	public String getConfigKey() {
		return "Concentration";
	}
	
	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.BOOK);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof ConcentrationSkill);
	}
	
	private int startingLevel;
	
	private double levelRate;
	
	public ConcentrationSkill() {
		File configFile = new File(QuestManagerPlugin.questManagerPlugin.getDataFolder(),
				QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillPath() + CONFIG_NAME);
		YamlConfiguration config = createConfig(configFile);
		
		if (!config.getBoolean("enabled", true)) {
			return;
		}
		
		this.startingLevel = config.getInt("startingLevel", 0);
		this.levelRate = config.getDouble("manaBonusRate", 0.015);
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	private YamlConfiguration createConfig(File configFile) {
		if (!configFile.exists()) {
			
			YamlWriter writer = new YamlWriter();
			
			writer.addLine("enabled", true, Lists.newArrayList("Whether or not this skill is allowed to be used.", "true | false"))
				.addLine("startingLevel", 0, Lists.newArrayList("The level given to players who don't have this skill yet", "[int]"))
				.addLine("manaBonusRate", 0.015, Lists.newArrayList("Mana recharge bonus given per level. Multiplicitive", "[double], 0.01 is 1%"));
			
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
	public void onRegen(MagicRegenEvent e) {		
			
		if (!(e.getEntity() instanceof QuestPlayer)) {
			return;
		}
		
		QuestPlayer player = (QuestPlayer) e.getEntity();
		
		int lvl = player.getSkillLevel(this);
				
		e.setModifier(e.getModifier() + (lvl * levelRate));
		
		this.perform(player);  
		
	}
}
