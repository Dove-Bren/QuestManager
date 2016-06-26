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
import org.bukkit.inventory.ItemStack;

/**
 * Holds info and event handling for a recaller object.
 * @author Skyler
 *
 */
public class Recaller {

	public static class RecallerDefinition {
		
		private static String displayName = "Recaller";
		
		private static Material type = Material.BOOK;
		
		public static boolean isHolder(ItemStack item) {
			if (item == null || item.getType() != type || !item.hasItemMeta()) {
				return false;
			}
			
			return displayName.equals(item.getItemMeta().getDisplayName());
		}

		public static void setDisplayName(String displayName) {
			RecallerDefinition.displayName = displayName;
		}

		public static void setType(Material type) {
			RecallerDefinition.type = type;
		}
		
	}
	
	public static class MarkerDefinition {
		private static Material type = Material.BEACON;
		
		public static boolean isMarker(Block block) {
			return (block != null && block.getType() == type);
		}

		public static void setType(Material type) {
			MarkerDefinition.type = type;
		}
	}
}
