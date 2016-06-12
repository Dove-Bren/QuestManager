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

import java.util.ListIterator;

import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.SimpleChatMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.ui.menu.message.Message;

import io.puharesource.mc.titlemanager.api.TitleObject;

/**
 * Repairs a player's equipment
 *
 */
public class ForgeAction implements MenuAction {
	
	public enum Repairable {
		SWORD,
		BOW,
		PICKAXE,
		SPADE,
		AXE,
		CHESTPLATE,
		LEGGINGS,
		HELMET,
		BOOTS;
		
		public static boolean isRepairable(Material mat) {
			
			String cname = mat.name();
			for (Repairable rep : Repairable.values()) {
				if (cname.endsWith(rep.name())) {
					return true;
				}
			}
			
			
			return false;
		}
	}

	private int cost;
	
	private QuestPlayer player;
	
	private Message denial;
	
	public ForgeAction(int cost, QuestPlayer player, Message denialMessage) {
		this.cost = cost;
		this.player = player;
		this.denial = denialMessage;
	}

	@Override
	public void onAction() {
		
		//check their money
		if (player.getMoney() >= cost) {
			//they have enough money
			
			//play smith sound, take money,
			//search inventory and find all equipment (:S) and repair it
			//and display a title
			
			
			if (!player.getPlayer().isOnline()) {
				System.out.println("Very bad ForgeAction error!!!!!!!!!!!!!");
				return;
			}
			
			
			
			Player p = player.getPlayer().getPlayer();
			
			Inventory inv = p.getInventory();
			ListIterator<ItemStack> items = inv.iterator();
			
			ItemStack item;
			int count = 0;
			
			while (items.hasNext()) {
				item = items.next();
				if (item == null) {
					continue;
				}
				if (!item.getType().equals(Material.AIR))
				if (Repairable.isRepairable(item.getType()))
				if (item.getDurability() > 0) {
					item.setDurability((short) 0);
					count++;
				}
			}
			
			//also run check over their equipment
			for (ItemStack it : p.getEquipment().getArmorContents()) {
				if (it == null) {
					continue;
				}
				if (!it.getType().equals(Material.AIR))
				if (Repairable.isRepairable(it.getType()))
				if (it.getDurability() > 0) {
					it.setDurability((short) 0);
					count++;
				}
			}
			
			//make sure they had items to repair
			if (count == 0) {
				//no items were repaired!
				ChatMenu whoops = new SimpleChatMenu(
						new FancyMessage("Actually, you don't seem to have any equipment"
								+ " in need of repair!"));
				whoops.show(p);
				return;
			}
			
			player.addMoney(-cost);
			
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
			
			(new TitleObject(ChatColor.GREEN + "Forge",
					ChatColor.BLUE + "" + count + " item(s) have been repaired"))
			.setFadeIn(20).setFadeOut(20).setStay(40).send(p);
		
			
			
		} else {
			//not enough money
			//show them a menu, sorrow
						
			ChatMenu menu = new SimpleChatMenu(denial.getFormattedMessage());
			
			menu.show(player.getPlayer().getPlayer());
		}
		
	}
}
