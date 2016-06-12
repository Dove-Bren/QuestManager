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
import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.SimpleChatMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.skyisland.questmanager.ui.menu.message.Message;

import io.puharesource.mc.titlemanager.api.TitleObject;

/**
 * Rests a player, restoring health and hunger
 *
 */
public class InnAction implements MenuAction {

	private int cost;
	
	private QuestPlayer player;
	
	private Message denial;
	
	public InnAction(int cost, QuestPlayer player, Message denialMessage) {
		this.cost = cost;
		this.player = player;
		this.denial = denialMessage;
	}
	
	@Override
	public void onAction() {
		
		//check their money
		if (player.getMoney() >= cost) {
			//they have enough money
			
			//blindness for 3 seconds, title saying you're now rested?
			//don't forget to restore health, hunger
			//and take out some money
			
			if (!player.getPlayer().isOnline()) {
				System.out.println("Very bad InnAction error!!!!!!!!!!!!!");
				return;
			}
			
			player.addMoney(-cost);
			
			Player p = player.getPlayer().getPlayer();
			double amount = p.getMaxHealth() - p.getHealth();
			EntityRegainHealthEvent e = new EntityRegainHealthEvent(p, amount, RegainReason.CUSTOM);
			Bukkit.getPluginManager().callEvent(e);
			if (!e.isCancelled()) {
				p.setHealth(p.getMaxHealth());
			}
			
			player.regenMP(-100);
			
			p.setFoodLevel(20);
			p.setExhaustion(0f);
			p.setSaturation(20f);
			
			p.addPotionEffect(
					new PotionEffect(PotionEffectType.BLINDNESS, 60, 5));


			new TitleObject(ChatColor.GREEN + "Sweet Dreams" + ChatColor.RESET,
					ChatColor.BLUE + "Health and hunger have been restored")
					.setFadeIn(20).setFadeOut(20).setStay(40).send(p);
	        
	        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, .5f);
			
		} else {
			//not enough money
			//show them a menu, sorrow
						
			ChatMenu menu = new SimpleChatMenu(denial.getFormattedMessage());
			
			menu.show(player.getPlayer().getPlayer());
		}
		
	}
}
