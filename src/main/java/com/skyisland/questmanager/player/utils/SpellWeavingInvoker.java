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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.QuestPlayer;


/**
 * Used to trigger invocation of spell weaving spells
 * @author Skyler
 *
 */
public class SpellWeavingInvoker implements Listener {
	
	public static class InvokerDefinition {
		
		private static Material invokerType = Material.CARROT_STICK;
		
		private static String displayName = "Spell Invoker";
		
		private static Enchantment enchant = Enchantment.ARROW_DAMAGE;
		
		public static boolean isInvoker(ItemStack item) {
			if (item == null || item.getType() != invokerType || !item.hasItemMeta()) {
				return false;
			}
			
			if (!displayName.equals(item.getItemMeta().getDisplayName())) {
				return false;
			}

			return item.containsEnchantment(enchant);
		}

		public static void setInvokerType(Material invokerType) {
			InvokerDefinition.invokerType = invokerType;
		}

		public static void setDisplayName(String displayName) {
			InvokerDefinition.displayName = displayName;
		}

		public static void setEnchant(Enchantment enchant) {
			InvokerDefinition.enchant = enchant;
		}
		
	}
	
	public SpellWeavingInvoker() {
		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getAllowSpellWeaving() 
				&& QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getUseWeavingInvoker()) {
			Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
		}
	}
	
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent e) {
		if (!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			return; //not right click
		}
		
		if (!InvokerDefinition.isInvoker(e.getItem())) {
			return;
		}
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getWorld().getName())) {
			return;
		}
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(e.getPlayer());
		
		qp.castSpellWeavingSpell();
		
	}
}
