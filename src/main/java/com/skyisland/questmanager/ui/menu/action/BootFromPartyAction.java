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

import com.skyisland.questmanager.player.Party;
import org.bukkit.ChatColor;

import com.skyisland.questmanager.player.QuestPlayer;

/**
 * Boots a player from a party
 *
 */
public class BootFromPartyAction implements MenuAction {
	
	private Party party;
	
	private QuestPlayer other;
	
	public BootFromPartyAction(Party party, QuestPlayer other) {
		this.party = party;
		this.other = other;
	}
	
	@Override
	public void onAction() {
		party.removePlayer(other, ChatColor.DARK_RED + "You've been kicked from the party" + ChatColor.RESET);
	}
}
