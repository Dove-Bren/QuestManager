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

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.magic.Imbuement;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;

public class ImbueAction implements MenuAction {
	
	private final String message = ChatColor.DARK_GREEN + "Your imbuement has been applied";
	
	private final Sound sound = Sound.BLOCK_BREWING_STAND_BREW;

	private QuestPlayer player;
	
	private Imbuement imbuement;
	
	public ImbueAction(QuestPlayer player, Imbuement imbuement) {
		this.player = player;
		this.imbuement = imbuement;
	}
	
	@Override
	public void onAction() {
		/*
		 * Apply has just happened. Just need to put it on, actually
		 */
		player.setCurrentImbuement(imbuement);
		
		if (!player.getPlayer().isOnline()) {
			return;
		}
		
		Player p = player.getPlayer().getPlayer();
		
		if (player.getOptions().getOption(PlayerOptions.Key.CHAT_COMBAT_RESULT)) {
			p.sendMessage(message);
		}
		
		p.getWorld().playSound(p.getLocation(), sound, 1, 1);
		
	}
}
