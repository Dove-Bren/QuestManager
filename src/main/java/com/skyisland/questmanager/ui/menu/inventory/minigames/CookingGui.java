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

package com.skyisland.questmanager.ui.menu.inventory.minigames;

import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Furnace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.FoodItem;
import com.skyisland.questmanager.player.skill.QualityItem;
import com.skyisland.questmanager.player.skill.defaults.CookingSkill;
import com.skyisland.questmanager.player.skill.defaults.CookingSkill.CookingStats;
import com.skyisland.questmanager.player.skill.defaults.CookingSkill.OvenRecipe;
import com.skyisland.questmanager.player.skill.event.CraftEvent;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;
import com.skyisland.questmanager.ui.menu.inventory.CloseableGui;
import com.skyisland.questmanager.ui.menu.inventory.GuiInventory;
import com.skyisland.questmanager.ui.menu.inventory.InventoryItem;

public class CookingGui extends GuiInventory implements Alarmable<Integer>, Listener, CloseableGui {
	
	private enum State {
		STOPPED,
		STARTING,
		RUNNING;
	}
	
	public static CookingSkill skillLink = null;
	
	public static final Random RANDOM = new Random();
	
	public static final Sound BURN_SOUND = Sound.BLOCK_FIRE_EXTINGUISH;
	/////
	public static final Sound MINING_HIT_SOUND = Sound.BLOCK_STONE_BREAK;
	
	public static final Sound LOSE_SOUND = Sound.ENTITY_CAT_DEATH;
	
	public static final Sound WIN_SOUND = Sound.ENTITY_PLAYER_LEVELUP;
	
	public static final Sound COUNTDOWN_SOUND = Sound.BLOCK_NOTE_PLING;
	
	public static final Sound CLICK_SOUND = Sound.UI_BUTTON_CLICK;
	
	public static final String LOSE_MESSAGE = ChatColor.RED + "Your creation is not edible";
	
	public static final String BURN_MESSAGE = ChatColor.RED + "You burnt the food into ash!";
	
	public static final String WIN_MESSAGE = "Success!" + ChatColor.RESET + " You successfully cooked ";
	
	public static final String NO_RECIPE_MESSAGE = ChatColor.YELLOW + "You don't know any recipes with that item!";
	
	public static final Sound START_SOUND = Sound.BLOCK_FIRE_AMBIENT;
	
	private static final int START_TIME = 3;
	
	public static final Sound DANGER_SOUND = Sound.BLOCK_NOTE_PLING;
	
	private static final double UPDATE_PERIOD = .1;
	
	private static final double FAIL_RATE = 0.1;
	
	private enum Fuel {
		WOOD(40, new ItemStack(Material.WOOD)),
		LOG(60, new ItemStack(Material.LOG)),
		COAL(80, new ItemStack(Material.COAL)),
		CHARCOAL(60, new ItemStack(Material.COAL, 1, (short) 1)),
		FISH(10, new ItemStack(Material.RAW_FISH)),
		TWIGS(20, new ItemStack(Material.SAPLING)),
		COALBLOCK(100, new ItemStack(Material.COAL_BLOCK));
		
		
		
		public short burntime;
		
		public ItemStack icon;
		
		Fuel(int burntime, ItemStack icon) {
			this.burntime = (short) burntime;
			this.icon = icon;
		}
	}
	
	private Furnace furnace;
	
	private Player player;
	
	private double cookTime;
	
	private double maxCookTime;
	
	private double fuelSwapTime;
	
	private double bonusQuality;
	
	private int skillLevel;
	
	private int failInterval;
	
	private BossBar displayBar;
	
	private BossBar failBar;
	
	private State gameState;
	
	private double missIndex;
	
	private Fuel topFuel;
	
	private Fuel bottomFuel;
	
	private ItemStack currentTarget;
	
	private double nextFuelTime;
	
	private boolean useInputQuality;
	
	private double cooldown;
	
	private int inputAmount;
	
	public CookingGui(Player player, Furnace furnace, double bonusQuality, boolean useInputQuality) {
		this.player = player;
		this.furnace = furnace;
		this.bonusQuality = bonusQuality;
		this.useInputQuality = useInputQuality;
		
		this.gameState = State.STOPPED;
		
//		for (Fuel fuel : Fuel.values()) {
//			Bukkit.addRecipe(new FurnaceRecipe(new ItemStack(Material.FIRE), fuel.icon.getType()));
//		}
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
		
	}
	
	@Override
	public Inventory getFormattedInventory(QuestPlayer player) {
		return furnace.getInventory();
	}

	@Override
	public Map<String, Object> serialize() {
		return null; //Runtime only, not serializable
	}

	@Override
	public InventoryItem getItem(int pos, InventoryAction action) {
		//0 is top slot, 1 is bottom, 2 is result
		if (pos > 2) {
			int slot = pos + 6;
			slot = slot % 36;
			//inventory click. Offer up sacrifice if we're not running currently
			if (gameState != State.STOPPED)
				return null;
			
			this.currentTarget = player.getInventory().getItem(slot);
			
			if (currentTarget == null) {
				return null;
			}
			
			OvenRecipe recipe = skillLink.getOvenRecipe(player.getInventory().getItem(slot));
			if (recipe == null) {
				player.sendMessage(NO_RECIPE_MESSAGE);
				return null;
			}
			
			inputAmount = 1;
			if (action == InventoryAction.PICKUP_HALF) {
				inputAmount = currentTarget.getAmount();
			}
			
			ItemStack replace = null;
			if (currentTarget.getAmount() > inputAmount) {
				replace = currentTarget.clone();
				replace.setAmount(currentTarget.getAmount() - inputAmount);
			}
			
			player.getInventory().setItem(slot, replace);
			
			currentTarget.setAmount(inputAmount);
			
			furnace.getInventory().setResult(currentTarget);
			QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
			CookingStats stats = skillLink.getCookingStats(recipe, qp);
			
			this.cookTime = this.maxCookTime = stats.getCookTime();
			this.failInterval = stats.getFailInterval();
			this.fuelSwapTime = stats.getFuelSwapTime();
			
			start();
			return null;
			
		}
		
		//we're running or starting
		if (gameState == State.STARTING)
			return null;
		
		//if fuel slot, submitFuel() e lse nothing
		if (pos < 2) {
			submitFuel(pos);
		}
		
		
		
		return null; //tell it to do nothing
	}
	
	private void generateFuel() {
		this.topFuel = Fuel.values()[RANDOM.nextInt(Fuel.values().length)];
		this.bottomFuel = Fuel.values()[RANDOM.nextInt(Fuel.values().length)];
		furnace.getInventory().setSmelting(topFuel.icon);
		furnace.getInventory().setFuel(bottomFuel.icon);
		this.nextFuelTime = fuelSwapTime;
	}
	
	private void submitFuel(int pos) {
		//a player has selected the given fuel.
		Fuel slot = topFuel;
		if (pos == 1)
			slot = bottomFuel;
		if (slot == null)
			return;
		
		furnace.setBurnTime((short) (slot.burntime + furnace.getBurnTime()));
		generateFuel();
	}
	
	private void start() {
		if (gameState != State.STOPPED) {
			return;
		}
		
		
		
		this.missIndex = 0;
		this.cookTime = maxCookTime;
		this.cooldown = 0;
		
		Alarm.getScheduler().schedule(this, 0, 1);
		player.getPlayer().sendMessage(ChatColor.RED + "Get ready...");
		player.playSound(player.getLocation(), START_SOUND, 1, 1);
		
		displayBar = Bukkit.createBossBar("Cooking Progress", BarColor.BLUE, BarStyle.SEGMENTED_20, new BarFlag[0]);
		displayBar.setProgress(1f);
		displayBar.addPlayer(player);
		
		failBar = Bukkit.createBossBar("Failure", BarColor.BLUE, BarStyle.SEGMENTED_20, new BarFlag[0]);
		failBar.setProgress(0f);
		failBar.addPlayer(player);
		
		
		this.gameState = State.STARTING;
		
	}
	
	private void update() {
		displayBar.setProgress(Math.max(0, cookTime / maxCookTime));
		failBar.setProgress(Math.min(1, missIndex));
		
		if (missIndex < .20)
			failBar.setColor(BarColor.RED);
		
		if (furnace.getBurnTime() > 200) {
			loseGame();
			player.getWorld().playSound(player.getLocation(), BURN_SOUND, 1, 1);
		}
		if (furnace.getBurnTime() < 0)
			loseGame();
		
		if (missIndex > 1.0) 
			loseGame();
		
		if (cookTime <= 0)
			winGame();
		
	}

	
	private void loseGame() {
		if (gameState != State.RUNNING)
			return;
		
		if (player == null) {
			return;
		}
		
		if (!player.isOnline()) {
			return;
		}
		
		this.gameState = State.STOPPED;
		
		displayBar.removeAll();
		failBar.removeAll();
		Alarm.getScheduler().unregister(this);
		furnace.getInventory().setFuel(null);
		furnace.getInventory().setSmelting(null);
		furnace.getInventory().setResult(null);
		this.topFuel = null;
		this.bottomFuel = null;
		this.currentTarget = null;
		
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		if (skillLink != null) {
			int range = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillCutoff();
			skillLink.perform(qp, Math.max(qp.getSkillLevel(skillLink) - range, Math.min(qp.getSkillLevel(skillLink) + range, skillLevel)), true);
		}
		
		//player.sendMessage(LOSE_MESSAGE);
		player.getWorld().playSound(player.getLocation(), LOSE_SOUND, 1, 1);
		player.sendMessage(LOSE_MESSAGE);
		
		if (furnace.getBurnTime() > 200) {
			ItemStack ash = new ItemStack(Material.SULPHUR);
			ash.setAmount(inputAmount);
			ItemMeta meta = ash.getItemMeta();
			meta.setDisplayName("Ash");
			ash.setItemMeta(meta);
			if (!(player.getInventory().addItem(ash)).isEmpty()) {
				player.sendMessage(ChatColor.RED + "There is no space left in your inventory");
				player.getWorld().dropItem(player.getEyeLocation(), ash);
			}
		}
		
	}
	
	private void winGame() {
		if (gameState != State.RUNNING)
			return;
		
		if (player == null) {
			return;
		}
		
		if (!player.isOnline()) {
			return;
		}
		
		if (skillLink == null) {
			return;
		}


		displayBar.removeAll();
		failBar.removeAll();
		Alarm.getScheduler().unregister(this);
		this.gameState = State.STOPPED;
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		OvenRecipe recipe = skillLink.getOvenRecipe(currentTarget);
		FoodItem result = recipe.output.clone();
		result.getUnderlyingItem().setAmount(inputAmount * result.getUnderlyingItem().getAmount());
		
		if (useInputQuality) {
			result.setQuality((new QualityItem(currentTarget)).getQuality());
		}

		furnace.getInventory().setFuel(null);
		furnace.getInventory().setSmelting(null);
		furnace.getInventory().setResult(null);
		this.topFuel = null;
		this.bottomFuel = null;
		this.currentTarget = null;

		if (inputAmount > 1) {
			skillLevel += 3;
		}
		
		CraftEvent event = new CraftEvent(qp, CraftEvent.CraftingType.COOKING, skillLevel, result);
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isFail()) {
			int range = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillCutoff();
			skillLink.perform(qp, Math.max(qp.getSkillLevel(skillLink) - range, Math.min(qp.getSkillLevel(skillLink) + range, skillLevel)), true);
			return;
		}
		
		result.setQuality(result.getQuality() * event.getQualityModifier());
		result.getUnderlyingItem().setAmount((int) (result.getUnderlyingItem().getAmount() * event.getQuantityModifier()));
		
		int range = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillCutoff();
		skillLink.performMajor(qp, Math.max(qp.getSkillLevel(skillLink) - range, Math.min(qp.getSkillLevel(skillLink) + range, skillLevel)), false);
		
		FancyMessage msg;
		
		result.setQuality(result.getQuality() * ((1 - missIndex) + bonusQuality));
		
		//factor in bonus food points
		result.setFoodLevel(skillLink.calculateFoodLevel
				(result.getFoodLevel(), result.getQuality()));
		
		
		String name;
		if (result.getItem().getItemMeta() == null || result.getItem().getItemMeta().getDisplayName() == null) {
			name = YamlWriter.toStandardFormat(result.getItem().getType().toString());
		} else {
			name = result.getItem().getItemMeta().getDisplayName();
		}
		
		msg = new FancyMessage(WIN_MESSAGE)
				.color(ChatColor.GREEN)
			.then(result.getItem().getAmount() > 1 ? result.getItem().getAmount() + "x " : "a ")
			.then("[" + name + "]")
				.color(ChatColor.DARK_PURPLE)
				.itemTooltip(result.getItem());
		
		
		msg.send(player);
		player.getWorld().playSound(player.getEyeLocation(), WIN_SOUND, 1, 1);
		if (!(player.getInventory().addItem(result.getItem())).isEmpty()) {
			player.sendMessage(ChatColor.RED + "There is no space left in your inventory");
			player.getWorld().dropItem(player.getEyeLocation(), result.getItem());
		}
		
	}

	public static void setCookingSkill(CookingSkill skill) {
		skillLink = skill;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CookingGui) {
			return ((CookingGui) o).player.getUniqueId().equals(player.getUniqueId());
		}
		
		return false;
	}
	
	@Override
	public void alarm(Integer key) {
		if (!player.isOnline()) {
			loseGame();
			return;
		}
		
		Player p = player.getPlayer();
		
		
		if (gameState == State.STARTING) {
			if (key >= START_TIME) {
				p.sendMessage(ChatColor.RED + "Go!");
				generateFuel();
				gameState = State.RUNNING;
				Alarm.getScheduler().schedule(this, 0, UPDATE_PERIOD);
				furnace.setBurnTime((short) 100);
				furnace.update();
				return;
			}
			
			p.sendMessage(ChatColor.YELLOW + "" + (START_TIME - key));
			p.playSound(p.getLocation(), COUNTDOWN_SOUND, 1, 1);
			
			furnace.setBurnTime((short) (furnace.getBurnTime() + (100 / START_TIME)));
			furnace.update();
			Alarm.getScheduler().schedule(this, key + 1, 1);
			return;
		}
		
		if (gameState == State.RUNNING) {
			//update based on burn time, etc
			this.nextFuelTime -= UPDATE_PERIOD;
			if (nextFuelTime <= 0)
				generateFuel();
			

			
			if (Math.abs(furnace.getBurnTime() - 100) > failInterval) {
				missIndex += (FAIL_RATE * UPDATE_PERIOD);
				cooldown -= UPDATE_PERIOD;
				if (cooldown <= 0) {
					cooldown = 1.0;
					player.playSound(player.getLocation(), DANGER_SOUND, 1f, 1.8f);
				}
			} else {
				this.cookTime -= UPDATE_PERIOD;
			}
			
			update();
			
			
			Alarm.getScheduler().schedule(this, key, UPDATE_PERIOD);
		}
	}
	
	@EventHandler
	public void onItemBurn(FurnaceBurnEvent e) {
		//we don't want out stuff to burn, so cancel it if it's ours!
		if (e.getBlock().getLocation().equals(furnace.getLocation())) {
			e.setCancelled(true);
			e.setBurning(true);
			
		
			return;
		}
	}

	@Override
	public void onClose() {
		//player closed the inventory is what this means
		HandlerList.unregisterAll(this);
		loseGame();
		
		if (skillLink != null)
			skillLink.unregisterOven(furnace.getLocation());
		return;
	}
	
	@EventHandler
	public void onItemSmelt(FurnaceSmeltEvent e) {
		if (e.getBlock().getLocation().equals(furnace.getLocation())) {
			e.setCancelled(true);
		}
	}
	
}
