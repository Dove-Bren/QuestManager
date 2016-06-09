package com.skyisland.questmanager.player.skill.defaults;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.CraftingSkill;
import com.skyisland.questmanager.player.skill.LogSkill;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.player.skill.SkillRecipe;
import com.skyisland.questmanager.player.skill.event.CraftEvent;
import com.skyisland.questmanager.ui.menu.InventoryMenu;
import com.skyisland.questmanager.ui.menu.inventory.minigames.FashioningGui;

public class FashioningSkill extends LogSkill implements Listener, CraftingSkill {
	
	private static final String inUseMessage = ChatColor.GRAY + "That fashioning bench is already in use by another player";
	
	public static final String configName = "Fashioning.yml";

	public static final class FashioningRecipe implements SkillRecipe {
		
		public int difficulty;
		
		public ItemStack input1;
		
		public ItemStack input2;
		
		public ItemStack result;
		
		public FashioningRecipe(int difficulty, ItemStack input1, ItemStack input2, ItemStack result) {
			this.difficulty = difficulty;
			if (input1 == null && input2 != null) {
				input1 = input2; //swap!
				input2 = null;
			}
			this.input1 = input1;
			this.input2 = input2;
			this.result = result;
		}
		
		@Override
		public ItemStack getDisplay() {
			return result;
		}
		
		@Override
		public String getDescription() {
			String name;
			name = YamlWriter.toStandardFormat(input1.getType().name());
			String builder = "Fashion together a ";
			
			if (input1.hasItemMeta() && input1.getItemMeta().hasDisplayName())
				name = input1.getItemMeta().getDisplayName();
			builder += "[" + name + "]";
			
			if (input2 != null) {
				name = YamlWriter.toStandardFormat(input2.getType().name());
				if (input2.hasItemMeta() && input2.getItemMeta().hasDisplayName())
					name = input2.getItemMeta().getDisplayName();
				builder += " and a [" + name + "]";
			}
			
			return builder;
		}

		@Override
		public int getDifficulty() {
			return this.difficulty;
		}
	}

	public Type getType() {
		return Skill.Type.TRADE;
	}
	
	public String getName() {
		return "Fashioning";
	}
	
	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.LEATHER);
	}
	
	public String getDescription(QuestPlayer player) {
		String ret = ChatColor.WHITE + "Fashioning Skills allow a player to take ordinary objects and "
				+ "combine them into something of new purpose";
		
		int level = player.getSkillLevel(this);
		
		
		ret += "\n\n" + ChatColor.GOLD + "Bonus Quality: +" + String.format("%.2f", (level * qualityRate));
		
		return ret;
	}

	@Override
	public int getStartingLevel() {
		return startingLevel;
	}
	
	@Override
	public String getConfigKey() {
		return "Fashioning";
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof FashioningSkill);
	}
	
	private int startingLevel;
	
	private double qualityRate;
	
	private Map<Location, QuestPlayer> tableMap;
	
	private double difficultyRate;
	
	private List<FashioningRecipe> recipes;
	
	public FashioningSkill() {
		File configFile = new File(QuestManagerPlugin.questManagerPlugin.getDataFolder(), 
				QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillPath() + configName);
		YamlConfiguration config = createConfig(configFile);

		
		if (!config.getBoolean("enabled", true)) {
			return;
		}
		
		this.startingLevel = config.getInt("startingLevel", 0);
		this.qualityRate = config.getDouble("qualityRate", .02);
		this.difficultyRate = config.getDouble("difficultyRate", .05);
		
		
		this.recipes = new LinkedList<>();
		if (!config.contains("recipes")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Didn't find any oven recipe table"
					+ "for FashioningSkill even though it's enabled!");
			return;
		} else {
			ConfigurationSection sex = config.getConfigurationSection("recipes");
			for (String key : sex.getKeys(false)) {
				if (key.startsWith("==")) {
					continue;
				}
				
				try {
					recipes.add(new FashioningRecipe(
							sex.getInt(key + ".difficulty"), sex.getItemStack(key + ".input1"),
							sex.getItemStack(key + ".input2"), sex.getItemStack(key + ".output")
							));
				} catch (Exception e) {
					e.printStackTrace();
					QuestManagerPlugin.questManagerPlugin.getLogger().warning("Skipping that one! ^");
				}
			}
		}
				
		this.tableMap = new HashMap<>();
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
		
	}
	
	private YamlConfiguration createConfig(File configFile) {
		if (!configFile.exists()) {
			YamlWriter writer = new YamlWriter();
			
			writer.addLine("enabled", true, Lists.newArrayList("Whether or not this skill is allowed to be used.",  "true | false"))
				.addLine("startingLevel", 0, Lists.newArrayList("The level given to players who don't have this skill yet", "[int]"))
				.addLine("difficultyRate", .05, Lists.newArrayList("Additional chance of failure per difficulty point", "over the player's level", "[double] .05 is 5% chance to fail per level"))
				.addLine("qualityRate", .02, Lists.newArrayList("Additional quality on a crafted item given", "per skill level"));
			
			
			Map<String, Map<String, Object>> map = new HashMap<>();
			Map<String, Object> sub = new HashMap<>();
			
			sub.put("difficulty", 0);
			ItemStack item = new ItemStack(Material.STRING);
			sub.put("input1", item);
			item = new ItemStack(Material.STRING);
			sub.put("input2", item);
			item = new ItemStack(Material.STRING);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("Tough String");
			item.setItemMeta(meta);
			sub.put("output", item);
			map.put("Tough String", sub);
			
			sub = new HashMap<>();
			sub.put("difficulty", 0);
			item = new ItemStack(Material.STRING);
			sub.put("input1", item);
			item = new ItemStack(Material.SULPHUR);
			meta = item.getItemMeta();
			meta.setDisplayName("Ash");
			item.setItemMeta(meta);
			sub.put("input2", item);
			item = new ItemStack(Material.STRING);
			meta = item.getItemMeta();
			meta.setDisplayName("Yarn");
			item.setItemMeta(meta);
			sub.put("output", item);
			map.put("Yarn", sub);
			
			sub = new HashMap<>();
			sub.put("difficulty", 10);
			item = new ItemStack(Material.ROTTEN_FLESH);
			sub.put("input1", item);
			item = new ItemStack(Material.STRING);
			meta = item.getItemMeta();
			meta.setDisplayName("Yarn");
			item.setItemMeta(meta);
			sub.put("input2", item);
			item = new ItemStack(Material.ROTTEN_FLESH, 2);
			sub.put("output", item);
			map.put("RottenFleshDup", sub);		
			
			writer.addLine("recipes", map, Lists.newArrayList("List of fashioning recipes. Names of", "input items must match exactly", "Plan difficulties carefully, as players that are", "at a level with no recipes in range (maxDifficultyRange)", "are stuck forever!", "name: {difficulty: [int], input1: [itemstack], input2: [itemstack], output: [itemstack]}"));
			
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
	public void onPlayerFashion(PlayerInteractEvent e) {	
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getWorld().getName())) {
			return;
		}
		
		if (e.getHand() != EquipmentSlot.HAND)
			return;
		
		if (e.getClickedBlock() == null)
			return;
		
		if (e.getClickedBlock().getType() != Material.NOTE_BLOCK) {
			return;
		}
		
		if (tableMap.containsKey(e.getClickedBlock().getLocation())) {
			e.getPlayer().sendMessage(inUseMessage);
			return;
		}
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(e.getPlayer());
		
		FashioningGui gui = new FashioningGui(this, e.getPlayer(), "Fashion");
		InventoryMenu menu = new InventoryMenu(qp, gui);
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(e.getPlayer(), menu);
		
		
		e.setCancelled(true);
		return;
	}

	/**
	 * Scans the available recipes given the provided input and returns the first matching recipe.
	 * If multiple recipes are defined with the same input, the first registered is returned.
	 * @return A recipe with the same input as given, if one exists. Null otherwise
	 */
	public FashioningRecipe getRecipe(ItemStack input1, ItemStack input2) {
		if (recipes.isEmpty()) {
			return null;
		}
		
		for (FashioningRecipe recipe : recipes) {
			if ((isMatch(recipe.input1, input1) && isMatch(recipe.input2, input2))
			 || (isMatch(recipe.input1, input2) && isMatch(recipe.input2, input1)))
				return recipe;
		}
		
		return null;
	}
	
	public double getFashioningChance(QuestPlayer player, FashioningRecipe recipe) {
		int level = player.getSkillLevel(this);
		return 1.0 - Math.max(0, Math.min(1, difficultyRate * ((double) (recipe.difficulty - level))));
	}
	
	private boolean isMatch(ItemStack i1, ItemStack i2) {
		if (i1 == null && i2 == null)
			return true;
		
		if (i1 == null || i2 == null) {
			return false;
		}
		
		if (i1.getType() != i2.getType() || i1.getDurability() != i2.getDurability())
			return false;
		
		if (!i1.hasItemMeta() && !i2.hasItemMeta())
			return true;
		
		if (!i1.hasItemMeta() || !i2.hasItemMeta())
			return false;
		
		ItemMeta m1 = i1.getItemMeta(), m2 = i2.getItemMeta();
		
		if (m1.getDisplayName() == null && m2.getDisplayName() == null)
			return true;
		
		if (m1.getDisplayName() == null || m2.getDisplayName() == null)
			return false;
		
		return ChatColor.stripColor(m1.getDisplayName()).equals(ChatColor.stripColor(m2.getDisplayName()));
	}
	
	public void unregisterBench(Location location) {
		tableMap.remove(location);
	}
	
	@EventHandler
	public void onCook(CraftEvent e) {
		if (e.getType() != CraftEvent.CraftingType.COOKING) {
			return;
		}
		
		double level = e.getPlayer().getSkillLevel(this);
		e.setQualityModifier(e.getQualityModifier() + (level * qualityRate));
	}

	@Override
	public List<SkillRecipe> getRecipes() {
		return new ArrayList<SkillRecipe>(recipes);
	}	
}
