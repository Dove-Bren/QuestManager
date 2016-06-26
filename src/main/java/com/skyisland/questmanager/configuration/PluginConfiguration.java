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

package com.skyisland.questmanager.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;
import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.utils.Compass;
import com.skyisland.questmanager.player.utils.Recaller;
import com.skyisland.questmanager.player.utils.SpellHolder;
import com.skyisland.questmanager.player.utils.SpellWeavingInvoker;

/**
 * Wrapper class for configuration files needed by the plugin.
 * This does not include configuration files for individual quests.
 *
 */
public class PluginConfiguration {
	
	protected YamlConfiguration config;
	
	protected boolean conservative;
	
	public static enum Category {
		PLUGIN("Plugin"),
		MANAGER("Manager"),
		PLAYER("Player Interfaces"),
		FEATURE("Special Features"),
		OTHER("Other");
		
		private String name;
		
		private Category(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}
	
	public static enum PluginConfigurationKey {
		
		VERSION("version", Category.PLUGIN, null, "The config version number", 1.0),
		CONSERVATIVE("config.conservativeMode", Category.MANAGER, "Conservative Loading", "Whether quest save files with missing keys should be removed or left alone (default)", true),
		VERBOSEMENUS("menus.verboseMenus", Category.PLAYER, "Verbose Menus", "Should menus send messages when already expired? If not, they silently ignore input (default)", false),
		ALLOWCRAFTING("player.allowCrafting", Category.PLAYER, "Allow Crafting", "Can players craft like normal? If false, prevents all crafting (default)", true),
		ALLOWNAMING("player.allowNaming", Category.PLAYER, "Allow Naming", "Can players rename items on an anvil? (default no)", false),
		ALLOWTAMING("player.allowTaming", Category.PLAYER, "Allow Taming", "Can players tame cats and dogs", false),
		PARTYSIZE("player.maxPartySize", Category.PLAYER, "Party Limit", "Maximum players in a party", 4),
		CLEANUPVILLAGERS("world.villagerCleanup", Category.MANAGER, "Villager Removal", "Should NPCs be killed when the plugin is disabled? They will be regenerated when next enabled", false),
		XPMONEY("interface.useXPMoney", Category.PLAYER, "XP Money", "XP can be used as money; When XP is picked up, it'll be used as xp instead, and the player's experience level is used as money indicator", true),
		PORTALS("interface.usePortals", Category.PLAYER, "Use MV Portals", "Should QM use MultiversePortals to set player checkpoints when taking a portal to and from a QuestWorld? REQUIRES MultiversePortals", true),
		ADJUSTXP("interface.adjustXP", Category.PLAYER, "Adjust XP", "QM can change the amount of xp dropped by mobs automatically based on their name. This only works on mobs with names that include their 'lvl' in format [Mob (Lvl 4)]", true),
		TITLECHAT("interface.titleInChat", Category.PLAYER, "Title in Chat", "Should player titles prefix their chat messasge? Works accross worlds", true),
		COMPASS("interface.compass.enabled", Category.PLAYER, "Enable Compass", "The Magic Compass points players to select locatable objectives for quets. If diabled, compasses work like Vanilla", true),
		COMPASSTYPE("interface.compass.type", Category.PLAYER, "Compass Type", "What MATERIAL should be used as the compass. You most certainly want this to be COMPASS, usually", "COMPASS"),
		COMPASSNAME("interface.compass.name", Category.PLAYER, "Compass Name", "What name should compasses have to have to be considered valid magic compasses?", "Magic Compass"),
		ALLOWMAGIC("magic.enabled", Category.FEATURE, "Enable Magic", "Can magic be used? Also enabled magic interfaces", true),
		MANADEFAULT("magic.startingMana", Category.FEATURE, "Starting Mana", "How much mana should new players get?", 20.0),
		DAYREGEN("magic.dayRegen", Category.FEATURE, "Daytime Mana Regen", "How much mana is regenned in the day? If positive, this is the amount. If negative, this is the percent (-50 is 50% of max)", 1.0),
		NIGHTREGEN("magic.nightRegen", Category.FEATURE, "Nighttime Mane Regen", "How much mana is regenned at night. Follows the same rules as Daytime Mana", 1.0),
		OUTSIDEREGEN("magic.outsideRegen", Category.FEATURE, "Outside Regen", "Do players ONLY regen if they can see the sky?", true),
		KILLREGEN("magic.regenOnKill", Category.FEATURE, "Kill Mana Regen", "How much mana does a player get each time they kill something? Follows same rules as Daytime Mana", 1.0),
		XPREGEN("magic.regenOnXP", Category.FEATURE, "XP Mana Regen", "How much mana does a player get when they absorb an XP orb? Same rules as Daytime Mana", 1.0),
		FOODREGEN("magic.regenOnFood", Category.FEATURE, "Food Regen", "Each time a player eats, how much mana do they regen? Same rules as Daytime Mana", -50),
		HOLDERNAME("interface.magic.holderName", Category.PLAYER, "Spellholder Name", "What name should spellholders have to be considered valid spellholders?", "Spell Vessel"),
		ALLOWWEAVING("spellweaving.enabled", Category.FEATURE, "Enable Spellweaving", "Should spellweaving be enabled?", true),
		USEINVOKER("spellweaving.useInvoker", Category.FEATURE, "Use Invoker", "Can players use an item invoker to invoke their spellweaving spells?", true),
		INVOKERNAME("interface.spellweaving.invokerName", Category.PLAYER, "Invoker Name", "What name should an invoker have to be considered valid", "Spell Invoker"),
		INVOKERTYPE("interface.spellweaving.invokerType", Category.PLAYER, "Invoker Type", "What material type is the spell invoker?", "CARROT_STICK"),
		ALLOWRECALL("interface.recall.enabled", Category.PLAYER, "Enable Recall", "Should the mark/recall mechanic be enabled?", true),
		RECALLERTYPE("interface.recall.recallerType", Category.PLAYER, "Recaller Type", "What material is used as the recaller object? If the player can't get the item, they can only recall if you set up a recall spell", "BOOK"),
		RECALLERNAME("interface.recall.recallerName", Category.PLAYER, "Recaller Name", "What name must items have to be considered a recaller?", "Waystone"),
		MARKLOCTYPE("interface.recall.markerType", Category.PLAYER, "Marker Type", "What type of block players click on with a recaller to set their mark? If a recaller isn't obtainable, a player can still mark with a mark spell", "BEACON"),
		MARKONCE("interface.recall.singleRecall", Category.PLAYER, "Single Recall", "Should a player's mark be removed once they recall?", false),
		RECALLCOST("interface.recall.cost", Category.PLAYER, "Recall Cost", "How much mana it costs to recall. Follows same rules as man regen", -100),
		ALTERTYPE("interface.magic.alterBlockType", Category.PLAYER, "Magic Altar Type", "What block type is the magic altar, which is used to load spells into spell holders", "ENCHANTMENT_TABLE"),
		WORLDS("questWorlds", Category.MANAGER, "Quest Worlds", "Which worlds should be treated as quest worlds? Players not in these worlds are overlooked by QM", Lists.newArrayList("World1", "World2")),
		QUESTDIR("questDir", Category.PLUGIN, "Quest Directory", "Where are quest template files stored?", "quests/"),
		SAVEDIR("saveDir", Category.PLUGIN, "Save Directory", "Where are quest save files stored between disables and enables?", "savedata/"),
		REGIONDIR("regionDir", Category.PLUGIN, "Region Directory", "Where are region files stored?", "region/"),
		SPELLDIR("spellDir", Category.PLUGIN, "Spell Directory", "Where are spell files stored", "spells/"),
		SKILLDIR("skillDir", Category.PLUGIN, "Skill Config Directory", "Where can skills look to find and store their config", "skills/"),
		SKILLCAP("skill.cap", Category.FEATURE, "Skill Cap", "Maximum skill level for any skill", 100),
		SKILLSUCCESSGROWTH("skill.growth.success", Category.FEATURE, "Skill Growth - Success", "How much a skill grows when performing an action at the same level, on success", 0.10),
		SKILLFAILGROWTH("skill.growth.fail", Category.FEATURE, "Skill Growth - Failure", "How much a skill grows when performing an action at the same level, on success", 0.025),
		SKILLGROWTHCUTOFF("skill.growth.cutoff", Category.FEATURE, "Skill Cuttoff", "Maximum difference between skill level and action level where a player gets xp", 10),
		SKILLGROWTHUPPERCUTOFF("skill.growth.cutoffUpper", Category.FEATURE, "Skill Upper Cuttoff", "How much higher a skill can be and the player still get xp for a failure", 10),
		SUMMONLIMIT("summonLimit", Category.FEATURE, "Summon Limit", "Maximum summons a player can have", 2),
		MUSICDURATIONS("musicDurations", Category.FEATURE, "Music Durations", "Map between Sounds and their durations", null);
		
		private String key;
		
		protected Object def;
		
		private String name;
		
		private String desc;
		
		private Category category;
		
		PluginConfigurationKey(String key, Category category, String name, String description, Object def) {
			this.key = key;
			this.name = name;
			this.desc = description;
			this.def = def;
			this.category = category;
		}
		
		public Category getCaterogy() {
			return category;
		}
		
		public String getKey() {
			return key;
		}

		public Object getDef() {
			if (this != MUSICDURATIONS)
				return def;
			
			YamlConfiguration yaml = new YamlConfiguration();
			ConfigurationSection mdur = yaml.createSection("aslkfl");
			mdur.set("RECORD_CHIRP", 16.0);
			mdur.set("RECORD_11", 123.45);
			return mdur;
		}

		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return desc;
		}
	}
	
	protected PluginConfiguration() {
		config = null;
		conservative = (Boolean) PluginConfigurationKey.CONSERVATIVE.def;
	}
	
	public PluginConfiguration(File configFile) {
		config = new YamlConfiguration();
		this.conservative = false;
		
		if (!configFile.exists() || configFile.isDirectory()) {
			QuestManagerPlugin.logger.warning(ChatColor.YELLOW + "Unable to find Quest Manager config file!" + ChatColor.RESET);
			config = createDefaultConfig(configFile);
		} else 	try {
			config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		proofConfig();

		if (config.getBoolean(PluginConfigurationKey.CONSERVATIVE.key, true)) {
			this.conservative = true;
			QuestManagerPlugin.logger.info("Conservative mode is on,"
					+ " so invalid configs will simply be ignored instead of destroyed.");
		}
		
		if (getCompassEnabled()) {
			Compass.CompassDefinition.setCompassType(getCompassType());
			Compass.CompassDefinition.setDisplayName(getCompassName());
		}
		
		if (getUseWeavingInvoker()) {
			SpellWeavingInvoker.InvokerDefinition.setDisplayName(getSpellInvokerName());
			SpellWeavingInvoker.InvokerDefinition.setInvokerType(getInvokerType());
		}
		
		Recaller.RecallerDefinition.setDisplayName(getRecallerName());
		Recaller.RecallerDefinition.setType(getRecallerType());
		Recaller.MarkerDefinition.setType(getMarkType());
		
		SpellHolder.SpellHolderDefinition.setDisplayName(getSpellHolderName());
		SpellHolder.SpellAlterTableDefinition.setBlockType(getAlterType());
	}
	
	public boolean getConservative() {
		return this.conservative;
	}
	
	private void proofConfig() {
		for (PluginConfigurationKey key : PluginConfigurationKey.values())
		if (!config.contains(key.getKey()))
			config.set(key.getKey(), key.getDef());
	}
	
	/**
	 * Returns the VERSION number of the current configuration file.
	 * This is simply the reported VERSION number in the configuration file.
	 */
	public double getVersion() {
		return config.getDouble(PluginConfigurationKey.VERSION.key, 0.0);
	}
	
	public List<String> getWorlds() {
		return config.getStringList(PluginConfigurationKey.WORLDS.key);
	}
	
	/**
	 * Gets the stored quest path information -- where the quest configuration files are stored
	 */
	public String getQuestPath() {
		return config.getString(PluginConfigurationKey.QUESTDIR.key);
	}
	
	/**
	 * Indicates whether or not the config indicates invalid configuration files, states, or
	 * active logs should be kept or removed.
	 */
	public boolean getKeepOnError() {
		return config.getBoolean(PluginConfigurationKey.CONSERVATIVE.key, true);
	}
	
	/**
	 * Should the plugin remove ALL villager in quest worlds before populating it with quest related NPCs?
	 * This is useful to avoid stray villagers escape on error of the plugin, but removed the possibility to use villagers
	 * that aren't managed by QuestManager in registered QuestWorlds!
	 */
	public boolean getVillagerCleanup() {
		return config.getBoolean(PluginConfigurationKey.CLEANUPVILLAGERS.key);
	}
	
	/**
	 * Should xp gained in the quest world count as 'money'?
	 * When this is enabled, all XP received is instead converted to 'money'. This is represented to the player's client
	 * as the level of the player.
	 */
	public boolean getXPMoney() {
		return config.getBoolean(PluginConfigurationKey.XPMONEY.key);
	}
	
	/**
	 * Returns the largest size a party can get
	 */
	public int getMaxPartySize() {
		return config.getInt(PluginConfigurationKey.PARTYSIZE.key);
	}
	
	/**
	 * Returns how many summons a player is allowed to have
	 */
	public int getSummonLimit() {
		return config.getInt(PluginConfigurationKey.SUMMONLIMIT.key, 2);
	}
	
	/**
	 * Can players tame animals in the QuestWorlds?
	 */
	public boolean getAllowTaming() {
		return config.getBoolean(PluginConfigurationKey.ALLOWTAMING.key);
	}
	
	/**
	 * Whether or not multiverse portals should be used and tracked.
	 * When this is on, players will be returned to the last portal they used when leaving registered QuestWorlds.
	 */
	public boolean getUsePortals() {
		return config.getBoolean(PluginConfigurationKey.PORTALS.key);
	}
	
	/**
	 * Returns whether or not the number of XP mobs drop should depend on their level
	 * Note: Currently, this requires that the name of the mob have "Lvl ###" in it! TODO
	 */
	public boolean getAdjustXP() {
		return config.getBoolean(PluginConfigurationKey.ADJUSTXP.key);
	}
	
	/**
	 * Returns whether or not magic is set to be enabled
	 */
	public boolean getMagicEnabled() {
		return config.getBoolean(PluginConfigurationKey.ALLOWMAGIC.key);
	}
	
	/**
	 * Gets the specified default mana allotment, for new players
	 */
	public int getStartingMana() {
		return config.getInt(PluginConfigurationKey.MANADEFAULT.key);
	}
	
	/**
	 * Returns the amount of mp to regen in the day.
	 * Negative amounts indicate a percentage to regen rather than a constant (-(return)%)
	 * @return The amount to regen; positive values indicate a constant, negative a rate (out of 100)
	 */
	public double getMagicRegenDay() {
		return config.getDouble(PluginConfigurationKey.DAYREGEN.key);
	}
	
	/**
	 * Amount to regen at night
	 * @return The amount to regen; positive values indicate a constant, negative a rate (out of 100)
	 */
	public double getMagicRegenNight() {
		return config.getDouble(PluginConfigurationKey.NIGHTREGEN.key);
	}
	
	/**
	 * @return whether or not mp should regen only when outside
	 */
	public boolean getMagicRegenOutside() {
		return config.getBoolean(PluginConfigurationKey.OUTSIDEREGEN.key);
	}
	
	/**
	 * The amount of mp to regen when a player gets a kill
	 * @return The amount to regen; positive values indicate a contant, negative a rate (out of 100)
	 */
	public double getMagicRegenKill() {
		return config.getDouble(PluginConfigurationKey.KILLREGEN.key);
	}
	
	/**
	 * The amount to regen per xp picked up by a player
	 * @return The amount to regen; positive values indicate a constant, negative a rate (out of 100)
	 */
	public double getMagicRegenXP() {
		return config.getDouble(PluginConfigurationKey.XPREGEN.key);
	}
	
	/**
	 * Amount to regen when a player consumes a food item
	 * @return The amount to regen; positive values indicate a constant, negative a rate (out of 100)
	 */
	public double getMagicRegenFood() {
		return config.getDouble(PluginConfigurationKey.FOODREGEN.key);
	}
	
	/**
	 * Indicates whether or not menus should print out extra messages about expired menus.
	 * This can be used as a security feature to avoid players from spamming old menus!
	 */
	public boolean getMenuVerbose() {
		return config.getBoolean(PluginConfigurationKey.VERBOSEMENUS.key);
	}
	
	public boolean getAllowCrafting() {
		return config.getBoolean(PluginConfigurationKey.ALLOWCRAFTING.key);
	}
	
	/**
	 * Whether or not renaming of items, entities is allowed through anvils
	 */
	public boolean getAllowNaming() {
		return config.getBoolean(PluginConfigurationKey.ALLOWNAMING.key);
	}
	
	/**
	 * Returns whether or not titles should be put into chat in all worlds
	 */
	public boolean getChatTitle() {
		return config.getBoolean(PluginConfigurationKey.TITLECHAT.key);
	}
	
	/**
	 * Returns whether or not compasses are enabled
	 */
	public boolean getCompassEnabled() {
		return config.getBoolean(PluginConfigurationKey.COMPASS.key, true);
	}
	
	/**
	 * Gets the configuration's defined material for the compass object
	 */
	public Material getCompassType() {
		try {
			return Material.valueOf(config.getString(PluginConfigurationKey.COMPASSTYPE.key, "COMPASS"));
		} catch (IllegalArgumentException e) {
			QuestManagerPlugin.logger.warning("Unable to find the compass material: " 
		+ config.getString(PluginConfigurationKey.COMPASSTYPE.key, "COMPASS"));
			return Material.COMPASS;
		}
		
	}
	
	/**
	 * Returns the name of the compass object
	 */
	public String getCompassName() {
		return config.getString(PluginConfigurationKey.COMPASSNAME.key, "Magic Compass");
	}
	
	/**
	 * Gets the stored save data path information
	 */
	public String getSavePath() {
		return config.getString(PluginConfigurationKey.SAVEDIR.key);
	}
	
	/**
	 * Returns the path to where region data including enemy spawn data is kept
	 */
	public String getRegionPath() {
		return config.getString(PluginConfigurationKey.REGIONDIR.key);
	}
	
	/**
	 * Returns the path to where spell configuration
	 */
	public String getSpellPath() {
		return config.getString(PluginConfigurationKey.SPELLDIR.key);
	}
	
	public String getSkillPath() {
		return config.getString(PluginConfigurationKey.SKILLDIR.key);
	}
	
	/**
	 * Returns the plugin-wide skill cap. This is the maximum level any skill can achieve.
	 * Defaults to <i>100</i> if it is absent from the config.
	 */
	public int getSkillCap() {
		return config.getInt(PluginConfigurationKey.SKILLCAP.key, 100);
	}
	
	public double getSkillGrowthOnSuccess() {
		return config.getDouble(PluginConfigurationKey.SKILLSUCCESSGROWTH.key, 0.20);
	}
	
	public double getSkillGrowthOnFail() {
		return config.getDouble(PluginConfigurationKey.SKILLFAILGROWTH.key, 0.05);
	}
	
	public int getSkillCutoff() {
		return config.getInt(PluginConfigurationKey.SKILLGROWTHCUTOFF.key, 20);
	}
	
	/**
	 * Returns the limit of how might higher a spell difficulty level can be than a players where the player
	 * will still get xp on failure.
	 */
	public int getSkillUpperCutoff() {
		return config.getInt(PluginConfigurationKey.SKILLGROWTHUPPERCUTOFF.key, 20);
	}
	
	/**
	 * Gets the name of the spell holders
	 */
	public String getSpellHolderName() {
		return config.getString(PluginConfigurationKey.HOLDERNAME.key);
	}
	
	/**
	 * Gets the material block type used for spell holder alteration
	 */
	public Material getAlterType() {
		return Material.valueOf(config.getString(PluginConfigurationKey.ALTERTYPE.key));
	}
	
	/**
	 * Gets whether spell weaving is enabled on this server
	 */
	public boolean getAllowSpellWeaving() {
		return config.getBoolean(PluginConfigurationKey.ALLOWWEAVING.key, true);
	}
	
	/**
	 * Gets whther or not to use the spell invoker
	 */
	public boolean getUseWeavingInvoker() {
		return config.getBoolean(PluginConfigurationKey.USEINVOKER.key, true);
	}
	
	/**
	 * This manager's custom invoker name
	 */
	public String getSpellInvokerName() {
		return config.getString(PluginConfigurationKey.INVOKERNAME.key);
	}
	
	/**
	 * The material used to stand for the spell weaving invoker
	 */
	public Material getInvokerType() {
		return Material.valueOf(config.getString(PluginConfigurationKey.INVOKERTYPE.key));
	}
	
	/**
	 * The material used to stand for the mark/recall item
	 */
	public Material getRecallerType() {
		return Material.valueOf(config.getString(PluginConfigurationKey.RECALLERTYPE.key));
	}
	
	/**
	 * The block material used to set a mark point
	 * @return
	 */
	public Material getMarkType() {
		return Material.valueOf(config.getString(PluginConfigurationKey.MARKLOCTYPE.key));
	}
	
	/**
	 * Should a mark'ed location be removed once the player recalls to it?
	 * @return
	 */
	public boolean singleRecall() {
		return config.getBoolean(PluginConfigurationKey.MARKONCE.key);
	}
	
	/**
	 * How much does it cost to recall? Same rules as the mana regen options (50 is 50 mana, -50 is 50% of max)
	 * @return
	 */
	public double getRecallCost() {
		return config.getDouble(PluginConfigurationKey.RECALLCOST.key);
	}
	
	/**
	 * What name do recallers need to have?
	 * @return
	 */
	public String getRecallerName() {
		return config.getString(PluginConfigurationKey.RECALLERNAME.getKey());
	}

	public Map<Sound, Double> getMusicDurations() {
		Map<Sound, Double> map = new HashMap<>();
		Map<String, Object> configMap = config.getConfigurationSection(PluginConfigurationKey.MUSICDURATIONS.key)
				.getValues(false);
//		if (!config.contains(PluginConfigurationKey.MUSICDURATIONS.key)) {
//			configMap = (Map<S, Double>) PluginConfigurationKey.MUSICDURATIONS.getDef();
//		}
		
		//for (String key : config.getConfigurationSection(PluginConfigurationKey.MUSICDURATIONS.key).getKeys(false)) {
		for (String key : configMap.keySet()) {
			try {
				map.put(Sound.valueOf(key), (Double) configMap.get(key));
			} catch (Exception e) {
				QuestManagerPlugin.logger.warning("Unable to determine sound from " + key);
			}
		}
		return map;
	}
	
	public Object getBaseValue(PluginConfigurationKey key) {
		return config.get(key.key, key.def);
	}
	
	public static PluginConfiguration generateDefault() {
		PluginConfiguration config = new PluginConfiguration();
		YamlConfiguration yaml = new YamlConfiguration();
		for (PluginConfigurationKey key : PluginConfigurationKey.values()) {
			yaml.set(key.key, key.getDef());
		}
		config.config = yaml;
		return config;
	}
	
	/**
	 * Sets up a default configuration file with blank values
	 */
	private YamlConfiguration createDefaultConfig(File configFile) {
		if (configFile.isDirectory()) {
			QuestManagerPlugin.logger.warning(ChatColor.RED + 
					"Unable to create default config!" + ChatColor.RESET);
			return null;
		}
		
		YamlConfiguration config = new YamlConfiguration();
		
		config.set(PluginConfigurationKey.VERSION.key, QuestManagerPlugin.VERSION);
		
		//config options
		config.set(PluginConfigurationKey.CONSERVATIVE.key, true);
		
		//menu options
		config.set(PluginConfigurationKey.VERBOSEMENUS.key, false);
		
		//player options
		config.set(PluginConfigurationKey.ALLOWCRAFTING.key, false);
		config.set(PluginConfigurationKey.ALLOWNAMING.key, false);
		config.set(PluginConfigurationKey.PARTYSIZE.key, 4);
		config.set(PluginConfigurationKey.ALLOWTAMING.key, false);
		
		//world options
		config.set(PluginConfigurationKey.CLEANUPVILLAGERS.key, false);
		
		//interface options
		config.set(PluginConfigurationKey.XPMONEY.key, true);
		config.set(PluginConfigurationKey.PORTALS.key, true);
		config.set(PluginConfigurationKey.ADJUSTXP.key, true);
		config.set(PluginConfigurationKey.TITLECHAT.key, true);
		
		config.set(PluginConfigurationKey.COMPASS.key, true);
		config.set(PluginConfigurationKey.COMPASSNAME.key, "Magic Compass");
		config.set(PluginConfigurationKey.COMPASSTYPE.key, "COMPASS");
		
		config.set(PluginConfigurationKey.HOLDERNAME.key, "Magic Scroll");
		config.set(PluginConfigurationKey.ALTERTYPE.key, "ENCHANTING_TABLE");
		
		//magic options
		config.set(PluginConfigurationKey.ALLOWMAGIC.key, true);
		config.set(PluginConfigurationKey.MANADEFAULT.key, 20);
		config.set(PluginConfigurationKey.DAYREGEN.key, 1.0);
		config.set(PluginConfigurationKey.NIGHTREGEN.key, 1.0);
		config.set(PluginConfigurationKey.OUTSIDEREGEN.key, false);
		config.set(PluginConfigurationKey.KILLREGEN.key, 0.0);
		config.set(PluginConfigurationKey.XPREGEN.key, 0.0);
		config.set(PluginConfigurationKey.FOODREGEN.key, 0.0);
		
		List<String> worlds = new ArrayList<>();
		worlds.add("QuestWorld");
		worlds.add("TutorialWorld");
		config.set(PluginConfigurationKey.WORLDS.key, worlds);
		
		config.set(PluginConfigurationKey.QUESTDIR.key, "quests/");
		config.set(PluginConfigurationKey.SAVEDIR.key, "savedata/");
		config.set(PluginConfigurationKey.REGIONDIR.key, "regions/");
		config.set(PluginConfigurationKey.SPELLDIR.key, "spells/");
		config.set(PluginConfigurationKey.SKILLDIR.key, "skills/");
		
		config.set(PluginConfigurationKey.SKILLCAP.key, 100);
		config.set(PluginConfigurationKey.SKILLSUCCESSGROWTH.key, 0.20);
		config.set(PluginConfigurationKey.SKILLFAILGROWTH.key, 0.05);
		config.set(PluginConfigurationKey.SKILLGROWTHCUTOFF.key, 20);
		config.set(PluginConfigurationKey.SKILLGROWTHUPPERCUTOFF.key, 10);
				
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return config;
	}
}
