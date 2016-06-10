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
import com.skyisland.questmanager.player.skill.LogReducedSkill;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.player.skill.event.FishEvent;
import com.skyisland.questmanager.player.skill.event.MineEvent;
import com.skyisland.questmanager.player.skill.event.WoodChopEvent;

public class PatienceSkill extends LogReducedSkill implements Listener {
	
	public static final String configName = "Patience.yml";

	public Type getType() {
		return Skill.Type.OTHER;
	}
	
	public String getName() {
		return "Patience";
	}
	
	public String getDescription(QuestPlayer player) {
		String ret = ChatColor.WHITE + "Those with patience have an easier time with gathering skills.";
		return ret;
	}

	@Override
	public int getStartingLevel() {
		return startingLevel;
	}
	
	@Override
	public String getConfigKey() {
		return "Patience";
	}
	
	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.EGG);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof PatienceSkill);
	}
	
	private int startingLevel;
	
	private double levelRate;
	
	public PatienceSkill() {
		File configFile = new File(QuestManagerPlugin.questManagerPlugin.getDataFolder(), 
				QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillPath() + configName);
		YamlConfiguration config = createConfig(configFile);
		
		if (!config.getBoolean("enabled", true)) {
			return;
		}
		
		this.startingLevel = config.getInt("startingLevel", 0);
		this.levelRate = config.getDouble("generalDifficultyDiscount", 0.002);
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	private YamlConfiguration createConfig(File configFile) {
		if (!configFile.exists()) {
			
			YamlWriter writer = new YamlWriter();
			
			writer.addLine("enabled", true, Lists.newArrayList("Whether or not this skill is allowed to be used.", "true | false"))
				.addLine("startingLevel", 0, Lists.newArrayList("The level given to players who don't have this skill yet", "[int]"))
				.addLine("generalDifficultyDiscount", 0.002, Lists.newArrayList("Discount given to skill difficulty per skill level", "[double], 0.01 is 1%"));
			
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
	public void onFish(FishEvent e) {		
		QuestPlayer player = e.getPlayer();
		
		int lvl = player.getSkillLevel(this);
				
		e.setObstacleDifficultyModifier(e.getObstacleDifficultyModifier() - (levelRate * (double) lvl));
		this.perform(player, e.getDifficulty());
		
		this.perform(player);  
		
	}

	@EventHandler
	public void onChop(WoodChopEvent e) {		
		QuestPlayer player = e.getPlayer();
		
		int lvl = player.getSkillLevel(this);
				
		e.setTimingModifier(e.getTimingModifier() + (levelRate * (double) lvl));
		this.perform(player, e.getDifficulty());
		
		this.perform(player);  
		
	}

	@EventHandler
	public void onMine(MineEvent e) {		
		QuestPlayer player = e.getPlayer();
		
		int lvl = player.getSkillLevel(this);
				
		e.setHardnessModifier(e.getHardnessModifier() - (levelRate * (double) lvl));
		e.setHitsModifier(e.getHitsModifier() + (levelRate * (double) lvl));
		this.perform(player, e.getDifficulty());
		
		this.perform(player);  
		
	}
}
