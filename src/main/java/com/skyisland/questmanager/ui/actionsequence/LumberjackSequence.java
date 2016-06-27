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

package com.skyisland.questmanager.ui.actionsequence;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.effects.ChargeEffect;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.QualityItem;
import com.skyisland.questmanager.player.skill.defaults.LumberjackSkill;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;

public class LumberjackSequence implements Listener, Alarmable<Integer> {
	
	private enum State {
		STOPPED,
		STARTING,
		RUNNING,
		FINISHED
	}

	private static final Random RANDOM = new Random();
	
	public static final String MISS_MESSAGE = ChatColor.RED + "You miss the swing, resulting in no good wood";
	
	public static final String EARLY_MESSAGE = ChatColor.RED + "You swung too eagerly, ruining the wood";
	
	public static final String WIN_MESSAGE = ChatColor.GREEN + "You successfully chopped ";
	
	public static final String PERFECT_MESSAGE = ChatColor.GREEN + "You landed a perfect chop!";
	
	public static final Sound COUNTDOWN_SOUND = Sound.BLOCK_NOTE_PLING;
	
	public static final Sound START_SOUND = Sound.BLOCK_GRAVEL_BREAK;
	
	public static final Sound LOSE_SOUND = Sound.ENTITY_CAT_DEATH;
	
	public static final Sound WIN_SOUND = Sound.ENTITY_PLAYER_LEVELUP;
	
	public static final Sound CLICK_SOUND = Sound.BLOCK_WOOD_BREAK;
	
	public static final Sound PERFECT_SOUND = Sound.BLOCK_NOTE_PLING;
	
	private static final ChargeEffect SUCCESS_EFFECT = new ChargeEffect(Effect.CRIT);
	
	private static final ChargeEffect FAIL_EFFECT = new ChargeEffect(Effect.SMALL_SMOKE);
	
	private static final double PERFECT_HIT_THRESHHOLD = 0.025;
	
	private static final double HIT_OFFBY_INTERVAL = 0.1;
	
	private static final int COUNTDOWN_TIMER = 3;
	
	private static final double TIME_STEP = 0.05;
	
	private static final double PERFECT_BONUS = .50;
	
	private static final Effect CHOP_EFFECT = Effect.STEP_SOUND;
	
	private static LumberjackSkill skillLink;
	
	private State state;
	
	private QuestPlayer player;
	
	private QualityItem input;
	
	private Vector treeLocation;
	
	private double averageSwingTime;
	
	private double swingTimeDeviation;
	
	private double reactionTime;
	
	private int hits;
	
	private int offByIndex;
	
	private BossBar displayBar;
	
	private String displayName;
	
	private double secondsRemaining;
	
	private double swingTime;
	
	private int skillLevel;

	public LumberjackSequence(QuestPlayer player, Vector treeLocation, QualityItem input, double averageSwingTime,
			double swingTimeDeviation, double reactionTime, int hits, String displayName, int skillLevel) {
		this.player = player;
		this.input = input;
		this.averageSwingTime = averageSwingTime;
		this.swingTimeDeviation = swingTimeDeviation;
		this.reactionTime = reactionTime;
		this.hits = hits;
		this.treeLocation = treeLocation;
		this.displayName = displayName;
		this.skillLevel = skillLevel;
		
		this.state = State.STOPPED;
		this.offByIndex = 0;
	}
	
	public void start() {
		if (!player.getPlayer().isOnline())
			return;
		
		state = State.STARTING;
		Alarm.getScheduler().schedule(this, 0, 1);
		player.getPlayer().getPlayer().sendMessage(ChatColor.GREEN + "Get Ready...");
		
		this.displayBar = Bukkit.createBossBar(displayName, BarColor.BLUE, BarStyle.SEGMENTED_20, new BarFlag[0]);
		displayBar.setProgress(1f);
		displayBar.addPlayer(player.getPlayer().getPlayer());
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	private void setTimer() {
		Alarm.getScheduler().unregister(this);
			
		this.secondsRemaining = averageSwingTime + (RANDOM.nextGaussian() * swingTimeDeviation);
		this.swingTime = secondsRemaining;
		
		displayBar.setProgress(1);
		displayBar.setColor(BarColor.YELLOW);
		
		Alarm.getScheduler().schedule(this, 0, TIME_STEP);
		
	}
	
	@Override
	public void alarm(Integer key) {
		
		if (!player.getPlayer().isOnline()) {
			return;
		}

		Player p = player.getPlayer().getPlayer();
		if (state == State.STARTING) {
			//starting countdown
			if (key >= COUNTDOWN_TIMER) {
				player.getPlayer().getPlayer().sendMessage(ChatColor.RED + "Go!");
				state = State.RUNNING;
				p.playSound(p.getLocation(), START_SOUND, 1, 1);
				setTimer();
				return;
			}
			
			p.sendMessage(ChatColor.YELLOW + "" + (COUNTDOWN_TIMER - key));
			p.playSound(p.getLocation(), COUNTDOWN_SOUND, 1, 1);
			Alarm.getScheduler().schedule(this, key + 1, 1);
			return;
		}
		
		if (state == State.RUNNING) {
			
			this.secondsRemaining -= TIME_STEP;
			
			displayBar.setProgress(Math.max(0, secondsRemaining / swingTime));
			if (this.secondsRemaining < reactionTime)
				displayBar.setColor(BarColor.GREEN);
			
			if (this.secondsRemaining <= PERFECT_HIT_THRESHHOLD)
				displayBar.setColor(BarColor.BLUE);
			
			if (this.secondsRemaining < -reactionTime) {
				loseGame();
				return;
			}
			
			Alarm.getScheduler().schedule(this, 0, TIME_STEP);
		}
	}
	
	private void loseGame() {
		this.state = State.FINISHED;
		
		Alarm.getScheduler().unregister(this);
		displayBar.removeAll();
		
		if (skillLink != null) {
			int range = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillCutoff();
			skillLink.playerFinish(player);
			skillLink.perform(player, Math.max(player.getSkillLevel(skillLink) - range, Math.min(player.getSkillLevel(skillLink) + range, skillLevel)), true);
		}
		
		if (!player.getPlayer().isOnline()) {
			return;
		}
		Player p = player.getPlayer().getPlayer();
		
		p.sendMessage(MISS_MESSAGE);
		p.getWorld().playSound(p.getLocation(), LOSE_SOUND, 1, 1);
		FAIL_EFFECT.play(p, p.getLocation());
		
	}
	
	private void winGame() {
		this.state = State.FINISHED;
		Alarm.getScheduler().unregister(this);
		
		displayBar.removeAll();
		
		if (skillLink != null) {
			int range = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillCutoff();
			skillLink.playerFinish(player);
			skillLink.performMajor(player, Math.max(player.getSkillLevel(skillLink) - range, Math.min(player.getSkillLevel(skillLink) + range, skillLevel)), false);
		}
		
		if (!player.getPlayer().isOnline()) {
			return;
		}
		Player p = player.getPlayer().getPlayer();
		
		p.getWorld().playSound(p.getLocation(), WIN_SOUND, 1, 1);
		SUCCESS_EFFECT.play(p, p.getLocation());
		
		double qratio;
		if (offByIndex == 0)
			qratio = 1 + PERFECT_BONUS;
		else {
			qratio = 1.0 / (1.0 + (offByIndex / 5.0));
		}
		input.setQuality(input.getQuality() * qratio);
		
		String name;
		if (input.getItem().getItemMeta() == null || input.getItem().getItemMeta().getDisplayName() == null) {
			name = YamlWriter.toStandardFormat(input.getItem().getType().toString());
		} else {
			name = input.getItem().getItemMeta().getDisplayName();
		}
		
		FancyMessage msg = new FancyMessage(WIN_MESSAGE)
				.color(ChatColor.GREEN)
			.then(input.getItem().getAmount() > 1 ? input.getItem().getAmount() + "x " : "a ")
			.then("[" + name + "]")
				.color(ChatColor.DARK_PURPLE)
				.itemTooltip(input.getItem());
		
		
		msg.send(p);
		if (!(p.getInventory().addItem(input.getItem())).isEmpty()) {
			p.sendMessage(ChatColor.RED + "There is no space left in your inventory");
			p.getWorld().dropItem(p.getEyeLocation(), input.getItem());
		}
	}
	
	private static int getOffbyIndex(double secondOffset) {
		secondOffset = Math.abs(secondOffset);
		
		if (secondOffset <= PERFECT_HIT_THRESHHOLD)
			return 0;
		
		return (int) Math.round(Math.max(1, secondOffset / HIT_OFFBY_INTERVAL));
	}
	
	@EventHandler
	public void onTreeHit(PlayerInteractEvent e) {
		if (state != State.RUNNING)
			return;
		
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		if (!e.getPlayer().getUniqueId().equals(player.getPlayer().getUniqueId()))
			return;
		
		if (e.getItem() == null || !e.getItem().getType().name().contains("AXE"))
			return;
		
		Vector clicked = e.getClickedBlock().getLocation().toVector();
		if (clicked.getBlockX() != treeLocation.getBlockX()
				|| clicked.getBlockZ() != treeLocation.getBlockZ()) 
			return;
		
		//wood chop. 
		doChop(e.getClickedBlock().getLocation(), e.getClickedBlock().getType());
		
	}
	
	private void doChop(Location chopLocation, Material chopData) {
		//check time. Add offby points or fail game
		if (!player.getPlayer().isOnline())
			return;
		
		Player p = player.getPlayer().getPlayer();
		p.getWorld().playSound(p.getLocation(), CLICK_SOUND, 1.1f, 1);
		p.getWorld().playEffect(chopLocation, CHOP_EFFECT, chopData);
		
		double timeDifference;
		timeDifference = Math.abs(secondsRemaining);
		
		if (timeDifference > reactionTime) {
			loseGame();
			return;
		}
		
		int add = getOffbyIndex(timeDifference);
		this.offByIndex += add;
		if (add == 0) {
			p.sendMessage(PERFECT_MESSAGE);
			p.playSound(p.getLocation(), PERFECT_SOUND, 1, 1);
			p.getWorld().playEffect(chopLocation, CHOP_EFFECT, chopData);
			p.getWorld().playEffect(chopLocation, CHOP_EFFECT, chopData);
		}
		
		this.hits--;
		
		if (hits <= 0) {
			winGame();
			return;
		}
		
		setTimer();
		
	}
	
	public static void setSkillLink(LumberjackSkill skill) {
		skillLink = skill;
	}
}
