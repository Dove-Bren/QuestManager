package com.skyisland.questmanager.ui.menu.inventory.minigames;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.effects.ChargeEffect;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.QualityItem;
import com.skyisland.questmanager.player.skill.defaults.SmithingSkill;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;
import com.skyisland.questmanager.ui.menu.inventory.CloseableGui;
import com.skyisland.questmanager.ui.menu.inventory.GuiInventory;
import com.skyisland.questmanager.ui.menu.inventory.InventoryItem;

public class SmeltingGui extends GuiInventory implements Alarmable<Integer>, CloseableGui {
	
	private enum State {
		STOPPED,
		SOLID,
		SMELTING,
		LIQUID,
		ENDED;
	}
	
	public static final Random random = new Random();
	
	public static final ItemStack defaultHotIcon = new ItemStack(Material.FIREBALL);
	
	public static final ItemStack smeltButtonIcon = new ItemStack(Material.IRON_FENCE);
	
	public static final ItemStack slagIcon = new ItemStack(Material.COBBLESTONE);
	
	{
		ItemMeta meta;
		
		meta = defaultHotIcon.getItemMeta();
		meta.setDisplayName("???");
		defaultHotIcon.setItemMeta(meta);
		
		meta = smeltButtonIcon.getItemMeta();
		meta.setDisplayName("Heat");
		meta.addEnchant(Enchantment.FIRE_ASPECT, 88, true);
		smeltButtonIcon.setItemMeta(meta);
		
		meta = slagIcon.getItemMeta();
		meta.setDisplayName("Slag");
		slagIcon.setItemMeta(meta);
	}
	
	private static final Sound dismissSound = Sound.BLOCK_GRAVEL_BREAK;
	
	private static final Sound burnSound = Sound.BLOCK_FIRE_EXTINGUISH;
	
	private static final Sound loseSound = Sound.ENTITY_CAT_DEATH;
	
	private static final Sound winSound = Sound.ENTITY_PLAYER_LEVELUP;
	
	private static final Sound clickSound = Sound.UI_BUTTON_CLICK;
	
	private static final Sound liquifySound = Sound.BLOCK_LAVA_AMBIENT;
	
	private static final Sound smeltSound = Sound.BLOCK_FURNACE_FIRE_CRACKLE;
	
	private static final Sound solidifySound = Sound.BLOCK_STONE_PLACE;
	
	private static final String tooManyHeats = ChatColor.GRAY + "You melted the metal too many times, and it has become brittle and worthless";
	
	private static final String loseMessage = ChatColor.RED + "You failed to find anything useable";
	
	private static final String tooLittleMessage = ChatColor.RED + "You didn't salvage enough material to be worth anything";
	
	private static final String winMessage = "Success!" + ChatColor.RESET + " You successfully smelted ";
	
	private static final Sound startSound = Sound.ITEM_ARMOR_EQUIP_IRON;
	
	private static final String tooHardMessage = ChatColor.YELLOW + "The material is too hard to be dealt with. It must be liquified first.";
	
	private static final ChargeEffect successEffect = new ChargeEffect(Effect.LAVA_POP);
	
	private static final ChargeEffect failEffect = new ChargeEffect(Effect.SMALL_SMOKE);
	
	private int smeltingTime = 3;
	
	private Player player;
	
	private Inventory inv;
	
	private int clicksPerHeat;
	
	private int currentClicks;
	
	private int maxHeats;
	
	private int currentHeats;
	
	private double bonusQuality;
	
	private Map<Integer, ItemStack> backend;
	
	private List<Integer> slagSlots;
	
	private List<ItemStack> inputs;
	
	private int discardedMetals;
	
	private int generatedMetals;
	
	private double metalRatio;
	
	private int rows;
	
	private int skillLevel;
	
	private SmithingSkill skill;
	
	private QualityItem baseResult;
	
	private BossBar displayBar;
	
	private State gameState;
	
	public SmeltingGui(SmithingSkill skill, Player player, int difficulty, List<ItemStack> metals, int clicksPerHeat,
			int totalHeats,	double metalRatio, int rows, double bonusQuality, QualityItem result) {
		this(skill, player, difficulty, metals, clicksPerHeat, totalHeats, metalRatio, rows, bonusQuality, result,
				"Smelting - " + player.getUniqueId().toString().substring(0, 8));
	}
	
	public SmeltingGui(SmithingSkill skill, Player player, int difficulty, List<ItemStack> metals, int clicksPerHeat,
			int totalHeats,	double metalRatio, int rows, double bonusQuality, QualityItem result, String invName) {
		this.skill = skill;
		this.skillLevel = difficulty;
		this.player = player;
		this.inputs = metals;
		this.clicksPerHeat = clicksPerHeat;
		this.maxHeats = totalHeats;
		this.metalRatio = metalRatio;
		this.rows = Math.max(1, Math.min(6, rows));
		this.bonusQuality = bonusQuality;
		this.baseResult = result;
		
		this.backend = new HashMap<>();
		this.slagSlots = new LinkedList<>();
				
		int size = 9 * this.rows;
		this.inv = Bukkit.createInventory(null, size, invName);
		
		this.gameState = State.STOPPED;
		
	}
	
	@Override
	public Inventory getFormattedInventory(QuestPlayer player) {
		return inv;
	}

	@Override
	public Map<String, Object> serialize() {
		return null; //Runtime only, not serializable
	}

	@Override
	public InventoryItem getItem(int pos) {
		if (!player.isOnline()) {
			Alarm.getScheduler().unregister(this);
			loseGame();
			return null;
		}
		
		if (this.backend == null) {
			return null;
		}
		
		//check if it's button
		if (pos < 0) 
			return null;
		
		if (pos == 0) {
			player.playSound(player.getLocation(), clickSound, 1, 1);
			if (gameState != State.SOLID)
				return null;
			gameState = State.SMELTING;
			currentHeats++;
			currentClicks = 0;
			Alarm.getScheduler().schedule(this, 0, 1);
			player.playSound(player.getLocation(), smeltSound, 1, 1);
			return null;
			
		}
		

		//liquid and a block. check if it's really there and if so, remove it
		if (pos >= 9 * rows) {
			return null; //their inventory
		}
		
		if (this.gameState != State.LIQUID) {
			player.sendMessage(tooHardMessage);
			return null;
		}
		
		
		inv.setItem(pos, null);
		ItemStack back = backend.remove(pos);
		boolean slot = slagSlots.remove((Integer) pos);
		if (!slot && back != null) {
			//they discarded some metal!
			discardedMetals++;
			player.playSound(player.getLocation(), burnSound, 1, 1);
		}
		
		player.playSound(player.getLocation(), dismissSound, 1, 1);
		currentClicks++;
		update();
		
		return null; //tell it to do nothing
	}
	
	public void start() {
		if (gameState != State.STOPPED) {
			return;
		}

		//generate blocks.
		//first block is the button
		inv.setItem(0, smeltButtonIcon);
		
		ItemStack slot;
		for (int i = 1; i < 9 * rows; i++) {
			if (random.nextDouble() < metalRatio) {
				//it's metal. pick one
				slot = inputs.get(random.nextInt(inputs.size()));
				generatedMetals++;
			} else {
				slot = slagIcon;
				this.slagSlots.add(i);
			}
			
			
			inv.setItem(i, slot);
			backend.put(i, slot);
		}
		
		displayBar = Bukkit.createBossBar("Ore Temperature", BarColor.RED, BarStyle.SEGMENTED_20, new BarFlag[0]);
		displayBar.setProgress(0f);
		displayBar.addPlayer(player);
		
		player.getPlayer().sendMessage(ChatColor.GREEN + "Begin");
		player.playSound(player.getLocation(), startSound, 1, 1);
		
		this.gameState = State.SOLID;
		currentClicks = 0;
		currentHeats = 0;
		discardedMetals = 0;
		
	}
	
	private void update() {
		//win if we need to, and update bar
		this.displayBar.setProgress((double) (clicksPerHeat - currentClicks) / (double) clicksPerHeat);
		if (isWon()) {
			winGame();
			return;
		}
		
		if (currentClicks >= clicksPerHeat) {
			//lose if we need to
			if (currentHeats >= maxHeats)
				loseGame();
			else
				solidify();
		}
	}
	
	private void solidify() {
		for (int i = 1; i < 9 * rows; i++) {
			inv.setItem(i, backend.get(i));
		}
		player.playSound(player.getLocation(), solidifySound, 1, 1);
		gameState = State.SOLID;
	}
	
	private void liquify() {
		//turn all blocks into hidden block stuff
		for (int i = 1; i < 9 * rows; i++) {
			if (backend.containsKey(i))
			inv.setItem(i, defaultHotIcon);
		}
		player.playSound(player.getLocation(), liquifySound, 1, 1);
		gameState = State.LIQUID;
	}
	
	/**
	 * Returns the number of intact ore blocks, or -1 if they are not separated.
	 * If non-negative, the game is finished
	 */
	private boolean isWon() {
		return slagSlots.isEmpty();
	}

	
	private void loseGame() {
		if (player == null) {
			return;
		}
		
		if (!player.isOnline()) {
			return;
		}
		
		if (gameState == State.STOPPED)
			return;
		
		this.gameState = State.STOPPED;
		
		if (currentHeats >= maxHeats)
			player.sendMessage(tooManyHeats);
		else
			player.sendMessage(loseMessage);
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		skill.perform(qp, skillLevel, true);
		
		player.getWorld().playSound(player.getLocation(), loseSound, 1, 1);
		failEffect.play(player, player.getLocation());

		player.closeInventory();
	}
	
	private void winGame() {
		if (player == null) {
			return;
		}
		
		if (!player.isOnline()) {
			return;
		}
		
		this.gameState = State.STOPPED;
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		
		skill.perform(qp, skillLevel, false);
		QualityItem result = baseResult.clone();
		double intactRatio = Math.max(0.0, 1.0 - (discardedMetals / generatedMetals));
		result.setQuality((1.0 + bonusQuality) * intactRatio);
		result.getUnderlyingItem().setAmount((int) Math.round((double) result.getUnderlyingItem().getAmount() * intactRatio));
		
		FancyMessage msg;
		
		if (result.getUnderlyingItem().getAmount() <= 0)
			msg = new FancyMessage(tooLittleMessage);
		else {
			String name;
			if (result.getItem().getItemMeta() == null || result.getItem().getItemMeta().getDisplayName() == null) {
				name = YamlWriter.toStandardFormat(result.getItem().getType().toString());
			} else {
				name = result.getItem().getItemMeta().getDisplayName();
			}
			
			msg = new FancyMessage(winMessage)
					.color(ChatColor.GREEN)
				.then(result.getItem().getAmount() > 1 ? result.getItem().getAmount() + "x " : "a ")
				.then("[" + name + "]")
					.color(ChatColor.DARK_PURPLE)
					.itemTooltip(result.getItem());
		}
		
		
		msg.send(player);
		player.getWorld().playSound(player.getEyeLocation(), winSound, 1, 1);
		successEffect.play(player, player.getLocation());
		
		player.closeInventory();
		
	}
	
	@Override
	public void onClose() {
		if (backend != null) {
			loseGame();
			backend = null;
		}
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		skill.playerFinish(qp);
		
		displayBar.removeAll();
		
		gameState = State.STOPPED;
		Alarm.getScheduler().unregister(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SmeltingGui) {
			return ((SmeltingGui) o).player.getUniqueId().equals(player.getUniqueId());
		}
		
		return false;
	}
	
	@Override
	public void alarm(Integer key) {
		if (gameState == State.SMELTING) {
			if (key >= smeltingTime) {
				if (player.isOnline())
					player.sendMessage(ChatColor.GREEN + "The ore has melted!");
				liquify();
				currentClicks = 0;
				return;
			}
				
			//raise temperature bar
			displayBar.setProgress(((double) key + 1.0) / ((double) smeltingTime + 1.0));
			
			Alarm.getScheduler().schedule(this, key + 1, 1);
			
		}
	}
}
