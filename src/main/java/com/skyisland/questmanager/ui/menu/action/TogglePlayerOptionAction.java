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

import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;

/**
 * Toggles a specific player option
 *
 */
public class TogglePlayerOptionAction implements MenuAction {
	
	private QuestPlayer player;
	
	private PlayerOptions.Key key;
	
	private static final String RESULT_MESSAGE = ChatColor.DARK_GRAY + "[%s] %s is now set to %s";
	
	public TogglePlayerOptionAction(QuestPlayer player, PlayerOptions.Key key) {
		this.player = player;
		this.key = key;
	}
	
	@Override
	public void onAction() {
		//Set the player option. Then update icon
		
		if (!player.getPlayer().isOnline()) {
			return;
			//something fishy happened...
		}
		
		player.getOptions().setOption(key, !player.getOptions().getOption(key));
		
		//do another lookup to ensure reported is correct
		boolean ret = player.getOptions().getOption(key);
		
		player.getPlayer().getPlayer().sendMessage(String.format(RESULT_MESSAGE,
				(ret ? ChatColor.DARK_GREEN + "on" + ChatColor.DARK_GRAY : ChatColor.DARK_RED + "off" + ChatColor.DARK_GRAY),
				YamlWriter.toStandardFormat(key.name()),
				(ret ? "on" : "off")
				));
	}
}
