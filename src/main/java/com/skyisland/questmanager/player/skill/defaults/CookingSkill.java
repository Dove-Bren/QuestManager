package com.skyisland.questmanager.player.skill.defaults;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
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
import com.skyisland.questmanager.player.skill.LogSkill;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.player.skill.event.CraftEvent;
import com.skyisland.questmanager.ui.menu.ActiveInventoryMenu;
import com.skyisland.questmanager.ui.menu.InventoryMenu;
import com.skyisland.questmanager.ui.menu.action.CollectFishAction;
import com.skyisland.questmanager.ui.menu.inventory.minigames.CombiningGui;
import com.skyisland.questmanager.ui.menu.inventory.minigames.CookingGui;

public class CookingSkill extends LogSkill implements Listener {
	
	private static final String inUseMessage = ChatColor.GRAY + "That oven is already in use by another player";
	
	public static final String configName = "Cooking.yml";
	
	public static final class OvenRecipe {
		
		public int difficulty;
		
		public ItemStack input;
		
		public ItemStack output;
		
		public OvenRecipe(int difficulty, ItemStack input, ItemStack output) {
			this.difficulty = difficulty;
			this.input = input;
			this.output = output;
		}
		
	}
	
	public static final class CookingStats {
		
		private double cookTime;
		
		private double fuelSwapTime;
		
		private int failInterval;

		public CookingStats(double cookTime, double fuelSwapTime,
				int failInterval) {
			super();
			this.cookTime = cookTime;
			this.fuelSwapTime = fuelSwapTime;
			this.failInterval = failInterval;
		}

		public double getCookTime() {
			return cookTime;
		}

		public double getFuelSwapTime() {
			return fuelSwapTime;
		}

		public int getFailInterval() {
			return failInterval;
		}
	}
	
	public static final class CombineRecipe {
		
		public int difficulty;
		
		public ItemStack input1;
		
		public ItemStack input2;
		
		public ItemStack input3;
		
		public ItemStack result;
		
		public CombineRecipe(int difficulty, ItemStack input1, ItemStack input2, ItemStack input3,
				ItemStack result) {
			this.difficulty = difficulty;
			this.input1 = input1;
			this.input2 = input2;
			this.input3 = input3;
			this.result = result;
		}
	}

	public Type getType() {
		return Skill.Type.TRADE;
	}
	
	public String getName() {
		return "Cooking";
	}
	
	public String getDescription(QuestPlayer player) {
		String ret = ChatColor.WHITE + "Fishing skill determines the types of fish caught and how difficulty it"
				+ " is to catch them.";
		
		int level = player.getSkillLevel(this);
		
		//TODO
//		ret += "\n\n" + ChatColor.GOLD + "Fish Range: " 
//				+ Math.max(0, level - maxDifficultyRange) + " - " + (level + maxDifficultyRange);
//		ret += "\n" + ChatColor.GOLD + "Catch Quality: +" + (level * qualityRate);
//		
//		ret += "\n" + ChatColor.GREEN + "Difficulty: " 
//				+ ((int) (100 - (100 * (level * reelDifficultyDiscount)))) + "%" + ChatColor.RESET;
//		ret += "\n" + ChatColor.DARK_BLUE + "Reeling Time: "
//				+ ((int) (100 - (100 * (level * timeDiscount))) + "%"); 
		
		return ret;
	}

	@Override
	public int getStartingLevel() {
		return startingLevel;
	}
	
	@Override
	public String getConfigKey() {
		return "Cooking";
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof CookingSkill);
	}
	
	private int startingLevel;
	
	private double timeDiscount;
	
	private double qualityRate;
	
	private Map<Location, QuestPlayer> furnaceMap;
	
	private double fuelSwapTime;
	
	private double baseTime;
	
	private double timeRate;

	private double bonusQuality;
	
	private int failInterval;
	
	private boolean useItemQuality;
	
	private double combineDifficultyRate;
	
	private List<OvenRecipe> oRecipes;
	
	private List<CombineRecipe> cRecipes;
	
	public CookingSkill() {
		File configFile = new File(QuestManagerPlugin.questManagerPlugin.getDataFolder(), 
				QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillPath() + configName);
		YamlConfiguration config = createConfig(configFile);

		
		if (!config.getBoolean("enabled", true)) {
			return;
		}
		
		this.startingLevel = config.getInt("startingLevel", 0);
		this.baseTime = config.getDouble("baseTime", 20.0);
		this.timeRate = config.getDouble("timeRate",  .2);
		this.timeDiscount = config.getDouble("timeDiscount", .005);
		this.fuelSwapTime = config.getDouble("fuelSwapTime", 1.0);
		this.qualityRate = config.getDouble("qualityRate", .02);
		this.useItemQuality = config.getBoolean("useItemQuality", true);
		this.failInterval = config.getInt("failInterval", 25);
		this.combineDifficultyRate = config.getDouble("combineDifficultyRate", .05);
		this.bonusQuality = config.getDouble("bonusQuality", .20);
		
		this.oRecipes = new LinkedList<>();
		if (!config.contains("oven")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Didn't find any oven recipe table"
					+ "for CookingSkill even though it's enabled!");
			return;
		} else {
			ConfigurationSection sex = config.getConfigurationSection("oven");
			for (String key : sex.getKeys(false)) {
				if (key.startsWith("==")) {
					continue;
				}
				
				try {
					oRecipes.add(new OvenRecipe(
							sex.getInt(key + ".difficulty"), sex.getItemStack(key + ".input"),
							sex.getItemStack(key + ".output")
							));
				} catch (Exception e) {
					e.printStackTrace();
					QuestManagerPlugin.questManagerPlugin.getLogger().warning("Skipping that one! ^");
				}
			}
		}
		
		this.cRecipes = new LinkedList<>();
		if (!config.contains("mixing")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Didn't find any mixing recipe table"
					+ "for CookingSkill even though it's enabled!");
			return;
		} else {
			ConfigurationSection sex = config.getConfigurationSection("mixing");
			for (String key : sex.getKeys(false)) {
				if (key.startsWith("==")) {
					continue;
				}
				
				try {
					cRecipes.add(new CombineRecipe(
							sex.getInt(key + ".difficulty"), sex.getItemStack(key + ".input1"),
							(sex.contains(key + ".input2") ? sex.getItemStack(key + ".input2") : null),
							(sex.contains(key + ".input3") ? sex.getItemStack(key + ".input3") : null),
							sex.getItemStack(key + ".output")
							));
				} catch (Exception e) {
					e.printStackTrace();
					QuestManagerPlugin.questManagerPlugin.getLogger().warning("Skipping that one! ^");
				}
			}
		}
				
		this.furnaceMap = new HashMap<>();
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
		CookingGui.setCookingSkill(this);
		CombiningGui.setCookingSkill(this);
	}
	
	private YamlConfiguration createConfig(File configFile) {
		if (!configFile.exists()) {
			YamlWriter writer = new YamlWriter();
			
			writer.addLine("enabled", true, Lists.newArrayList("Whether or not this skill is allowed to be used.",  "true | false"))
				.addLine("startingLevel", 0, Lists.newArrayList("The level given to players who don't have this skill yet", "[int]"))
				.addLine("baseTime", 20.0, Lists.newArrayList("Base time it takes to fufill a recipe", "[double] time in seconds"))
				.addLine("timeRate",  .2, Lists.newArrayList("How many more seconds are added per", "difficulty of the recipe", "[double] time in seconds"))
				.addLine("timeDiscount", .005, Lists.newArrayList("How much of the time a player gets cut", "off per skill level", "[double] 0.01 is 1%"))
				.addLine("fuelSwapTime", 1.0, Lists.newArrayList("Time between fuel swaps if the player", "doesn't pick a fuel", "low and high values are difficult", "[double] time in seconds"))
				.addLine("qualityRate", .02, Lists.newArrayList("Additional quality on a crafted item given", "per skill level"))
				.addLine("useItemQuality", true, Lists.newArrayList("Should ingredient quality be used to calculate", "product quality? If false, quality from", "qualityRate * this skill level is the only", "source of quality. Else, that quality is", "added to the sum of the ingredients", "[true|false]"))
				.addLine("failInterval", 25, Lists.newArrayList("How far from 100 (perfect center) the player", "can be without the job stalling and", "racking up failure", "[int] between 0-100"))
				.addLine("combineDifficultyRate", .05, Lists.newArrayList("Success-change penalty per level difference in", "mixing recipes and player skill", "[double] 0.05 is 5% per level"))
				.addLine("bonusQuality", .20, Lists.newArrayList("Bonus quality percent added to a", "perfect cooking craft", "[double] .2 is 20%"));
			
			Map<String, Map<String, Object>> map = new HashMap<>();
			Map<String, Object> sub = new HashMap<>();
			
			sub.put("difficulty", 5);
			ItemStack item = new ItemStack(Material.RAW_FISH);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("Trout");
			item.setItemMeta(meta);
			sub.put("input", item);
			item = new ItemStack(Material.COOKED_FISH);
			meta = item.getItemMeta();
			meta.setDisplayName("Trout");
			item.setItemMeta(meta);
			sub.put("output", item);
			map.put("Trout", sub);
			
			sub = new HashMap<>();
			sub.put("difficulty", 15);
			item = new ItemStack(Material.RAW_FISH, 1, (short) 1);
			meta = item.getItemMeta();
			meta.setDisplayName("Salmon");
			item.setItemMeta(meta);
			sub.put("input", item);
			item = new ItemStack(Material.COOKED_FISH, 1, (short) 1);
			meta = item.getItemMeta();
			meta.setDisplayName("Salmon");
			item.setItemMeta(meta);
			sub.put("output", item);
			map.put("Salmon", sub);
			
			sub = new HashMap<>();
			sub.put("difficulty", 20);
			item = new ItemStack(Material.CLAY);
			meta = item.getItemMeta();
			meta.setDisplayName("Dough");
			item.setItemMeta(meta);
			sub.put("input", item);
			item = new ItemStack(Material.BREAD);
			meta = item.getItemMeta();
			sub.put("output", item);
			map.put("Bread", sub);
			
			
			writer.addLine("oven", map, Lists.newArrayList("List of oven recipes. Names of", "input items must match exactly", "Plan difficulties carefully, as players that are", "at a level with no fish in range (maxDifficultyRange)", "are stuck forever!", "name: {difficulty: [int], icon: [itemstack]}"));
			
			map = new HashMap<>();
			sub = new HashMap<>();
			sub.put("difficulty", 10);
			item = new ItemStack(Material.WHEAT);
			sub.put("input1", item);
			item = new ItemStack(Material.WHEAT);
			sub.put("input2", item);
			item = new ItemStack(Material.CLAY);
			meta = item.getItemMeta();
			meta.setDisplayName("Dough");
			item.setItemMeta(meta);
			sub.put("output", item);
			map.put("Dough", sub);
			
			writer.addLine("mixing", map, Lists.newArrayList("List of mixing recipes. Names of", "input items must match exactly", "Difficulty follows the traditional (think magery)", "spell calculations. No rounding happens.", "name: {difficulty: [int], input1: [itemstack], input 2..3: [itemstack|null] output: [itemstack]}"));
			
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
	
	private void onPlayerCombine(PlayerInteractEvent e) {
		if (e.getClickedBlock().getType() != Material.CAULDRON) {
			return;
		}
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(e.getPlayer());
		
		CombiningGui gui = new CombiningGui(e.getPlayer(), "Mixing Pot");
		InventoryMenu menu = new InventoryMenu(qp, gui);
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(e.getPlayer(), menu);
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerCook(PlayerInteractEvent e) {		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getWorld().getName())) {
			return;
		}
		
		if (e.getHand() != EquipmentSlot.HAND)
			return;
		
		if (e.getClickedBlock() == null)
			return;
		
		if (e.getClickedBlock().getType() != Material.FURNACE &&
				e.getClickedBlock().getType() != Material.BURNING_FURNACE) {
			onPlayerCombine(e);
			return;
		}
		
		if (furnaceMap.containsKey(e.getClickedBlock().getLocation())) {
			e.getPlayer().sendMessage(inUseMessage);
			return;
		}
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(e.getPlayer());
		
		CookingGui gui = new CookingGui(e.getPlayer(), (Furnace) e.getClickedBlock().getState(),
				bonusQuality, useItemQuality
				);
		InventoryMenu menu = new ActiveInventoryMenu(qp, gui, new CollectFishAction(qp));
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(e.getPlayer(), menu);
		
		e.setCancelled(true);
		return;
	}

	/**
	 * Scans the available recipes given the provided input and returns the first matching recipe.<br />
	 * If multiple recipes are defined with the same input, the first registered is returned.
	 * @param input
	 * @return A recipe with the same input as given, if one exists. Null otherwise
	 */
	public OvenRecipe getOvenRecipe(ItemStack input) {
		if (oRecipes.isEmpty()) {
			return null;
		}
		
		for (OvenRecipe recipe : oRecipes) {
			if (isMatch(recipe.input, input)) {
				return recipe;
			}
		}
		
		return null;
	}

	/**
	 * With the provided inputs, looks for a combine recipe with the same constraints.<br />
	 * The recipe provided must match all constraints. This included names
	 * @param input
	 * @return A recipe with the same input as given, if one exists. Null otherwise
	 */
	public CombineRecipe getMixingRecipe(ItemStack input1, ItemStack input2, ItemStack input3) {
		if (oRecipes.isEmpty()) {
			return null;
		}
		
		List<ItemStack> args, inputs;
		Iterator<ItemStack> it;
		inputs = Lists.newArrayList(input1, input2, input3);
		int size = inputs.size();
		for (CombineRecipe recipe : cRecipes) {
			args = Lists.newArrayList(recipe.input1, recipe.input2, recipe.input3);
			for (ItemStack item : inputs) {
				size--;
				it = args.iterator();
				while (it.hasNext()) {
					if (isMatch(it.next(), item)) {
						it.remove();
						break;
					}
				}
				if (size < args.size())
					break;
			}
			if (args.isEmpty())
				return recipe;
		}
		
		return null;
	}
	
	public double getCombineChance(QuestPlayer player, CombineRecipe recipe) {
		int level = player.getSkillLevel(this);
		return Math.max(0, Math.min(1, combineDifficultyRate * (recipe.difficulty - level)));
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
	
	public void unregisterOven(Location location) {
		furnaceMap.remove(location);
	}
	
	@EventHandler
	public void onCook(CraftEvent e) {
		if (e.getType() != CraftEvent.CraftingType.COOKING) {
			return;
		}
		
		double level = e.getPlayer().getSkillLevel(this);
		e.setQualityModifier(e.getQualityModifier() + (level * qualityRate));
	}
	
	public CookingStats getCookingStats(OvenRecipe recipe, QuestPlayer player) {
		double cookTime, fuelSwapTime;
		int failInterval;
		int lvl = player.getSkillLevel(this);
		
		cookTime = baseTime + (timeRate * (double) recipe.difficulty);
		cookTime *= 1.0 - ((double) lvl * timeDiscount);
		fuelSwapTime = this.fuelSwapTime;
		failInterval = this.failInterval;
		
		
		
		return new CookingStats(cookTime, fuelSwapTime, failInterval);
	}
	
}
