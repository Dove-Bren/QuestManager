package com.skyisland.questmanager.player.skill.defaults;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.LocationState;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.effects.ChargeEffect;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.QualityItem;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.player.skill.event.TrapSetEvent;
import com.skyisland.questmanager.region.Region;
import com.skyisland.questmanager.region.RegionManager;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;

public class TrappingSkill extends Skill implements Listener {
	
	public static final String CONFIG_NAME = "Trapping.yml";
	
	private static final String BAD_TRAP_LVL = ChatColor.RED + "You aren't skilled enough to use this type of trap!"
			+ " It requires level %d Trapping skill";
	
	private static final String BAD_OWNER_MESSAGE = ChatColor.DARK_GRAY + "This is not your trap";
	
	private static final String BAD_RANGE_MESSAGE = ChatColor.RED + "There is no game nearby in your level range";
	
	private static final String BAD_TIME_MESSAGE = ChatColor.DARK_GRAY + "The trap hasn't caught anything yet";
	
	private static final String BAD_TRAP_COUNT = ChatColor.RED + "You cannot place any more traps!";
	
	private static final String BAD_BASE_TYPE = ChatColor.DARK_GRAY + "You cannot set a trap here!";
	
	private static final String WIN_MESSAGE = ChatColor.GREEN + "Your prey was fruitful. You got ";
	
	private static final Sound PLACE_SOUND = Sound.BLOCK_IRON_TRAPDOOR_OPEN;
	
	private static final Sound COLLECT_SOUND = Sound.BLOCK_IRON_DOOR_OPEN;
	
	private static final Effect COLLECT_EFFECT = Effect.HAPPY_VILLAGER;
	
	/**
	 * Set of materials traps can be placed on
	 */
	private static final Set<Material> validMaterials = Sets.immutableEnumSet(
			Material.GRASS,
			Material.DIRT
			);
	
	public Type getType() {
		return Skill.Type.TRADE;
	}
	
	public String getName() {
		return "Trapping"; 
	}
	
	public String getDescription(QuestPlayer player) {
		String ret = ChatColor.WHITE + "Trappers lay snares and capture animals for food and crafts";
		int level = player.getSkillLevel(this);
		double bonus = (double) level * qualityRate;
		int trapCount = (int) (1 + (level * trapRate));
		ret += "\n\n" + ChatColor.GREEN + "Traps: " + trapCount;
		ret += "\n" + ChatColor.GOLD + "Bonus Quality: +" + String.format("%.2f", bonus) + "%"; 
		
		return ret;
	}

	@Override
	public int getStartingLevel() {
		return startingLevel;
	}
	
	@Override
	public String getConfigKey() {
		return "Trapping";
	}
	
	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.TRIPWIRE_HOOK);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof TrappingSkill);
	}
	
	private static class GameRecord {
		
		private int difficulty;
		
		private double trapTime;
		
		private double trapDeviation;
		
		private TrapType trap;
		
		private ItemStack result;
		
		private Region region;
		
		public GameRecord(Region region, int difficulty, double trapTime, double trapDeviation, TrapType trap, ItemStack result) {
			this.trapTime = trapTime;
			this.trapDeviation = trapDeviation;
			this.trap = trap;
			this.result = result;
			this.difficulty = difficulty;
		}
	}
	
	private static class TrapType {
		
		private String name;
		
		private int skillRequirement;
		
		private ItemStack icon;
		
		public TrapType(String name, int skillRequirement, ItemStack icon) {
			this.name = name;
			this.skillRequirement = skillRequirement;
			this.icon = icon;
		}
		
		/**
		 * Checks and returns whether the given item counts as this trap type
		 * @param trap
		 * @return
		 */
		public boolean matches(ItemStack trap) {
			if (trap == null || trap.getType() == Material.AIR)
				return false;
			if (trap.getType() != icon.getType())
				return false;
			if (trap.getDurability() != icon.getDurability())
				return false;
			String trapName, iconName;
			trapName = iconName = null;
			if (icon.hasItemMeta() && icon.getItemMeta().hasDisplayName())
				iconName = icon.getItemMeta().getDisplayName();
			if (trap.hasItemMeta() && trap.getItemMeta().hasDisplayName())
				trapName = trap.getItemMeta().getDisplayName();
			
//			if (!trap.hasItemMeta() && !icon.hasItemMeta())
//				return true;
//			if (!trap.hasItemMeta() || !icon.hasItemMeta())
//				return false; //since they _both_ don't have on meta, if a single one does then false
//			if (!trap.getItemMeta().hasDisplayName() && !icon.getItemMeta().hasDisplayName())
//				return true;
//			if (!trap.getItemMeta().hasDisplayName() || !icon.getItemMeta().hasDisplayName())
//				return false;
			if (iconName == null && trapName == null)
				return true;
			if (iconName == null || trapName == null)
				return false;
			
			return trapName.equals(iconName);
		}
	}
	
	private static int trapIDBase = 0;
	
	/**
	 * An active trap with a time
	 * @author Skyler
	 *
	 */
	private class Trap implements Alarmable<Integer> {
		
		private QualityItem reward;
		
		private OfflinePlayer oplayer;
		
		private int difficulty;
		
		private Block block;
		
		private Material memory;
		
		private boolean isDone;
		
		private int id;
		
		public Trap(OfflinePlayer player, int difficulty, QualityItem reward, Block trapBlock, double time) {
			this.id = trapIDBase++;
			this.difficulty = difficulty;
			this.reward = reward;
			this.oplayer = player;
			this.block = trapBlock;
			isDone = false;
			Alarm.getScheduler().schedule(this, 0, Math.max(1.0, time));
			
			memory = block.getType();
			block.setType(trapMaterial);
		}
		
		@Override
		public void alarm(Integer o) {
			isDone = true;
			trapCatch(this);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Trap)
				return ((Trap) obj).id == id;
			
			return false;
		}
		
		@Override
		public int hashCode() {
			return 1512 + (id * 17);
		}
		
		public void remove() {
			block.setType(memory);
		}
	}
	
	private int startingLevel;
	
	private double qualityRate;
	
	private double trapRate;
	
	private Map<UUID, List<Trap>> activeTraps;
	
	private List<GameRecord> records;
	
	private List<TrapType> trapTypes;
	
	private Material trapMaterial;
	
	private String alertMessage;
	
	private int maxLevelDifference;
	
	public TrappingSkill() {
		File configFile = new File(QuestManagerPlugin.questManagerPlugin.getDataFolder(),
				QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillPath() + CONFIG_NAME);
		YamlConfiguration config = createConfig(configFile);
		
		if (!config.getBoolean("enabled", true)) {
			return;
		}
		
		this.activeTraps = new HashMap<>();
		this.startingLevel = config.getInt("startingLevel", 0);
		this.qualityRate = config.getDouble("bonusQualityRate", 0.002);
		this.maxLevelDifference = config.getInt("maxLevelDifference", 10);
		this.trapRate = config.getDouble("bonusTrapRate", 0.05);
		this.trapMaterial = Material.matchMaterial(config.getString("trapMaterial", Material.IRON_TRAPDOOR.name()));
		this.alertMessage = config.getString("alertMessage", null);
		if (this.alertMessage != null && this.alertMessage.trim().isEmpty())
			this.alertMessage = null;
		
		if (trapMaterial == null)
			trapMaterial = Material.IRON_TRAPDOOR;
		
		this.trapTypes = new LinkedList<>();
		if (!config.contains("traps")) {
			QuestManagerPlugin.logger.warning("No traps are defined, making the Trapping skill worthless!");
			return;
		}
		/*
		 * traps:
		 *   noobtrap:
		 *     difficulty: 0
		 *     icon: ==:ItemStack
		 *   bosstrap: ...
		 */
		for (String key : config.getConfigurationSection("traps").getKeys(false)) {
			try {
				trapTypes.add(new TrapType(key,
						config.getInt("traps." + key + ".difficulty"),
						config.getItemStack("traps." + key + ".icon")));
			} catch (Exception e) {
				QuestManagerPlugin.logger.warning("Skipping trap type " + key);
			}
		}
		
		this.records = new LinkedList<>();
		if (!config.contains("game")) {
			QuestManagerPlugin.logger.warning("No game are defined, making the Trapping skill worthless!");
			return;
		}
		/*
		 * game:
		 *   bear:
		 *     difficulty: 0
		 *     trapTime: 30.0
		 *     trapDeviation: 10.0
		 *     trapType: noobtrap
		 *     region: [Location]
		 *     result: ==:ItemStack
		 */
		RegionManager rManager = QuestManagerPlugin.questManagerPlugin.getEnemyManager();
		for (String key : config.getConfigurationSection("game").getKeys(false)) {
			ConfigurationSection subsex = config.getConfigurationSection("game." + key);
			try {
				TrapType type = lookupTrap(subsex.getString("trapType"));
				if (type == null) {
					QuestManagerPlugin.logger.warning("Unable to find trap type for name " + subsex.getString("trapType"));
					continue;
				}
				Region region = rManager.getRegion(subsex.contains("region") ? 
						(subsex.get("region") == null ? null : ((LocationState) subsex.get("region")).getLocation())
						: null);
				records.add(new GameRecord(
						region, subsex.getInt("difficulty"),
						subsex.getDouble("trapTime"), subsex.getDouble("trapDeviation"),
						type, subsex.getItemStack("result")
						));
			} catch (Exception e) {
				QuestManagerPlugin.logger.warning("Skipping game " + key);
			}
		}
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	private YamlConfiguration createConfig(File configFile) {
		if (!configFile.exists()) {
			
			YamlWriter writer = new YamlWriter();
			
			writer.addLine("enabled", true, Lists.newArrayList("Whether or not this skill is allowed to be used.", "true | false"))
				.addLine("startingLevel", 0, Lists.newArrayList("The level given to players who don't have this skill yet", "[int]"))
				.addLine("bonusQualityRate", 0.002, Lists.newArrayList("Bonus quality given per skill level", "[double], 0.01 is 1%"))
				.addLine("maxLevelDifference", 10, Lists.newArrayList("Maximum difference between a game level and", "player level where a player can still", "get the game", "[int]"))
				.addLine("bonusTrapRate", 0.05, Lists.newArrayList("How much of an extra trap the player gets a level. This is", "rounded down when calculating the maximum", "number of traps a player can have", "[double] bonus traps per level"))
				.addLine("trapMaterial", Material.IRON_TRAPDOOR.name(), Lists.newArrayList("What should a trap set in the world look like?", "[Material] Defaults to an Iron Trapdoor"))
				.addLine("alertMessage", null, Lists.newArrayList("When a trap is ready to be collected, what (if", "any) message should players get? If empty or null", "no message is sent"));
			
			Map<String, Map<String, Object>> map;
			Map<String, Object> submap;
			ItemStack item;
			ItemMeta meta;
			
			//trapTypes
			map = new HashMap<>();
			submap = new HashMap<>();
			submap.put("difficulty", 0);
			item = new ItemStack(Material.TRIPWIRE_HOOK);
			meta = item.getItemMeta();
			meta.setDisplayName("Small Trap");
			item.setItemMeta(meta);
			submap.put("icon", item);
			map.put("SmallTrap", submap);

			submap = new HashMap<>();
			submap.put("difficulty", 20);
			item = new ItemStack(Material.TRIPWIRE_HOOK);
			meta = item.getItemMeta();
			meta.setDisplayName("Strong Trap");
			item.setItemMeta(meta);
			submap.put("icon", item);
			map.put("StrongTrap", submap);
			
			writer.addLine("traps", map, Lists.newArrayList("A list of trap types with their difficulty and item representations"));
			
			//game
			map = new HashMap<>();
			submap = new HashMap<>();
			submap.put("difficulty", 0);
			submap.put("trapTime", 180.0);
			submap.put("trapDeviation", 30.0);
			submap.put("trapType", "SmallTrap");
			submap.put("region", null);
			item = new ItemStack(Material.RABBIT);
			meta = item.getItemMeta();
			meta.setDisplayName("Raw Rabbit");
			item.setItemMeta(meta);
			submap.put("result", item);
			map.put("rabbit", submap);

			submap = new HashMap<>();
			submap.put("difficulty", 10);
			submap.put("trapTime", 200.0);
			submap.put("trapDeviation", 50.0);
			submap.put("trapType", "SmallTrap");
			submap.put("region", new Location(Bukkit.getWorld("QuestWorld"), -486, 54, -506));
			item = new ItemStack(Material.RAW_CHICKEN);
			meta = item.getItemMeta();
			meta.setDisplayName("Raw Chicken");
			item.setItemMeta(meta);
			submap.put("result", item);
			map.put("chicken", submap);
			
			writer.addLine("game", map, Lists.newArrayList("List of possible game", "If region is left out or is null, the game", "can be caught anywhere instead of", "a specific region"));
			
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
	public void onSnarePlace(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if (e.getClickedBlock() == null || e.getClickedBlock().getType() == Material.AIR)
			return;
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds().contains(
				e.getClickedBlock().getWorld().getName()))
			return;
		if (e.getClickedBlock().getType() == trapMaterial) {
			onSnareCollect(e);
			return;
		}
		
		if (e.getHand() != EquipmentSlot.HAND)
			return; //only do once
		
		ItemStack inHand, offHand;
		inHand = e.getPlayer().getInventory().getItemInMainHand();
		offHand = e.getPlayer().getInventory().getItemInOffHand();
		//if (!inHand.isSimilar(e.getPlayer().getInventory().getItemInOffHand()))
		if (inHand == null || offHand == null ||
				inHand.getType() != e.getPlayer().getInventory().getItemInOffHand().getType()
				|| inHand.getDurability() != e.getPlayer().getInventory().getItemInOffHand().getDurability())
			return;
		
		String mainName, offName;
		mainName = offName = null;
		if (inHand.hasItemMeta() && inHand.getItemMeta().hasDisplayName())
			mainName = inHand.getItemMeta().getDisplayName();
		if (offHand.hasItemMeta() && offHand.getItemMeta().hasDisplayName())
			offName = offHand.getItemMeta().getDisplayName();
		
		if ((mainName == null) ^ (offName == null))
			return;
		if (!mainName.equals(offName))
			return;
		
		TrapType trapType = null;
		for (TrapType type : trapTypes) {
			if (type.matches(inHand)) {
				trapType = type;
				break;
			}
		}
		if (trapType == null)
			return;
		e.setCancelled(true);
		
		if (!validMaterials.contains(e.getClickedBlock().getType())) {
			e.getPlayer().sendMessage(BAD_BASE_TYPE);
			return;
		}
		
		Block trapBlock = e.getClickedBlock().getRelative(BlockFace.UP);
		if (trapBlock.getType() != Material.AIR)
			return;
		
		Player player = e.getPlayer();
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		int lvl = qp.getSkillLevel(this);
		
		int maxTraps = 1 + (int) (lvl * trapRate);
		if (activeTraps.containsKey(player.getUniqueId()) && activeTraps.get(player.getUniqueId()).size() >= maxTraps) {
			player.sendMessage(BAD_TRAP_COUNT);
			return;
		}
		
		if (lvl < trapType.skillRequirement) {
			player.sendMessage(String.format(BAD_TRAP_LVL, trapType.skillRequirement));
			return;
		}
		
		GameRecord record = getRecord(trapBlock.getLocation(), trapType, lvl);
		if (record == null) {
			player.sendMessage(BAD_RANGE_MESSAGE);
			return;
		}
		
		TrapSetEvent event = new TrapSetEvent(qp, new QualityItem(record.result), record.difficulty);
		Bukkit.getPluginManager().callEvent(event);
		
		////// Our modifiers
		
		event.setQualityModifier(event.getQualityModifier() + (qualityRate * (double) lvl));
		
		/////
		
		if (event.isCancelled()) {
			return;
		}
		
		QualityItem result = event.getResult();
		result.setQuality(event.getQualityModifier() * result.getQuality());
		result.getUnderlyingItem().setAmount((int) Math.round(event.getAmountModifier() * result.getUnderlyingItem().getAmount()));
		double time = record.trapTime + (Skill.RANDOM.nextGaussian() * record.trapDeviation);
		time *= event.getTimingModifier();
		
		Trap trap = new Trap(player, record.difficulty, result, trapBlock, time);
		if (!activeTraps.containsKey(player.getUniqueId()))
			activeTraps.put(player.getUniqueId(), new LinkedList<>());
		
		activeTraps.get(player.getUniqueId()).add(trap);
		player.getWorld().playSound(trapBlock.getLocation(), PLACE_SOUND, 1, 1);
		ItemStack main, off;
		main = player.getInventory().getItemInMainHand();
		if (main.getAmount() > 1)
			main.setAmount(main.getAmount() - 1);
		else
			main = null;
		off = player.getInventory().getItemInOffHand();
		if (off.getAmount() > 1)
			off.setAmount(off.getAmount() - 1);
		else
			off = null;
		player.getInventory().setItemInMainHand(main);
		player.getInventory().setItemInOffHand(off);
		
	}
	
	private void onSnareCollect(PlayerInteractEvent e) {
		if (!activeTraps.containsKey(e.getPlayer().getUniqueId())) {
			e.getPlayer().sendMessage(BAD_OWNER_MESSAGE);
			return; //no set traps
		}
		
		Player player = e.getPlayer();
		List<Trap> traps = activeTraps.get(e.getPlayer().getUniqueId());
		if (traps.isEmpty()) {
			player.sendMessage(BAD_OWNER_MESSAGE);
			return; //no set traps
		}
		Block clicked = e.getClickedBlock();
		Trap validTrap = null;
		for (Trap trap : traps) {
			if (trap.block.getX() == clicked.getX()
					&& trap.block.getY() == clicked.getY()
					&& trap.block.getZ() == clicked.getZ()) {
				validTrap = trap;
				break;
			}
		}
		
		if (validTrap == null) {
			//not the player's
			e.getPlayer().sendMessage(BAD_OWNER_MESSAGE);
			return;
		}
		
		if (!validTrap.isDone) {
			e.getPlayer().sendMessage(BAD_TIME_MESSAGE);
			return;
		}
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		this.perform(qp, validTrap.difficulty);
		player.getWorld().playSound(player.getLocation(), COLLECT_SOUND, 1, 1);
		ChargeEffect ef = new ChargeEffect(COLLECT_EFFECT);
		ef.play(player, player.getLocation());
		
		QualityItem result = validTrap.reward;
		ItemStack formattedResult = result.getItem();
		
		String name;
		if (formattedResult.getItemMeta() == null || formattedResult.getItemMeta().getDisplayName() == null) {
			name = YamlWriter.toStandardFormat(formattedResult.getType().toString());
		} else {
			name = formattedResult.getItemMeta().getDisplayName();
		}
		
		FancyMessage msg = new FancyMessage(WIN_MESSAGE)
				.color(ChatColor.GREEN)
			.then(formattedResult.getAmount() > 1 ? formattedResult.getAmount() + "x " : "a ")
			.then("[" + name + "]")
				.color(ChatColor.DARK_PURPLE)
				.itemTooltip(formattedResult);
		
		
		msg.send(player);
		if (!(player.getInventory().addItem(formattedResult)).isEmpty()) {
			player.sendMessage(ChatColor.RED + "There is no space left in your inventory");
			player.getWorld().dropItem(player.getEyeLocation(), result.getItem());
		}
		traps.remove(validTrap);
		validTrap.remove();
		
	}
	
	/**
	 * Returns a random game record that the player qualifies for, according
	 * to the given trap type and the player's level (in relation to maxLevelDifference)
	 * @param trapType
	 * @param level
	 * @return
	 */
	private GameRecord getRecord(Location trapLocation, TrapType trapType, int level) {
		Collections.shuffle(records);
		for (GameRecord record : records) {
			if (record.trap.name.equals(trapType.name))
			if (Math.abs(record.difficulty - level) <= maxLevelDifference)
			if (record.region == null || record.region.isIn(trapLocation)) {
				return record;
			}
		}
		
		return null;
	}
	
	protected void trapCatch(Trap trap) {
		if (alertMessage != null)
		if (trap.oplayer.isOnline()) {
			trap.oplayer.getPlayer().sendMessage(alertMessage);
		}
	}
	
	public void removeTraps() {
		for (Entry<UUID, List<Trap>> entry : activeTraps.entrySet())
		for (Trap trap : entry.getValue()) {
			trap.remove();
		}
	}
	
	private TrapType lookupTrap(String name) {
		if (trapTypes.isEmpty())
			return null;
		
		for (TrapType type : trapTypes) {
			if (type.name.equals(name))
				return type;
		}
		return null;
	}
}
