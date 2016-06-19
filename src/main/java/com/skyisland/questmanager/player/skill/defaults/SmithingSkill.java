package com.skyisland.questmanager.player.skill.defaults;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.effects.ChargeEffect;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.CraftingSkill;
import com.skyisland.questmanager.player.skill.LogSkill;
import com.skyisland.questmanager.player.skill.QualityItem;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.player.skill.SkillRecipe;
import com.skyisland.questmanager.player.skill.event.CraftEvent;
import com.skyisland.questmanager.ui.actionsequence.ForgeSequence;
import com.skyisland.questmanager.ui.menu.InventoryMenu;
import com.skyisland.questmanager.ui.menu.action.FillableInventoryAction;
import com.skyisland.questmanager.ui.menu.inventory.ContributionInventory;
import com.skyisland.questmanager.ui.menu.inventory.minigames.SmeltingGui;

public class SmithingSkill extends LogSkill implements Listener, CraftingSkill {
	
	public static final String configName = "Smithing.yml";
	
	private static final String badRangeMessage = ChatColor.RED + "You knew how to work the metal, but didn't possess "
			+ "enough skill to truly see your creation live.";
	
	private static final String noRecipeMessage = ChatColor.RED + "Your work with the metal was long, but the "
			+ "result wasn't anything of use. The unused ingredients have been returned to your inventory.";
	
	private static final String failMessage = ChatColor.RED + "Despite your efforts, the craft ended in failure. Perhaps "
			+ "with a little more skill..?";
	
	private static final String noSmeltingRecipeMessage = ChatColor.RED + "You don't know a recipe that uses those "
			+ "ingredients";
	
	private static final String winMessage = ChatColor.GREEN + "You successfully forged ";
	
	private static final Sound loseSound = Sound.BLOCK_GLASS_BREAK;
	
	private static final Sound winSound = Sound.BLOCK_ANVIL_USE;
	
	private static final ChargeEffect successEffect = new ChargeEffect(Effect.LAVA_POP);
	
	private static final ChargeEffect failEffect = new ChargeEffect(Effect.SMALL_SMOKE);
	
	public static final class Metal implements SkillRecipe {
		
		protected List<ItemStack> inputs;
		
		protected ItemStack image;
		
		protected String name;
		
		protected int difficulty;
		
		public Metal(String name, int difficulty, ItemStack image, List<ItemStack> inputs) {
			this.difficulty = difficulty;
			this.name = name;
			this.image = image;
			
			//go through and ensure only 1 itemstack per item type
			this.inputs = new LinkedList<ItemStack>();
			for (ItemStack item : inputs) {
				if (this.inputs.isEmpty()) {
					this.inputs.add(item);
					continue;
				}
				
				boolean flag = false;
				for (ItemStack existing : this.inputs) {
					if (existing.getType() == item.getType() && existing.getDurability() == item.getDurability()) {
						existing.setAmount(existing.getAmount() + item.getAmount()); //add together
						flag = true;
						break;
					}
				}
				
				if (flag)
					continue;
				
				this.inputs.add(item);
			}
		}
		
		public boolean isMetal(ItemStack sample) {
			if (sample == null)
				return false;
			String sampleName = null, imageName = null;
			if (sample.hasItemMeta() && sample.getItemMeta().hasDisplayName())
				sampleName = sample.getItemMeta().getDisplayName();
			
			if (image.hasItemMeta() && image.getItemMeta().hasDisplayName())
				imageName = image.getItemMeta().getDisplayName();
			
			return sample.getType() == image.getType() && sample.getDurability() == image.getDurability()
					&& ((sampleName == null && imageName == null) || sampleName.equals(imageName));
		}
		
		/**
		 * Returns how many of this material can be made with the given inputs.
		 * Input types must match exactly, least a -1 is returned. If they do match, the maximum
		 * number of ingots that could be made are returned. If the types are correct but there is too
		 * little of a certain type (or all) of input, 0 may be returned. This means 0, not 'it didn't match'
		 * @return -1 if this material cannot be made with those inputs, The number of whole units of this material
		 * that could be produced otherwise
		 */
		public int getMeterialCount(List<ItemStack> inputs) {
			//check input types first just to make sure
			Map<ItemStack, ItemStack> matchMap = new HashMap<>();
			int matsLeft = this.inputs.size();
			Iterator<ItemStack> in = inputs.iterator();
			ItemStack input, match;
			while (in.hasNext()) {
				input = in.next();
				if (input == null)
					continue;
				
				if (matsLeft <= 0)
					return -1; //ran out of recipe items but have more inputs

				match = null;
				for (ItemStack item : this.inputs) {
					if (matchMap.containsKey(item))
						continue; //already matched
					
					if (item.getType() == input.getType() && item.getDurability() == input.getDurability()) {
						match = item;
						break;
					}
				}
				
				if (match == null)
					return -1;
				
				matchMap.put(match, input);
				matsLeft--;
			}
			
			if (matsLeft != 0) 
				return -1;
			
			//all inputs matched. Now calculate max return
			int minimum = -1, quotient;
			for (ItemStack item : matchMap.keySet()) {
				quotient = matchMap.get(item).getAmount() / item.getAmount(); //int division rounds down for free :)
				if (minimum == -1 || minimum > quotient)
					minimum = quotient;
			}
			
			return minimum;
			
			
		}

		@Override
		public ItemStack getDisplay() {
			return image;
		}
		
		@Override
		public String getDescription() {
			return "With your tool, select a forge's heatbars. Then, combine " + delistInputs();
		}


		@Override
		public int getDifficulty() {
			return difficulty;
		}
		
		private String delistInputs() {
			if (inputs == null || inputs.isEmpty())
				return "";
			
			String builder = "";
			Iterator<ItemStack> it = inputs.iterator();
			ItemStack cur = null;
			while (it.hasNext()) {
				if (cur != null) {
					builder += ", ";
					if (it.hasNext())
						builder += "and ";
				}
				cur = it.next();
				builder += (cur.getAmount() > 1 ? (cur.getAmount() + "x ") : "a ");
				if (cur.hasItemMeta() && cur.getItemMeta().hasDisplayName())
					builder += cur.getItemMeta().getDisplayName();
				else
					builder += YamlWriter.toStandardFormat(cur.getData().toString());
				
			}
			
			return builder;
		}
		
	}
	
	private static final class ForgeRecipe implements SkillRecipe {
		
		protected Metal base;
		
		protected List<ItemStack> inputs;
		
		protected ItemStack reward;
		
		// String name;
		
		protected int hammerTimes;
		
		protected int difficulty;
		
		private boolean needsCut;
		
		private boolean needsQuelch;
		
		
		public ForgeRecipe(String name, int difficulty, Metal base, List<ItemStack> inputs, ItemStack reward,
				int hammerTimes, boolean cut, boolean quelch) {
			//this.name = name;
			this.reward = reward;
			this.hammerTimes = hammerTimes;
			this.needsCut = cut;
			this.needsQuelch = quelch;
			this.difficulty = difficulty;
			this.base = base;
			
			//go through and ensure only 1 itemstack per item type
			this.inputs = new LinkedList<ItemStack>();
			for (ItemStack item : inputs) {
				if (this.inputs.isEmpty()) {
					this.inputs.add(item);
					continue;
				}
				
				boolean flag = false;
				for (ItemStack existing : this.inputs) {
					if (existing.getType() == item.getType() && existing.getDurability() == item.getDurability()) {
						existing.setAmount(existing.getAmount() + item.getAmount()); //add together
						flag = true;
						break;
					}
				}
				
				if (flag)
					continue;
				
				this.inputs.add(item);
			}
		}

		/**
		 * Checks and returns whether the given combination matched this recipe
		 */
		public boolean isMatch(int hammerHits, boolean cut, boolean quelched, Metal base, List<ItemStack> inputs) {
			//easiest part first: check ints and bools
			if (hammerHits != hammerTimes || cut != needsCut || quelched != needsQuelch || !base.name.equals(this.base.name))
				return false;
			
			//go through inputs, marking them off as we go
			//gonna just reuse Metal recipe
			//check input types first just to make sure
			Map<ItemStack, ItemStack> matchMap = new HashMap<>();
			int matsLeft = this.inputs.size();
			Iterator<ItemStack> in = inputs.iterator();
			ItemStack input, match;
			while (in.hasNext()) {
				input = in.next();
				if (input == null)
					continue;

				if (matsLeft <= 0)
					return false; //ran out of recipe items but have more inputs
				
				match = null;
				for (ItemStack item : this.inputs) {
					if (matchMap.containsKey(item))
						continue; //already matched
					
					if (item.getType() == input.getType() && item.getDurability() == input.getDurability())
					if ((!item.hasItemMeta() && !input.hasItemMeta()) 
						|| (!item.getItemMeta().hasDisplayName() && !input.getItemMeta().hasDisplayName())
						|| (item.getItemMeta().getDisplayName().equals(input.getItemMeta().getDisplayName()))) {
						match = item;
						break;
					}
				}
				
				if (match == null)
					return false;
				
				matchMap.put(match, input);
				matsLeft--;
			}
			
			if (matsLeft != 0) 
				return false;
			
			return true;
		}

		@Override
		public String getDescription() {
						
			String builder = "Start by taking " + base.name + " and combine it with " + delistInputs() + ". Then, heat the metal "
					+ "near a forge. Take the hot metal and hammer it " + hammerTimes + " times, keeping the metal hot.";
			
			if (needsCut)
				builder += " Next, cut the metal using the forge's tools.";
			
			if (needsQuelch)
				builder += " Then, dip the hot metal in water or oil to quelch and finalize your creation.";
			else 
				builder += " Then, let the metal cool.";
			
			
			return builder;
		}
		
		private String delistInputs() {
			if (inputs == null || inputs.isEmpty())
				return "";
			
			String builder = "";
			Iterator<ItemStack> it = inputs.iterator();
			ItemStack cur = null;
			while (it.hasNext()) {
				if (cur != null) {
					builder += ", ";
					if (it.hasNext())
						builder += "and ";
				}
				cur = it.next();
				builder += (cur.getAmount() > 1 ? (cur.getAmount() + "x ") : "a ");
				if (cur.hasItemMeta() && cur.getItemMeta().hasDisplayName())
					builder += cur.getItemMeta().getDisplayName();
				else
					builder += YamlWriter.toStandardFormat(cur.getData().toString());
				
			}
			
			return builder;
		}


		@Override
		public ItemStack getDisplay() {
			return reward;
		}


		@Override
		public int getDifficulty() {
			return difficulty;
		}
		
		
	}

	public Type getType() {
		return Skill.Type.TRADE;
	}
	
	public String getName() {
		return "Smithing";
	}
	
	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.ANVIL);
	}
	
	public String getDescription(QuestPlayer player) {//proficient
		String ret = ChatColor.WHITE + "Smiths take ore and smelt it to usable metal, and then work the metal into "
				+ "shapes both beautiful and useful.";
		
		int level = player.getSkillLevel(this);
		
		ret += "\n\n" + ChatColor.GOLD + "Tree Range: " 
				+ Math.max(0, level - maxDifficultyRange) + " - " + (level + maxDifficultyRange);
		ret += "\n" + ChatColor.GOLD + "Wood Quality: +" + (level * qualityRate);
		
		ret += "\n" + ChatColor.GREEN + "Hit Discount: " 
				+ ((float) (level * hitBonus)) + ChatColor.RESET;
		
		return ret;
	}

	@Override
	public int getStartingLevel() {
		return startingLevel;
	}
	
	@Override
	public String getConfigKey() {
		return "Smithing";
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof SmithingSkill);
	}
	
	private int startingLevel;
	
	private double heatBase;
	
	private double heatRate;
	
	private double coolBase;
	
	private double coolRate;
	
	private double hitBase;
	
	private double hitRate;
	
	private double hitBonus;
	
	private double difficultyRate;
	
	private int masteryOffset;
	
	private int maxDifficultyRange;
	
	private double qualityRate;
	
	//private double damageQualityRate;
	
	private List<ForgeRecipe> forgeRecipes;
	
	private Map<String, Metal> metals;
	
	private Material toolType;
	
	private String toolName;
	
	private Material anvilType = Material.ANVIL;
	
	private Material smeltingType;
	
	private List<Player> activePlayers;
	
	public SmithingSkill() {
		File configFile = new File(QuestManagerPlugin.questManagerPlugin.getDataFolder(),
				QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillPath() + configName);
		YamlConfiguration config = createConfig(configFile);

		
		if (!config.getBoolean("enabled", true)) {
			return;
		}
		
		this.startingLevel = config.getInt("startingLevel", 0);
		this.heatBase = config.getDouble("heatBase", 3);
		this.heatRate = config.getDouble("heatRate", .02);
		this.coolBase = config.getDouble("coolBase", 5);
		this.coolRate = config.getDouble("coolRate", .03);
		this.hitBase = config.getDouble("hitBase", .8);
		this.hitRate = config.getDouble("hitRate", .015);
		this.hitBonus = config.getDouble("hitBonus", .002);
		this.toolType = Material.getMaterial(config.getString("toolType", "SHEARS"));
		this.toolName = config.getString("toolName", null);
		this.anvilType = Material.getMaterial(config.getString("anvilType", "ANVIL"));
		this.smeltingType = Material.getMaterial(config.getString("smeltingType", "IRON_FENCE"));
		this.difficultyRate = config.getDouble("difficultyRate", .05);
		this.masteryOffset = config.getInt("masteryOffset", 5);
		this.maxDifficultyRange = config.getInt("maxDifficultyRange", 20);
		this.qualityRate = config.getDouble("qualityRate", 0.01);
		//this.damageQualityRate = config.getDouble("damageQualityRate", .25);
		
		this.metals = new HashMap<>();
		if (!config.contains("metals")) {
			QuestManagerPlugin.logger.warning("Didn't find any metal table"
					+ "for SmithingSkill even though it's enabled!");
			return;
		} else {
			/*
			 * metals:
			 * 	Iron:
			 *    input: 
			 *      - [itemstack]
			 *      - [itemstack]
			 *    difficulty: 25
			 *    output: [itemstack]
			 *  Cobalt:
			 *    ..
			 *  
			 */
			ConfigurationSection sex = config.getConfigurationSection("metals"), subsex;
			for (String key : sex.getKeys(false)) {
				if (key.startsWith("==")) {
					continue;
				}
								
				subsex = sex.getConfigurationSection(key);
				try {
					@SuppressWarnings("unchecked")
					List<ItemStack> inputs = (List<ItemStack>) subsex.getList("inputs");
					metals.put(key, new Metal(key, subsex.getInt("difficulty"), subsex.getItemStack("output"), inputs));
				} catch (Exception e) {
					e.printStackTrace();
					QuestManagerPlugin.logger.warning("Skipping that one! ^");
				}
				
			}
		}
		
		this.forgeRecipes = new LinkedList<>();
		if (metals.isEmpty()) {
			QuestManagerPlugin.logger.warning("Since there are no defined metals, recipes are "
					+ "not being read in.");
		} else {
			if (!config.contains("recipes")) {
				QuestManagerPlugin.logger.warning("Didn't find any recipe table"
						+ "for SmithingSkill even though it's enabled!");
				return;
			} else {
				/*
				 * recipes:
				 *   Iron_Sword:
				 *     base: [Metal]
				 *     inputs: 
				 *       - [ItemStack]
				 *     output: [ItemStack]
				 *     hammerTimes: [int]
				 *     cut: [true/false]
				 *     quelch: [true/false]
				 */
				ConfigurationSection sex = config.getConfigurationSection("recipes"), subsex;
				for (String key : sex.getKeys(false)) {
					if (key.startsWith("==")) {
						continue;
					}
									
					subsex = sex.getConfigurationSection(key);
					try {
						//String name, int difficulty, Metal input, ItemStack reward, int hammerTimes, boolean cut, boolean quelch
						@SuppressWarnings("unchecked")
						List<ItemStack> inputs = (List<ItemStack>) subsex.getList("inputs");
						if (!metals.containsKey(subsex.getString("base"))) {
							QuestManagerPlugin.logger.warning("Unable to find base metal "
									+ subsex.getString("base") + " for craft " + key);
							continue;
						}
						
						forgeRecipes.add(new ForgeRecipe(key, subsex.getInt("difficulty"), metals.get(subsex.getString("base")),
								inputs, subsex.getItemStack("output"), subsex.getInt("hammerTimes"),
								subsex.getBoolean("cut"), subsex.getBoolean("quelch")));
					} catch (Exception e) {
						e.printStackTrace();
						QuestManagerPlugin.logger.warning("Skipping that one! ^");
					}
					
				}
			}
		}
		
		this.activePlayers = new LinkedList<>();
				
		//LumberjackSequence.setSkillLink(this);
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	private YamlConfiguration createConfig(File configFile) {
		if (!configFile.exists()) {
			YamlWriter writer = new YamlWriter();
			
			writer.addLine("enabled", true, Lists.newArrayList("Whether or not this skill is allowed to be used.",  "true | false"))
				.addLine("startingLevel", 0, Lists.newArrayList("The level given to players who don't have this skill yet", "[int]"))
				.addLine("heatBase", 3.0, Lists.newArrayList("Base number of seconds heated metal will stay heated", "[double] seconds"))
				.addLine("heatRate", .02, Lists.newArrayList("Rate at which the time metal stays heated is decreased", "per difficulty of the metal", "[double] seconds per difficulty"))
				.addLine("coolBase", 5, Lists.newArrayList("Base time a blade must sit before it cools without", "quelching. This is like the 'finish' timeout", "[double] seconds"))
				.addLine("hitBase", .8, Lists.newArrayList("Chance a player has to successfully hit the heated", "metal when trying to hammer it. This is the", "exact rate at the same level as craft difficulty", "[double] change. 0.80 is 80%"))
				.addLine("hitRate", .015, Lists.newArrayList("Chance to hit taken away per point in difficulty", "that a craft is above a player's level", "[double] .01 is 1%"))
				.addLine("hitBonus", .002, Lists.newArrayList("Bonus to chance to hit given to a player per", "skill level", "[double] .01 is 1%"))
				.addLine("toolType", "SHEARS", Lists.newArrayList("The item type to be considered a hammer", "and have to be kept in the players hand", "[Material] this is a bukkit Material! IT must match! UPPERCAE"))
				.addLine("toolName", null, Lists.newArrayList("If your tool must have a name to be considered", "real, what is it", "[String] can be null for no name"))
				.addLine("anvilType", "ANVIL", Lists.newArrayList("What block counts as an anvil", "[Material]"))
				.addLine("smeltingType", "IRON_FENCE", Lists.newArrayList("Block type used to smelt and cut crafts", "[Material]"))
				.addLine("difficultyRate", .05, Lists.newArrayList("Chance that a craft will succeed taken away", "per point in craft difficulty over the", "player's adjusted mastery level", "[double] .05 is 5%"))
				.addLine("masteryOffset", 5, Lists.newArrayList("Levels over a craft difficulty a player must", "be to achieve a 100% chance of performing the craft", "[int] skill levels"))
				.addLine("maxDifficultyRange", 20, Lists.newArrayList("Biggest gap between player and craft difficulty", "that will be allowed to even be attempted", "[int] larger than 0"))
				.addLine("qualityRate", 0.01, Lists.newArrayList("Bonus to quality per mining skill level", "[double] .01 is 1%"))
				;//.addLine("damageQualityRate", .25, Lists.newArrayList("Per 1.0 point in quality away from 1 (default),", "how much should damage be multiplied by?", "for example, .25 means at quality 2.0, a", "forged weapon will do 125% damage", "[double] percent per 1.0 difference from normal"));
			

			
			/*
			 * 
				this.toolType = Material.getMaterial(config.getString("toolType", "SHEARS"));
				this.toolName = config.getString("toolName", null);
				this.anvilType = Material.getMaterial(config.getString("anvilType", "ANVIL"));
				this.smeltingType = Material.getMaterial(config.getString("smeltingType", "IRON_FENCE"));
			 *  
			 */
			
			Map<String, Map<String, Object>> map = new HashMap<>();
			Map<String, Object> sub = new HashMap<>();
			List<ItemStack> inputs = new LinkedList<>();;
			ItemStack item;
			ItemMeta meta;
			
			sub.put("difficulty", 10);
			item = new ItemStack(Material.IRON_ORE, 3);
			meta = item.getItemMeta();
			meta.setDisplayName("Copper Ore");
			item.setItemMeta(meta);
			inputs.add(item);
			sub.put("inputs", inputs);
			item = new ItemStack(Material.CLAY_BRICK);
			meta = item.getItemMeta();
			meta.setDisplayName("Copper Ingot");
			meta.setLore(Lists.newArrayList("Quality ingot of Copper. Can be", "useful in crafts"));
			item.setItemMeta(meta);
			sub.put("output", item);
			map.put("Copper", sub);
			
			
			sub = new HashMap<>();
			inputs = new LinkedList<>();
			sub.put("difficulty", 25);
			item = new ItemStack(Material.IRON_ORE, 2);
			meta = item.getItemMeta();
			meta.setDisplayName("Copper Ore");
			item.setItemMeta(meta);
			inputs.add(item);
			item = new ItemStack(Material.FLINT, 1);
			meta = item.getItemMeta();
			meta.setDisplayName("Tin Chunk");
			item.setItemMeta(meta);
			inputs.add(item);			
			sub.put("inputs", inputs);
			
			item = new ItemStack(Material.CLAY_BRICK);
			meta = item.getItemMeta();
			meta.setDisplayName("Bronze Ingot");
			meta.setLore(Lists.newArrayList("Ingot of sturdy bronze."));
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			item.setItemMeta(meta);
			sub.put("output", item);
			map.put("Bronze", sub);
			
			
			writer.addLine("metals", map, Lists.newArrayList("List of metals and their components.", "Plan difficulties carefully, as players that are", "at a level with no wood in range (maxDifficultyRange)", "are stuck forever!"));
			
			
			
			/*
			 * recipes:
			 *   Iron_Sword:
			 *     base: [Metal]
			 *     inputs:
			 *       - [ItemStack]
			 *     output: [ItemStack]
			 *     hammerTimes: [int]
			 *     cut: [true/false]
			 *     quelch: [true/false]
			 *     difficulty: 12
			 */
			
			map = new HashMap<>();
			sub = new HashMap<>();
			inputs = new LinkedList<>();
			sub.put("difficulty", 8);
			sub.put("base", "Copper");
			sub.put("hammerTimes", 5);
			sub.put("cut", true);
			sub.put("quelch", true);
			item = new ItemStack(Material.END_ROD, 1);
			meta = item.getItemMeta();
			meta.setDisplayName("Blade Hilt");
			item.setItemMeta(meta);
			inputs.add(item);			
			sub.put("inputs", inputs);
			
			item = new ItemStack(Material.WOOD_SWORD);
			meta = item.getItemMeta();
			meta.setDisplayName("Copper Knife");
			meta.setLore(Lists.newArrayList("Hand-Crafted copper knife.", "Was it worth it?"));
			item.setItemMeta(meta);
			sub.put("output", item);
			map.put("Copper_Knife", sub);
			
			
			sub = new HashMap<>();
			inputs = new LinkedList<>();
			sub.put("difficulty", 12);
			sub.put("hammerTimes", 8);
			sub.put("base", "Copper");
			sub.put("cut", true);
			sub.put("quelch", true);
			item = new ItemStack(Material.END_ROD, 1);
			meta = item.getItemMeta();
			meta.setDisplayName("Blade Hilt");
			item.setItemMeta(meta);
			inputs.add(item);			
			item = new ItemStack(Material.SULPHUR, 1);
			meta = item.getItemMeta();
			meta.setDisplayName("Ash");
			item.setItemMeta(meta);
			inputs.add(item);		
			sub.put("inputs", inputs);
			
			item = new ItemStack(Material.STONE_SWORD);
			meta = item.getItemMeta();
			meta.setDisplayName("Copper Sword");
			meta.setLore(Lists.newArrayList("Basic sword crafted with a", "single sharpened, tempered edge."));
			item.setItemMeta(meta);
			sub.put("output", item);
			map.put("Copper_Sword", sub);
			
			writer.addLine("recipes", map, Lists.newArrayList("Recipes that use the forging action sequence"));
		
			
			
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
	public void onPlayerSmith(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (activePlayers.contains(e.getPlayer())) {
			//e.getPlayer().sendMessage(ChatColor.YELLOW + "You're already involved in a wood chopping sequence");
			return;
		}

		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getWorld().getName())) {
			return;
		}

		if (!isTool(e.getItem())) {
			return;
		}

		if (e.getClickedBlock().getType() == anvilType) {
			onCraftStart(e);
			e.setCancelled(true);
		} else if (e.getClickedBlock().getType() == smeltingType) {
			onSmeltStart(e);
			e.setCancelled(true);
		}
		
		
	}
	
	private void onSmeltStart(PlayerInteractEvent e) {
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(e.getPlayer());
		if (activePlayers.contains(e.getPlayer())) {
			e.getPlayer().sendMessage("You're already in the middle of a craft");
			return;
		}
		
		int max = 0;
		for (Metal metal : metals.values()) {
			if (metal.inputs.size() > max)
				max = metal.inputs.size();
		}
		
		ContributionInventory inv = new ContributionInventory(e.getPlayer(), new FillableInventoryAction() {
			
			private ItemStack[] items;
			
			public void onAction() {
				if (items == null || items.length < 1) 
					return;
				
				onComponentPick(qp, items);
			}
			
			public void provideItems(ItemStack[] items) {
				this.items = items;
			}
		}, max, null, "Select Metal Ingredients/Ores", true);
		InventoryMenu menu = new InventoryMenu(qp, inv);
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(e.getPlayer(), menu);
		
	}
	
	private void onComponentPick(QuestPlayer player, ItemStack[] inputs) {
		
		if (!player.getPlayer().isOnline()) {
			return;
		}
		
		List<ItemStack> ingredients = Lists.newArrayList(inputs);
		
		//first, try and find a matching recipe
		Metal recipe = null;
		int yield = 0;
		for (Metal metal : metals.values()) {
			yield = metal.getMeterialCount(ingredients);
			if (yield != -1) {
				recipe = metal;
				break;
			}
		}
		
		Player p = player.getPlayer().getPlayer();
		
		if (recipe == null) {
			p.sendMessage(noSmeltingRecipeMessage);
			
			boolean trip = false;
			for (ItemStack input : inputs) {
				if (input != null && !(p.getInventory().addItem(input)).isEmpty()) {
					if (!trip) {
						p.sendMessage(ChatColor.RED + "There is no space left in your inventory");
						trip = true;
					}
					p.getWorld().dropItem(p.getEyeLocation(), input);
				}
			}
			return;
		}
		
		int skillLevel = player.getSkillLevel(this);
		ItemStack item = recipe.image.clone();
		item.setAmount(yield);
		QualityItem outcome = new QualityItem(item, 1.0);
		
		CraftEvent event = new CraftEvent(player, CraftEvent.CraftingType.SMITHING, recipe.difficulty, outcome);
		event.setQualityModifier(event.getQualityModifier() + ((double) skillLevel * qualityRate));
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isFail()) {
			p.sendMessage(failMessage);
			
			boolean trip = false;
			for (ItemStack input : inputs) {
				if (input != null && !(p.getInventory().addItem(input)).isEmpty()) {
					if (!trip) {
						p.sendMessage(ChatColor.RED + "There is no space left in your inventory");
						trip = true;
					}
					p.getWorld().dropItem(p.getEyeLocation(), input);
				}
			}
			return;
		}
		
		outcome = event.getOutcome();
		outcome.setQuality(outcome.getQuality() * event.getQualityModifier());
		outcome.getUnderlyingItem().setAmount((int) Math.round(outcome.getUnderlyingItem().getAmount() * event.getQuantityModifier()));
		
		//clicked with smelting type and the tool. Start a smelting action
		SmeltingGui gui = new SmeltingGui(this, player.getPlayer().getPlayer(), recipe.difficulty, 
				ingredients, 5, 7, .3, 5, 0.20, outcome);
		InventoryMenu menu = new InventoryMenu(player, gui);
		
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(player.getPlayer().getPlayer(), menu);
		gui.start();
	}
	
	private void onCraftStart(PlayerInteractEvent e) {
		//clicked on anvil type with the tool
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(e.getPlayer());
		if (activePlayers.contains(e.getPlayer())) {
			e.getPlayer().sendMessage("You're already in the middle of a craft");
			return;
		}
		
		ContributionInventory inv = new ContributionInventory(e.getPlayer(), new FillableInventoryAction() {
			
			private ItemStack[] items;
			
			public void onAction() {
				if (items == null || items.length < 1) 
					return;
				
				onMetalPick(qp, items[0]);
			}
			
			public void provideItems(ItemStack[] items) {
				this.items = items;;
			}
		}, 1, 
				(item) -> {
						for (Metal metal : metals.values()) {
							if (metal.isMetal(item)) 
								return true;
						}
						return false;
				}, "Select Base Metal");
		InventoryMenu menu = new InventoryMenu(qp, inv);
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(e.getPlayer(), menu);

		
	}
	
	private void onMetalPick(QuestPlayer player, ItemStack base) {
		if (!player.getPlayer().isOnline() || base == null) {
			return;
		}
		
		
		Metal material = null;
		for (Metal metal : metals.values()) {
			if (metal.isMetal(base)) {
				material = metal;
				break;
			}
		}
		
		final Metal baseMetal = material;
		
		//now we have our base material, but still need the rest of the inputs. We need to pop another menu up
		//to collect those
		ContributionInventory inv = new ContributionInventory(player.getPlayer().getPlayer(), new FillableInventoryAction() {
			
			private ItemStack[] items;
			
			public void onAction() {
				submitForgeIngredients(player, baseMetal, items);
			}
			
			public void provideItems(ItemStack[] items) {
				this.items = items;
			}
		}, 9, 
				null, "Select Additional Ingredients", true);
		InventoryMenu menu = new InventoryMenu(player, inv);
		
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(player.getPlayer().getPlayer(), menu);
	}
	
	private void submitForgeIngredients(QuestPlayer player, Metal base, ItemStack ...extraInputs) {
		
		//ready to start the game. Just gotta figure out some parameters first
		
		if (!player.getPlayer().isOnline()) {
			return;
		}
		
		double heatTime, coolTime, hitChance;
		
		int skillLevel = player.getSkillLevel(this);
		int levelDifference = Math.max(0, base.difficulty - skillLevel);
		
		heatTime = heatBase - (heatRate * base.difficulty);
		coolTime = coolBase - (coolRate * base.difficulty);
		hitChance = hitBase - (hitRate * levelDifference);
		  hitChance += (hitBonus * skillLevel);
		
		  ForgeSequence seq = new ForgeSequence(this, player, base.name, base, Lists.newArrayList(extraInputs),
				  heatTime, coolTime, hitChance);
		  this.activePlayers.add(player.getPlayer().getPlayer());
		  seq.start();
	}

	public void submitJob(QuestPlayer player, Metal base, List<ItemStack> inputs, int hammers, boolean cut, boolean quelch) {
		Player p = player.getPlayer().getPlayer();
		
		ForgeRecipe recipe = getForgeRecipe(base, inputs, hammers, cut, quelch);
		if (recipe == null) {
			p.sendMessage(noRecipeMessage);
			p.getWorld().playSound(p.getLocation(), loseSound, 1, 1);
			
			boolean trip = false;
			for (ItemStack input : inputs) {
				if (input != null && !(p.getInventory().addItem(input)).isEmpty()) {
					if (!trip) {
						p.sendMessage(ChatColor.RED + "There is no space left in your inventory");
						trip = true;
					}
					p.getWorld().dropItem(p.getEyeLocation(), input);
				}
			}
			
			failEffect.play(p, p.getLocation());
			this.performMinor(player, base.difficulty, true);
			return;
		}
		
		int skillLevel = player.getSkillLevel(this);
		
		if (recipe.difficulty > skillLevel + maxDifficultyRange) {
			p.sendMessage(badRangeMessage);
			p.getWorld().playSound(p.getLocation(), loseSound, 1, 1);

			this.performMinor(player, recipe.difficulty, true);
			failEffect.play(p, p.getLocation());
			return;
		}
		
		int adjustedOffset = Math.max(0, recipe.difficulty - (skillLevel - masteryOffset));
		double successChance = 1 - (difficultyRate * adjustedOffset);
		if (random.nextDouble() >= successChance) {
			p.sendMessage(failMessage);
			p.getWorld().playSound(p.getLocation(), loseSound, 1, 1);

			this.perform(player, recipe.difficulty, true);
			failEffect.play(p, p.getLocation());
			return;
		}

		CraftEvent event = new CraftEvent(player, CraftEvent.CraftingType.SMITHING, recipe.difficulty, 
				new QualityItem(addSignature(recipe.reward.clone(), player), 1.0));
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isFail()) {
			p.sendMessage(failMessage);
			p.getWorld().playSound(p.getLocation(), loseSound, 1, 1);

			this.perform(player, recipe.difficulty, true);
			failEffect.play(p, p.getLocation());
			return;
		}
		
		perform(player, recipe.difficulty, false);
		p.getWorld().playSound(p.getLocation(), winSound, 1, 1);
		successEffect.play(p, p.getLocation());
		
		event.setQualityModifier(event.getQualityModifier() + skillLevel * qualityRate);
		
		QualityItem result = event.getOutcome();
		
		result.setQuality(result.getQuality() * event.getQualityModifier());
		result.getUnderlyingItem().setAmount((int) Math.round(result.getUnderlyingItem().getAmount() * event.getQuantityModifier()));
		
		String name;
		if (result.getItem().getItemMeta() == null || result.getItem().getItemMeta().getDisplayName() == null) {
			name = YamlWriter.toStandardFormat(result.getItem().getType().toString());
		} else {
			name = result.getItem().getItemMeta().getDisplayName();
		}
		
		FancyMessage msg = new FancyMessage(winMessage)
				.color(ChatColor.GREEN)
			.then(result.getItem().getAmount() > 1 ? result.getItem().getAmount() + "x " : "a ")
			.then("[" + name + "]")
				.color(ChatColor.DARK_PURPLE)
				.itemTooltip(result.getItem());
		
		
		msg.send(p);
		if (!(p.getInventory().addItem(result.getItem())).isEmpty()) {
			p.sendMessage(ChatColor.RED + "There is no space left in your inventory");
			p.getWorld().dropItem(p.getEyeLocation(), result.getItem());
		}
		
	}
	
	public void playerFinish(QuestPlayer player) {
		activePlayers.remove(player.getPlayer());
	}

	/**
	 * Checks whether the given item is infact the smithing tool
	 */
	public boolean isTool(ItemStack item) {
		if (item == null || item.getType() != toolType) {
			return false;
		}
		
		if (toolName != null && (!item.hasItemMeta() || !item.getItemMeta().getDisplayName().equals(toolName))) {
			return false; //didn't have a name or it didn't match, but we had a registered name
		}
		
		return true;
	}
	
	public boolean isAnvil(Block block) {
		return block != null && block.getType() == anvilType;
	}
	
	public boolean isCutter(Block block) {
		return block != null && block.getType() == smeltingType;
	}

	@Override
	public List<SkillRecipe> getRecipes() {
		List<SkillRecipe> list = new ArrayList<>(metals.values());
		list.addAll(forgeRecipes);
		return list;
	}
	
	private ForgeRecipe getForgeRecipe(Metal base, List<ItemStack> inputs, int hits, boolean cut, boolean quelch) {
		if (forgeRecipes.isEmpty()) 
			return null;
		
		for (ForgeRecipe recipe : forgeRecipes) {
			if (recipe.isMatch(hits, cut, quelch, base, inputs))
				return recipe;
		}
		
		return null;
	}
	
	private ItemStack addSignature(ItemStack item, QuestPlayer player) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new LinkedList<>();
		
		lore.add(" ");
		lore.add(ChatColor.DARK_AQUA + "Forged by " + player.getPlayer().getName());
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
}
