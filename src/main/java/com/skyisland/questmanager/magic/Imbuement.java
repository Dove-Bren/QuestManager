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

package com.skyisland.questmanager.magic;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.magic.spell.effect.ImbuementEffect;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.defaults.ImbuementSkill;
import com.skyisland.questmanager.player.skill.event.CombatEvent;

/**
 * An active imbuement. Watches for events, takes action, fights crime, you name it! 
 * @see QuestPlayer
 * @see ImbuementHandler
 */
public class Imbuement implements Listener {
	
	public static final Sound defaultSlashSound = Sound.BLOCK_FENCE_GATE_CLOSE;
	
	public static final String fadeMessage = ChatColor.DARK_GRAY + "Your imbuement fades as you swap items";
	
	private Set<ImbuementEffect> effects;
	
	private QuestPlayer player;
	
	private Sound sound;
	
	private double cost;
	
	public Imbuement(QuestPlayer player, Set<ImbuementEffect> effects, double cost, Sound hitSound) {
		if (effects == null || player == null || effects.isEmpty()) {
			return;
		}
		
		this.player = player;
		this.effects = effects;
		this.sound = hitSound;
		this.cost = cost;
	}
	
	public void start() {
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	public Imbuement(QuestPlayer player, Set<ImbuementEffect> effects, double cost) {
		this(player, effects, cost, defaultSlashSound);
	}
	
	/**
	 * Cancels this imbuement. It will no longer watch for events and is ready to be nulled and freed
	 */
	public void cancel() {
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerCombat(CombatEvent e) {
		if (e.isMiss()) {
			return;
		}
		
		if (!player.getPlayer().isOnline()) {
			return;
		}
		
		if (!e.getPlayer().equals(player)) {
			return;
		}
		
		if (!player.getPlayer().getPlayer().getGameMode().equals(GameMode.CREATIVE) 
				&& player.getMP() < cost) {
			player.getPlayer().getPlayer().playSound(player.getPlayer().getPlayer().getLocation(), Sound.BLOCK_WATERLILY_PLACE, 1.0f, 0.5f);
			return;
		}
		
		ImbuementSkill skill = QuestManagerPlugin.questManagerPlugin.getImbuementHandler().getImbuementSkill();
		
		if (skill != null) {
			skill.performMinor(player, player.getSkillLevel(skill), true);
		}
		
		player.addMP(-cost);
		
		
		for (ImbuementEffect effect : effects) {
			effect.apply(e.getTarget(), e.getPlayer());
		}
		
		player.getPlayer().getPlayer().getWorld().playSound(e.getTarget().getLocation(), sound, 1, 1);
	}
	
	@EventHandler
	public void onWeaponSwitch(PlayerItemHeldEvent e) {
		if (!player.getPlayer().isOnline()) {
			return;
		}
		
		if (!e.getPlayer().getUniqueId().equals(player.getPlayer().getUniqueId())) {
			return;
		}
		
		if (!(QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getWorld().getName()))) {
			return;
		}
		
		//not allowed to switch weapons, cheater!
		this.cancel();
		e.getPlayer().sendMessage(fadeMessage);
	}
}
