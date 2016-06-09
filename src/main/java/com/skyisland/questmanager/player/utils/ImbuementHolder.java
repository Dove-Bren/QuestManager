package com.skyisland.questmanager.player.utils;

import com.skyisland.questmanager.magic.ImbuementSet;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.player.QuestPlayer;

public class ImbuementHolder {

	public static class ImbuementHolderDefinition {
		
		private static String displayName = "Imbuement Charm";
		
		private static Enchantment enchant = Enchantment.ARROW_DAMAGE;
		
		private static Material type = Material.STAINED_GLASS_PANE;
		
		public static boolean isHolder(ItemStack item) {
			if (item == null || item.getType() != type || !item.hasItemMeta()) {
				return false;
			}
			
			if (!displayName.equals(item.getItemMeta().getDisplayName())) {
				return false;
			}

			return item.containsEnchantment(enchant);
		}

		public static void setDisplayName(String displayName) {
			ImbuementHolderDefinition.displayName = displayName;
		}

		public static void setEnchant(Enchantment enchant) {
			ImbuementHolderDefinition.enchant = enchant;
		}
		
		public static void setType(Material type) {
			ImbuementHolderDefinition.type = type;
		}
		
	}
	
	public static class ImbuementAlterTableDefinition {
		
		private static Material blockType = Material.ENDER_PORTAL_FRAME;
		
		public static boolean isTable(Block block) {
			if (block == null || block.getType() != blockType) {
				return false;
			}
			
			return true;
		}

		public static void setBlockType(Material blockType) {
			ImbuementAlterTableDefinition.blockType = blockType;
		}
		
	}
	
	/**
	 * Tries to lookup the imbuement stored with the provided spell holder.
	 * If the item passed has no associated spell, <i>null</i> is returned.
	 * @return The imbuement that is associated with the holder, or null if there is none
	 */
	public static ImbuementSet getImbuement(QuestPlayer player, ItemStack holder) {
		if (player == null || holder == null) {
			return null;
		}
		
		return player.getStoredImbuement(holder.getDurability());
		
	}
}
