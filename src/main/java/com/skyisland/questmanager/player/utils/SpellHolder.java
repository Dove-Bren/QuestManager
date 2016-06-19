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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.magic.spell.Spell;
import com.skyisland.questmanager.player.QuestPlayer;


public class SpellHolder {

	public static class SpellHolderDefinition {
		
		private static String displayName = "Spell Scroll";
		
		private static Enchantment enchant = Enchantment.ARROW_DAMAGE;
		
		public static boolean isHolder(ItemStack item) {
			if (item == null || !item.getType().name().contains("RECORD") || !item.hasItemMeta()) {
				return false;
			}
			
			if (!displayName.equals(item.getItemMeta().getDisplayName())) {
				return false;
			}

			return item.containsEnchantment(enchant);
		}

		public static void setDisplayName(String displayName) {
			SpellHolderDefinition.displayName = displayName;
		}

		public static void setEnchant(Enchantment enchant) {
			SpellHolderDefinition.enchant = enchant;
		}
		
	}
	
	public static class SpellAlterTableDefinition {
		
		private static Material blockType = Material.ENCHANTMENT_TABLE;
		
		public static boolean isTable(Block block) {
			return !(block == null || block.getType() != blockType);
		}

		public static void setBlockType(Material blockType) {
			SpellAlterTableDefinition.blockType = blockType;
		}
		
	}
	
	/**
	 * Tries to lookup the spell stored with the provided spell holder.
	 * If the item passed has no associated spell, <i>null</i> is returned.
	 * @return The spell that is associated with the holder, or null if there is none
	 */
	public static Spell getSpell(QuestPlayer player, ItemStack holder) {
		if (player == null || holder == null) {
			return null;
		}
		
		return QuestManagerPlugin.questManagerPlugin.getSpellManager().getSpell
			(player.getStoredSpells().get(holder.getType()));
	}
}
