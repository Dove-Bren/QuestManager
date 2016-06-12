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

import com.skyisland.questmanager.ui.ChatMenu;

/**
 * Actions that causes a chat menu to be shown to a player
 * @author Skyler
 *
 */
public class ShowChatMenuAction implements MenuAction {

	private Player player;
	
	private ChatMenu menu;
	
	public ShowChatMenuAction(ChatMenu menu, Player player) {
		this.player = player;
		this.menu = menu;
	}
	
	@Override
	public void onAction() {
		menu.show(player);
	}
}
