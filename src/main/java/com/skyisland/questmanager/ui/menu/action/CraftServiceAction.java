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

import com.skyisland.questmanager.npc.utils.ServiceCraft;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.SimpleChatMenu;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.message.Message;

import io.puharesource.mc.titlemanager.api.TitleObject;

/**
 * Takes a list of requirements and a fee and produces results
 * @author Skyler
 *
 */
public class CraftServiceAction implements MenuAction {
	
	private ServiceCraft trade;
	
	private QuestPlayer player;
	
	private Message denial;
	
	public CraftServiceAction(ServiceCraft trade, QuestPlayer player, Message denialMessage) {
		this.trade = trade;
		this.player = player;
		this.denial = denialMessage;
	}

	@Override
	public void onAction() {
		if (!player.getPlayer().isOnline()) {
			System.out.println("Very bad Service error!!!!!!!!!!!!!");
			return;
		}
		//check their money
		if (player.getMoney() >= trade.getCost()) {
			//they have enough money
			
			Player p = player.getPlayer().getPlayer();
			
			//check if they have the required items
			boolean pass = true;
			for (ItemStack req : trade.getRequired()) {
				if (!player.hasItem(req)) {
					pass = false;
					break;
				}
			}
			
			if (!pass) {
				deny();
				return;
			}
			
			//had money, had items
			
			//play smith sound, take money,
			//deduct required items
			//give new item
			
			for (ItemStack req : trade.getRequired()) {
				player.removeItem(req);
			}
			player.addMoney(-trade.getCost());
			
			p.getInventory().addItem(trade.getResult());
			
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
			
			(new TitleObject("", ChatColor.GOLD + "Item Crafted"))
				.setFadeIn(20).setFadeOut(20).setStay(40).send(p);
			
			
			
			
		} else {
			//not enough money
			//show them a menu, sorrow
						
			deny();
		}
		
	}
	
	private void deny() {
		ChatMenu menu = new SimpleChatMenu(denial.getFormattedMessage());
		
		menu.show(player.getPlayer().getPlayer());
	}
}
