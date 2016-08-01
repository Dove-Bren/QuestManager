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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.player.QuestPlayer;

/**
 * The action of purchasing an item or service from an NPC.
 * This event specifically details purchases done from within an InventoryMenu, where it has
 * an ItemStack to give to the player
 *
 */
public class PurchaseAction implements MenuAction {
	
	private int cost;
	
	private int fameCheck;
	
	private ItemStack item;
	
	private QuestPlayer player;
	
	private static final String DENIAL_FAME = "Not famous enough!";
	
	private static final String DENIAL_MONEY = "Not enough money!";
	
	private static final String DENIAL_SPACE = "Not enough room in your inventory!";
	
	public PurchaseAction(QuestPlayer player, ItemStack item, int cost, int fameRequirement) {
		this.player = player;
		this.item = item;
		this.cost = cost;
		this.fameCheck = fameRequirement;
	}
	
	@Override
	public void onAction() {
		//check if they have enough fame and money. 
		// If they do, give them the item and subtract the cost
		// If they don't don't give them the item and tell them off those noobs
		if (!player.getPlayer().isOnline()) {
			return;
			//something fishy happened...
		}
		
		Player p = player.getPlayer().getPlayer();
		
		//if (player.getFame() < fameCheck) {
		if (player.getAlphaFame() < fameCheck) {
			p.sendMessage(DENIAL_FAME);
			return;
		}
		if (player.getMoney() < cost) {
			p.sendMessage(DENIAL_MONEY);
			return;			
		}
		
		//make sure there's room in their inventory
		if (p.getInventory().firstEmpty() == -1) {
			p.sendMessage(DENIAL_SPACE);
			return;			
		}
		
		//everything's good, so throw it in!
		p.getInventory().addItem(item);
		player.addMoney(-cost);
	}
}
