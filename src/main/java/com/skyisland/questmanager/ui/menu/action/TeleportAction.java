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

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.SimpleChatMenu;
import com.skyisland.questmanager.ui.menu.message.Message;

/**
 * Ferries a player
 *
 */
public class TeleportAction implements MenuAction {

	private int cost;
	
	private Location destination;
	
	private QuestPlayer player;
	
	private Message denial;
	
	public TeleportAction(int cost, Location destination, QuestPlayer player, Message denialMessage) {
		this.cost = cost;
		this.player = player;
		this.denial = denialMessage;
		this.destination = destination;
	}
	
	@Override
	public void onAction() {
		
		//check their money
		if (player.getMoney() >= cost) {
			//they have enough money
			
			//blindness for some time, but just teleportation & particles!
			
			if (!player.getPlayer().isOnline()) {
				System.out.println("Very bad TeleportAction error!!!!!!!!!!!!!");
				return;
			}
			
			player.addMoney(-cost);
			
			Player p = player.getPlayer().getPlayer();
			
			p.addPotionEffect(
					new PotionEffect(PotionEffectType.BLINDNESS, 60, 5));
			
			p.teleport(destination);
			destination.getWorld().playEffect(destination, Effect.STEP_SOUND, 0);
			
			
		} else {
			//not enough money
			//show them a menu, sorrow
						
			ChatMenu menu = new SimpleChatMenu(denial.getFormattedMessage());
			
			menu.show(player.getPlayer().getPlayer(), null);
		}
	}
}
