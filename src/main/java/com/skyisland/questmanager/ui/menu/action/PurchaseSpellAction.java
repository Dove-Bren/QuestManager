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

import com.skyisland.questmanager.player.QuestPlayer;
import org.bukkit.entity.Player;

/**
 * The action of purchasing an item or service from an NPC.
 * This event specifically details purchases done from within an InventoryMenu, where it has
 * an ItemStack to give to the player
 *
 */
public class PurchaseSpellAction implements MenuAction {
	
	private int cost;
	
	private int fameCheck;
	
	private String spell;
	
	private QuestPlayer player;
	
	private static final String denialFame = "Not famous enough!";
	
	private static final String denialMoney = "Not enough money!";
	
	private static final String denialExists = "You've already learned this spell!";
	
	public PurchaseSpellAction(QuestPlayer player, String spellName, int cost, int fameRequirement) {
		this.player = player;
		this.spell = spellName;
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
		
		if (player.getFame() < fameCheck) {
			p.sendMessage(denialFame);
			return;
		}
		if (player.getMoney() < cost) {
			p.sendMessage(denialMoney);
			return;			
		}
		
		//make sure they don't already have it
		
		if (player.getSpells().contains(spell)) {
			p.sendMessage(denialExists);
			return;
		}
		
		//everything's good, so throw it in!
		player.addSpell(spell);
		player.addMoney(-cost);
	}
}
