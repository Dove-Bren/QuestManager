package com.SkyIsland.QuestManager.Player.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.Magic.Spell.Spell;
import com.SkyIsland.QuestManager.Player.QuestPlayer;


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
			
			if (!item.containsEnchantment(enchant)) {
				return false;
			}
			
			return true;
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
			if (block == null || block.getType() != blockType) {
				return false;
			}
			
			return true;
		}

		public static void setBlockType(Material blockType) {
			SpellAlterTableDefinition.blockType = blockType;
		}
		
	}
	
	/**
	 * Tries to lookup the spell stored with the provided spell holder.<br />
	 * If the item passed has no associated spell, <i>null</i> is returned.
	 * @param player 
	 * @param holder 
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
