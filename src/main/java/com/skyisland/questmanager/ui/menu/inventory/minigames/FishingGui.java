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

import java.util.HashMap;
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
import com.skyisland.questmanager.player.skill.defaults.FishingSkill;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;
import com.skyisland.questmanager.ui.menu.inventory.CloseableGui;
import com.skyisland.questmanager.ui.menu.inventory.GuiInventory;
import com.skyisland.questmanager.ui.menu.inventory.InventoryItem;

public class FishingGui extends GuiInventory implements Alarmable<Integer>, CloseableGui {
	
	public static FishingSkill skillLink = null;
	
	public static final Random RANDOM = new Random();
	
	public static final ItemStack DEFAULT_OBSTACLE_ICON = new ItemStack(Material.WATER_LILY);
	
	public static final ItemStack DEFAULT_METER_OFF_ICON = new ItemStack(Material.WOOL, 1, (short) 4);
	
	public static final ItemStack DEFAULT_METER_ON_ICON = new ItemStack(Material.WOOL, 1, (short) 14);
	
	public static final ItemStack DEFAULT_REEL_OFF_ICON = new ItemStack(Material.STRING);
	
	public static final ItemStack DEFAULT_REEL_ON_ICON = new ItemStack(Material.FISHING_ROD);
	
	public static final ItemStack DEFAULT_WATER_ICON = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
	
	{
		ItemMeta meta;
		
		meta = DEFAULT_OBSTACLE_ICON.getItemMeta();
		meta.setDisplayName("Lilypad");
		DEFAULT_OBSTACLE_ICON.setItemMeta(meta);
		meta = DEFAULT_WATER_ICON.getItemMeta();
		meta.setDisplayName("Water");
		DEFAULT_WATER_ICON.setItemMeta(meta);
		meta = DEFAULT_REEL_OFF_ICON.getItemMeta();
		meta.setDisplayName("Stop Reeling");
		DEFAULT_REEL_OFF_ICON.setItemMeta(meta);
		meta = DEFAULT_REEL_ON_ICON.getItemMeta();
		meta.setDisplayName("Start Reeling");
		DEFAULT_REEL_ON_ICON.setItemMeta(meta);
		meta = DEFAULT_METER_ON_ICON.getItemMeta();
		meta.setDisplayName("Line Stress");
		DEFAULT_METER_ON_ICON.setItemMeta(meta);
		meta = DEFAULT_METER_ON_ICON.getItemMeta();
		meta.setDisplayName("Line Stress");
		DEFAULT_METER_ON_ICON.setItemMeta(meta);
	}
	
	public static final Sound WATER_SOUND = Sound.BLOCK_WATERLILY_PLACE;
	
	public static final Sound COUNTDOWN_SOUND = Sound.BLOCK_NOTE_PLING;
	
	public static final Sound START_SOUND = Sound.ENTITY_BOBBER_SPLASH;
	
	public static final Sound LOSE_SOUND = Sound.ENTITY_CAT_DEATH;
	
	public static final Sound WIN_SOUND = Sound.ENTITY_PLAYER_LEVELUP;
	
	public static final Sound CLICK_SOUND = Sound.UI_BUTTON_CLICK;
	
	public static final String LOSE_MESSAGE = ChatColor.RED + "You failed to land the fish";
	
	public static final String WIN_MESSAGE = "Success!" + ChatColor.RESET + " You caught ";
	
	private static final ChargeEffect SUCCESS_EFFECT = new ChargeEffect(Effect.SPLASH);
	
	private static final ChargeEffect FAIL_EFFECT = new ChargeEffect(Effect.SMALL_SMOKE);
	
	private static final class ObstacleSetter implements Alarmable<Integer> {
		
		private FishingGui gui;
		
		private int id;
		
		public ObstacleSetter(FishingGui gui, double time) {
			Alarm.getScheduler().schedule(this, 0, time);
			this.gui = gui;
			id = RANDOM.nextInt(Integer.MAX_VALUE);
		}
		
		@Override
		public void alarm(Integer pos) {
			gui.setObstacle();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof ObstacleSetter) {
				if (((ObstacleSetter) o).id == id) {
					return true;
				}
			}
			
			return false;
		}
		
	}
	
	private static class ReelTimer implements Alarmable<Integer> {
		
		private FishingGui gui;
		
		private int id;
		
		public ReelTimer(FishingGui gui) {
			Alarm.getScheduler().schedule(this, 0, .5);
			this.id = RANDOM.nextInt(Integer.MAX_VALUE);
			this.gui = gui;
		}
		
		@Override
		public void alarm(Integer key) {
			if (gui.phase != GamePhase.RUNNING) {
				return;
			}
			
			gui.updateReel();
		}
		
		protected void set() {
			Alarm.getScheduler().schedule(this, 0, .5);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof ReelTimer) {
				if (((ReelTimer) o).id == id) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	public enum GamePhase {
		SETTINGUP,
		STARTING,
		RUNNING,
		DONE
	}

	protected GamePhase phase;
	
	private String invName;
	
	private Player player;
	
	private Inventory inv;
	
	private int waterRows;
	
	private float reelDifficulty;
	
	private float reelDeviation;
	
	private double obstacleTime;
	
	private double obstacleDeviation;
	
	private double completionTime;
	
	private double maxCompletionTime;
	
	private Map<Integer, Boolean> obstacles;
	
	private float lineStress;
	
	private boolean isReeling;
	
	private boolean isStuck;
	
	private QualityItem result;
	
	private ItemStack obstacleIcon;
	
	private ItemStack meterOffIcon;
	
	private ItemStack meterOnIcon;
	
	private ItemStack reelOffIcon;
	
	private ItemStack reelOnIcon;
	
	private ItemStack waterIcon;
	
	private int stressCache;
	
	private int skillLevel;
	
	private BossBar displayBar;
	
	private ReelTimer reel;
	
	public FishingGui(Player player, QualityItem result, int skillLevel, int waterRows, float reelDifficulty, 
			float reelDeviation, double obstacleTime, double obstacleDeviation, double completionTime) {
		this(player, result, skillLevel, waterRows, reelDifficulty, reelDeviation, obstacleTime, obstacleDeviation, 
				completionTime, "Fishing - " + player.getUniqueId().toString().substring(0, 8));
	}
	
	public FishingGui(Player player, QualityItem result, int skillLevel, int waterRows, float reelDifficulty, 
			float reelDeviation, double obstacleTime, double obstacleDeviation, double completionTime, 
			String name) {
		this.player = player;
		this.reelDifficulty = reelDifficulty;
		this.reelDeviation = reelDeviation;
		this.obstacleTime = obstacleTime;
		this.obstacleDeviation = obstacleDeviation;
		this.invName = name;
		this.waterRows = Math.max(1, waterRows);
		this.result = result;
		this.completionTime = completionTime;
		this.maxCompletionTime = this.completionTime;
		this.skillLevel = skillLevel;
		
		this.isStuck = false;
		this.isReeling = false;
		this.obstacles = new HashMap<>();
		
		this.meterOffIcon = DEFAULT_METER_OFF_ICON;
		this.meterOnIcon = DEFAULT_METER_ON_ICON;
		this.reelOffIcon = DEFAULT_REEL_OFF_ICON;
		this.reelOnIcon = DEFAULT_REEL_ON_ICON;
		this.obstacleIcon = DEFAULT_OBSTACLE_ICON;
		this.waterIcon = DEFAULT_WATER_ICON;
		
		this.stressCache = 3;
		this.phase = GamePhase.SETTINGUP;
		
		int size = 9 * (1 + this.waterRows);
		this.inv = Bukkit.createInventory(null, size, invName);
		
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
		if (this.phase != GamePhase.RUNNING) {
			return null;
		}
		
		//real logic is here
		/*
		 * Check slot. If in player's inventory, cancel.
		 * If top slow, check if it's buttons, do action or nothing
		 * Check if we have obstacle there. IF so, get rid of it
		 */
		int size = 9 * (waterRows + 1);
		
		if (pos > size) {
			return null;
		}
		
		if (pos < 9) {
			//top bar. If 0, 8 it's a button
			if (pos == 0) {
				//stop reeling
				this.isReeling = false;
				player.playSound(player.getLocation(), CLICK_SOUND, 1, 1);
				return null;
			}
			if (pos == 8) {
				this.isReeling = true;
				player.playSound(player.getLocation(), CLICK_SOUND, 1, 1);
				return null;
			}
		}
		
		//somewhere in the water
		if (obstacles.containsKey(pos)) {
			player.playSound(player.getLocation(), WATER_SOUND, 1, 1);
			obstacles.remove(pos);
			inv.setItem(pos, waterIcon);
			
			if (obstacles.isEmpty()) {
				isStuck = false;
			}
		}
		
		
		return null; //tell it to do nothing
	}
	
	public void start() {

		//Set up top bar
		inv.setItem(0, this.reelOffIcon);
		for (int i = 1; i < 4; i++) {
			inv.setItem(i, this.meterOnIcon);
		}
		for (int i = 4; i < 8; i++) {
			inv.setItem(i, this.meterOffIcon);
		}
		inv.setItem(8, this.reelOnIcon);
		
		displayBar = Bukkit.createBossBar("Fishing Progress", BarColor.BLUE, BarStyle.SEGMENTED_20, new BarFlag[0]);
		displayBar.setProgress(1f);
		displayBar.addPlayer(player);
		
		//start filling the 'sea'
		//time is time to fill all in 5 seconds, or .2 if less than that (it would be too fast!)
		Alarm.getScheduler().schedule(this, 0, Math.max(.2, 5 / (9 * this.waterRows)));
	}
	
	public void alarm(Integer reminder) {
		if (!player.isOnline()) {
			return; //silently fade to nothing
		}
		
		if (phase == GamePhase.DONE) {
			return; //they left, or somethign messed up
		}
		
		if (phase == GamePhase.SETTINGUP) {
			//populating 'sea'
			inv.setItem(reminder + 9, this.waterIcon);
			player.playSound(player.getEyeLocation(), WATER_SOUND, 1, 1);
			
			if (reminder >= (9 * this.waterRows) -1) {
				startCountdown();
				return;
			}
			
			Alarm.getScheduler().schedule(this, reminder + 1, Math.max(.2, 5 / (9 * this.waterRows)));
			return;
		}
		
		if (phase == GamePhase.STARTING) {
			//countdown
			if (reminder == 0) {
				player.sendMessage(ChatColor.RED + "Go!");
				player.playSound(player.getEyeLocation(), START_SOUND, 1, 1);
				startGame();
				return;
			}
			
			player.sendMessage(ChatColor.YELLOW + "" + reminder);
			player.playSound(player.getEyeLocation(), COUNTDOWN_SOUND, 1, 1);
			Alarm.getScheduler().schedule(this, reminder - 1, 1);
			return;
		}
		
		if (phase == GamePhase.RUNNING) {
			//deduct from running tiem if conditions are good
			if (Math.abs(this.lineStress - 0.5f) < 0.2f) {
				this.completionTime -= .25;
				displayBar.setColor(BarColor.BLUE);
			} else {
				displayBar.setColor(BarColor.RED);
			}
			
			displayBar.setProgress(Math.max(0f, completionTime / maxCompletionTime));
			
			if (this.completionTime < 0)
				winGame();
			else
				Alarm.getScheduler().schedule(this, 0, .25);
			return;
		}
	}
	
	private void startCountdown() {
		this.phase = GamePhase.STARTING;
		Alarm.getScheduler().schedule(this, 3, 1);
		player.getPlayer().sendMessage(ChatColor.GREEN + "Starting in...");
	}
	
	private void startGame() {
		this.phase = GamePhase.RUNNING;
		this.isReeling = true;
		this.lineStress = ((float) 3 / (float) 7);
		
		double oTime = Math.max(.5, RANDOM.nextGaussian() * this.obstacleDeviation + this.obstacleTime);
		new ObstacleSetter(this, oTime);
		reel = new ReelTimer(this);
		
		Alarm.getScheduler().schedule(this, 0, .25);
	}
	
	protected void setObstacle() {
		if (phase != GamePhase.RUNNING) {
			return;
		}
		//set up obstacle at RANDOM spot
		double oTime = Math.max(.5, RANDOM.nextGaussian() * this.obstacleDeviation + this.obstacleTime);
		new ObstacleSetter(this, oTime);
		
		if (obstacles.size() >= 9 * waterRows) {
			return; //no place to put one
		}
		
		int slot = RANDOM.nextInt(9 * waterRows) + 9;
		while (inv.getItem(slot).isSimilar(this.obstacleIcon)) {
			slot = RANDOM.nextInt(9 * waterRows) + 9;
		}
		
		player.playSound(player.getLocation(), WATER_SOUND, 1, 1);
		obstacles.put(slot, true);
		inv.setItem(slot, obstacleIcon);
		this.isStuck = true;
	}
	
	protected void updateReel() {
		if (phase != GamePhase.RUNNING) {
			return;
		}
		
		//calculate reel + or -
		float diff = ((float) RANDOM.nextGaussian() * reelDeviation) + reelDifficulty;
		
		if (!isReeling && !isStuck) {
			this.lineStress -= diff * 5;
		} else {
			//either reeling or stuck, so stress increases
			this.lineStress += diff;
		}
		
		if (lineStress > 1f || lineStress < 0) {
			//game lost
			loseGame();
			return;
		}
		

		reel.set();
		//update inventory 
		int icons = Math.round(lineStress * 7f);
		if (icons == stressCache) {
			return; //no need to update
		}
		
		for (int i = 1; i < icons + 1; i++) {
			inv.setItem(i, meterOnIcon);
		}
		for (int i = icons + 1; i < 8; i++) {
			inv.setItem(i, meterOffIcon);
		}
		
	}
	
	private void loseGame() {
		if (phase == GamePhase.DONE || player == null || reel == null) {
			return;
		}
		Alarm.getScheduler().unregister(reel);
		
		phase = GamePhase.DONE;
		Alarm.getScheduler().unregister(this);
		
		if (!player.isOnline()) {
			return;
		}
		
		player.closeInventory();
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		if (skillLink != null) {
			int range = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillCutoff();
			skillLink.perform(qp, Math.max(qp.getSkillLevel(skillLink) - range, Math.min(qp.getSkillLevel(skillLink) + range, skillLevel)), true);
		}
		
		//player.sendMessage(LOSE_MESSAGE);
		player.getWorld().playSound(player.getLocation(), LOSE_SOUND, 1, 1);
		
		clean();
		
	}
	
	private void winGame() {
		phase = GamePhase.DONE;
		Alarm.getScheduler().unregister(this);
		
		if (!player.isOnline()) {
			return;
		}
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		if (skillLink != null) {
			int range = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillCutoff();
			skillLink.performMajor(qp, Math.max(qp.getSkillLevel(skillLink) - range, Math.min(qp.getSkillLevel(skillLink) + range, skillLevel)), false);
		}
		
		FancyMessage msg;
		if (result == null) {
			msg = new FancyMessage("Your fishing was successful, but yielded no returns!")
					.color(ChatColor.YELLOW);
		} else {
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
		}
		
		msg.send(player);
		player.getWorld().playSound(player.getEyeLocation(), WIN_SOUND, 1, 1);
		player.getInventory().addItem(this.result.getItem());
		
		this.result = null;
		
		player.closeInventory();
		
		clean();
	}
	
	/**
	 * Returns the <i>result the player didn't get!</i> If null, the player completed and fishing was
	 * successful, or null was passed in to begin with
	 */
	@Override
	public void onClose() {
		//return result. If null, already was given
		ItemStack[] ret = (result == null ? null : new ItemStack[]{result.getItem()});
		Alarm.getScheduler().unregister(this);
		
		if (ret == null) {
			SUCCESS_EFFECT.play(player, null);
		} else {
			player.sendMessage(LOSE_MESSAGE);
			FAIL_EFFECT.play(player, null);
		}
		
		loseGame();
		clean();
	}

	public void setObstacleIcon(ItemStack obstacleIcon) {
		this.obstacleIcon = obstacleIcon;
	}

	public void setMeterOffIcon(ItemStack meterOffIcon) {
		this.meterOffIcon = meterOffIcon;
	}

	public void setMeterOnIcon(ItemStack meterOnIcon) {
		this.meterOnIcon = meterOnIcon;
	}

	public void setReelOffIcon(ItemStack reelOffIcon) {
		this.reelOffIcon = reelOffIcon;
	}

	public void setReelOnIcon(ItemStack reelOnIcon) {
		this.reelOnIcon = reelOnIcon;
	}

	public void setWaterIcon(ItemStack waterIcon) {
		this.waterIcon = waterIcon;
	}
	
	public static void setFishingSkill(FishingSkill skill) {
		skillLink = skill;
	}
	
	private void clean() {
		this.inv = null;
		this.meterOffIcon = null;
		this.meterOnIcon = null;
		this.reelOffIcon = null;
		this.reelOnIcon = null;
		this.obstacleIcon = null;
		if (obstacles != null)
			this.obstacles.clear();
		this.obstacles = null;
		if (displayBar != null)
			displayBar.removeAll();
		displayBar = null;
		//this.player = null;
		this.result = null;
		this.waterIcon = null;
		this.reel = null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FishingGui) {
			return ((FishingGui) o).player.getUniqueId().equals(player.getUniqueId());
		}
		
		return false;
	}
}
