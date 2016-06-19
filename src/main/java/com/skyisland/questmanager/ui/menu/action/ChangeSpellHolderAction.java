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

package com.skyisland.questmanager.ui.menu.action;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.magic.spell.Spell;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.utils.SpellHolder;
import com.skyisland.questmanager.ui.menu.SimpleChatMenu;

public class ChangeSpellHolderAction implements MenuAction {

	private String newSpell;
	
	private QuestPlayer player;
	
	private ItemStack holder;
	
	public ChangeSpellHolderAction(QuestPlayer player, ItemStack holder, String newSpell) {
		this.newSpell = newSpell;
		this.holder = holder;
		this.player = player;
	}
	
	@Override
	public void onAction() {
		player.getStoredSpells().put(holder.getType(), newSpell);
		
		if (player.getPlayer().isOnline()) {
			new SimpleChatMenu(
					new FancyMessage("You successfully changed your spell")
						.color(ChatColor.GREEN))
			.show(player.getPlayer().getPlayer());
			
			ItemStack swapHolder = null;
			int slot;
			Inventory inv = player.getPlayer().getPlayer().getInventory();
			for (slot = 0; slot <= 35; slot++) {
				ItemStack item = inv.getItem(slot);
				if (item == null || item.getType() == Material.AIR) {
					continue;
				}
				if (SpellHolder.SpellHolderDefinition.isHolder(item)
						&& item.getType() == holder.getType()) {
					swapHolder = item;
					break;
				}
			}
			
			if (swapHolder == null) {
				//unable to find it!
				return;
			}
			
			String desc = "No Description";
			Spell s = QuestManagerPlugin.questManagerPlugin.getSpellManager().getSpell(newSpell);
			if (s != null) {
				desc = s.getDescription();
			}
			ItemMeta meta = holder.getItemMeta();
			List<String> descList = new LinkedList<>();
			String mid = "";
			for (int i = 0; i < 15 - (newSpell.length() / 2); i++) {
				mid = mid + " ";
			}
			descList.add(mid + ChatColor.DARK_RED + newSpell);
			if (s != null) {
				descList.add(
						ChatColor.BLUE + "Cost: " + s.getCost()
						);
			}
			int pos;
			while (desc.length() > 30) {
				
				desc = ChatColor.GOLD + desc.trim();
				
				//find first space before 30
				mid = desc.substring(0, 30);
				pos = mid.lastIndexOf(" ");
				if (pos == -1) {
					descList.add(mid);
					desc = desc.substring(30);
					continue;
				}
				//else we found a space
				descList.add(mid.substring(0, pos));
				desc = desc.substring(pos);
			}
			
			descList.add(ChatColor.GOLD + desc.trim());	
			meta.setLore(descList);
			
			
			holder.setItemMeta(meta);
			
			inv.setItem(slot, holder);
			
		}
	}
}
