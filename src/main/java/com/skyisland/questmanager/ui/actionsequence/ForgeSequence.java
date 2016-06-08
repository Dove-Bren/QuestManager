package com.skyisland.questmanager.ui.actionsequence;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.defaults.SmithingSkill;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;

public class ForgeSequence implements Listener, Alarmable<Integer> {
	
	private enum State {
		STOPPED,
		STARTING,
		RUNNING,
		FINISHED
	}

	private static final Random random = new Random();
	
	private static final String missMessage = ChatColor.RED + "Your hammer didn't quite hit the metal right... That one didn't count";
	
	private static final String cutMessage = ChatColor.DARK_GRAY + "You cut the hot metal";
	
	private static final String quelchMessage = ChatColor.DARK_GRAY + "You quelch the burning metal, making it hard and brittle.";
	
	private static final String alreadyCutMessage = ChatColor.YELLOW + "The blade is already cut!";
	
	private static final String wrongToolMessage = ChatColor.RED + "You dropped your tool! Switch back quickly!";
	
	private static final String wrongToolTimeoutMessage = ChatColor.YELLOW + "The metal cooled and cast while you fumbled for your tool";
	
	private static final String notHotMessage = ChatColor.YELLOW + "You cannot hammer the metal when it is not hot";
	
	private static final Sound notHotSound = Sound.ENTITY_ITEM_BREAK;
	
	private static final Sound startSound = Sound.BLOCK_FURNACE_FIRE_CRACKLE;
	
	private static final Sound heatSound = Sound.BLOCK_LAVA_AMBIENT;
	
	private static final Sound quelchSound = Sound.BLOCK_LAVA_EXTINGUISH;
	
	private static final Sound cutSound = Sound.ENTITY_SHEEP_SHEAR;
	
	private static final Sound countdownSound = Sound.BLOCK_NOTE_PLING;
	
	private static final Sound missSound = Sound.ENTITY_ENDERDRAGON_FLAP;
	
	private static final Sound hitSound = Sound.BLOCK_ANVIL_PLACE;
	
	private static final double timeStep = 0.05;
	
	private static final int wrongToolTimeout = 3;
	
	private static final int startupTime = 3;
	
	private State state;
	
	private QuestPlayer player;
	
	private List<ItemStack> inputs;
	
	private double heatTime;
	
	private double coolTime;
	
	private double hitChance;
	
	private int hammerHits;
	
	private boolean cut;
	
	private boolean quelch;
	
	private boolean isCooling;
	
	private boolean isWrongTool;
	
	private double timeLeft;
	
	private double wrongItemTimeLeft;
	
	private BossBar displayBar;
	
	private String displayName;
	
	private SmithingSkill skill;
	
	public ForgeSequence(SmithingSkill skill, QuestPlayer player, String display, List<ItemStack> inputs,
			double heatTime, double coolTime, double hitChance) {
		this.skill = skill;
		this.player = player;
		this.inputs = inputs;
		this.displayName = display;
		this.heatTime = heatTime;
		this.coolTime = coolTime;
		this.hitChance = hitChance;
		
		this.state = State.STOPPED;
		
		this.isCooling = false;
		this.isWrongTool = false;
		this.cut = false;
		this.quelch = false;
		this.hammerHits = 0;
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
		
		this.timeLeft = coolTime;
		isCooling = true;
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	@Override
	public void alarm(Integer key) {
		
		if (!player.getPlayer().isOnline()) {
			return;
		}

		Player p = player.getPlayer().getPlayer();
		if (state == State.STARTING) {
			//starting countdown
			if (key >= startupTime) {
				player.getPlayer().getPlayer().sendMessage(ChatColor.RED + "Go!");
				state = State.RUNNING;
				p.playSound(p.getLocation(), startSound, 1, 1);
				this.isCooling = true;
				Alarm.getScheduler().schedule(this, 0, timeStep);
				return;
			}
			
			p.sendMessage(ChatColor.YELLOW + "" + (startupTime - key));
			p.playSound(p.getLocation(), countdownSound, 1, 1);
			Alarm.getScheduler().schedule(this, key + 1, 1);
			return;
		}
		
		if (state == State.RUNNING) {
			
			if (isWrongTool) {
				this.wrongItemTimeLeft -= timeStep;
				if (wrongItemTimeLeft <= 0) {
					player.getPlayer().getPlayer().sendMessage(wrongToolTimeoutMessage);
					finishGame();
				}
			}
			
			
			this.timeLeft -= timeStep;
			
			if (timeLeft <= 0) {
				if (!isCooling) {
					isCooling = true;
					timeLeft = coolTime;
				} else {
					finishGame();
				}
			}
			
			update();
			Alarm.getScheduler().schedule(this, 0, timeStep);
		}
	}
	
	private void update() {
		if (this.state != State.RUNNING) {
			return;
		}
		
		if (isCooling) {
			displayBar.setColor(BarColor.BLUE);
			displayBar.setProgress(1.0 - (timeLeft / coolTime));
			
			if (timeLeft <= 0) {
				finishGame();
				return;
			}
		} else {
			displayBar.setColor(BarColor.RED);
			displayBar.setProgress(timeLeft / heatTime);
		}
	}
	
	private void finishGame() {
		this.state = State.FINISHED;
		Alarm.getScheduler().unregister(this);
		
		displayBar.removeAll();
		
		skill.submitJob(inputs, hammerHits, cut, quelch);
		skill.playerFinish(player);
		
		HandlerList.unregisterAll(this);
		
	}
	
	@EventHandler
	public void onHammerSwing(PlayerInteractEvent e) {
		if (state != State.RUNNING)
			return;
		
		if (isWrongTool)
			return;
		
		if (e.getHand() == EquipmentSlot.OFF_HAND)
			return;
		
		if (!e.getPlayer().getUniqueId().equals(player.getPlayer().getUniqueId()))
			return;
		
		e.setCancelled(true);
		
		if (skill.isAnvil(e.getClickedBlock())) {
			doAnvilClick(e);
			return;
		}
		
		if (skill.isCutter(e.getClickedBlock())) {
			doCutClick(e);
			return;
		}
		
		Set<Material> s = null;
		List<Block> sight = e.getPlayer().getLineOfSight(s, 4);
		for (Block block : sight) {
			if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
				doLavaClick(e);
				return;
			}
			if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
				doWaterClick(e);
				return;
			}
		}
	}
	
	private void doLavaClick(PlayerInteractEvent e) {
		if (cut) {
			e.getPlayer().sendMessage(alreadyCutMessage);
			return;
		}
		
		this.timeLeft = heatTime;
		this.isCooling = false;
		e.getPlayer().playSound(e.getPlayer().getLocation(), heatSound, 1, 1);
		update();
	}
	
	private void doAnvilClick(PlayerInteractEvent e) {
		if (cut) {
			e.getPlayer().sendMessage(alreadyCutMessage);
			return;
		}
		
		if (isCooling) {
			e.getPlayer().sendMessage(notHotMessage);
			e.getPlayer().playSound(e.getPlayer().getLocation(), notHotSound, 1, 1);
			return;
		}
		
		if (random.nextDouble() < hitChance) {
			e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), hitSound, 1, 1);
			this.hammerHits++;
			this.timeLeft *= .8;
		} else {
			e.getPlayer().sendMessage(missMessage);
			e.getPlayer().playSound(e.getPlayer().getLocation(), missSound, 1, 1);
		}
	}
	
	private void doCutClick(PlayerInteractEvent e) {
		if (cut) {
			e.getPlayer().sendMessage(alreadyCutMessage);
			return;
		}
		
		cut = true;
		e.getPlayer().sendMessage(cutMessage);
		e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), cutSound, 1, 1);
	}
	
	private void doWaterClick(PlayerInteractEvent e) {
		quelch = true;
		e.getPlayer().sendMessage(quelchMessage);
		e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), quelchSound, 1, 1);
		
		finishGame();		
	}
	
	@EventHandler
	public void onToolSwap(PlayerItemHeldEvent e) {
		if (!e.getPlayer().getUniqueId().equals(player.getPlayer().getUniqueId())) {
			return;
		}
		
		if (!isWrongTool && !skill.isTool(e.getPlayer().getInventory().getItem(e.getNewSlot()))) {
			this.isWrongTool = true;
			e.getPlayer().sendMessage(wrongToolMessage);
			this.wrongItemTimeLeft = wrongToolTimeout;
			return;
		}
		
		if (skill.isTool(e.getPlayer().getInventory().getItem(e.getNewSlot()))) {
			this.isWrongTool = false;
			return;
		}
	}
}
