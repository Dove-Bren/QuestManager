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

import com.skyisland.questmanager.effects.ChargeEffect;
import com.skyisland.questmanager.player.QuestPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import io.puharesource.mc.titlemanager.api.TitleObject;

/**
 * Levels up a player, awarding them some amount of mana
 * @author Skyler
 *
 */
public class LevelupManaAction implements MenuAction {
	
	private int cost;
	
	private int manaAmount;
	
	private QuestPlayer player;
	
	private static final String denialFame = "You do not have enough fame...";
	
	public LevelupManaAction(QuestPlayer player, int cost, int manaAmount) {
		this.player = player;
		this.cost = cost;
		this.manaAmount = manaAmount;
	}
	
	@Override
	public void onAction() {
		//check if they have enough fame
		if (!player.getPlayer().isOnline()) {
			return;
		}
		
		Player p = player.getPlayer().getPlayer();
		
		if (player.getFame() < cost) {
			p.sendMessage(denialFame);
			return;
		}
		
		//level them up
		player.levelUp(0, manaAmount);
		player.addFame(-cost);
		
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
		ChargeEffect ef = new ChargeEffect(Effect.WITCH_MAGIC);
		ef.play(p, p.getLocation());
		
		(new TitleObject(ChatColor.GREEN + "Level Up",
				ChatColor.BLUE + "Your maximum mana has been increased"))
		.setFadeIn(20).setFadeOut(20).setStay(40).send(p);
		
	}
}
