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

package com.skyisland.questmanager.player.utils;

import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.QuestPlayer;

/**
 * Holds methods to interface with a quest player's compass.
 *
 */
public class Compass {
	
	private static final Vector RESET_VECTOR = new Vector(0,0,-99999);
	
	public static class CompassDefinition {
		
		private static Material compassType = Material.COMPASS;
		
		private static String displayName = "Magic Compass";
		
		private static Enchantment enchant = Enchantment.ARROW_INFINITE;
		
		public static boolean isCompass(ItemStack item) {
			if (item == null || item.getType() != compassType || !item.hasItemMeta()) {
				return false;
			}
			
			if (!displayName.equals(item.getItemMeta().getDisplayName())) {
				return false;
			}

			return item.containsEnchantment(enchant);
		}

		public static void setCompassType(Material compassType) {
			CompassDefinition.compassType = compassType;
		}

		public static void setDisplayName(String displayName) {
			CompassDefinition.displayName = displayName;
		}

		public static void setEnchant(Enchantment enchant) {
			CompassDefinition.enchant = enchant;
		}
		
	}
	
	public static void updateCompass(QuestPlayer qp, boolean silent) {
		if (!qp.getPlayer().isOnline()) {
			return;
		}
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getCompassEnabled()) {
			return;
		}
		
		if (!hasCompass(qp.getPlayer().getPlayer().getInventory())) {
			return;
		}
		
		Player player = qp.getPlayer().getPlayer();
		
		Location targ = qp.getCompassTarget();
		if (targ == null) {
			player.setCompassTarget(player.getWorld().getBlockAt(0, 0, 0).getLocation().add(RESET_VECTOR));
		} else {
			player.setCompassTarget(qp.getCompassTarget());
		}
		
		if (!silent) {
			player.sendMessage(ChatColor.GRAY + "Your compass has been updated" + ChatColor.RESET);
			player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(0, Tone.E));
			player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(0, Tone.G));
			player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(0, Tone.B));
		}
	}
	
	private static boolean hasCompass(Inventory inv) {
		for (ItemStack item : inv) {
			if (CompassDefinition.isCompass(item)) {
				return true;
			}
		}
		
		return false;
	}
}
