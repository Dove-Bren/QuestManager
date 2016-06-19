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

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.player.Party;
import com.skyisland.questmanager.player.QuestPlayer;

/**
 * Adds a player to another player's party, creating it if it doesn't exist
 *
 */
public class JoinPartyAction implements MenuAction {
	
	private QuestPlayer leader;
	
	private QuestPlayer other;
	
	public JoinPartyAction(QuestPlayer leader, QuestPlayer other) {
		this.leader = leader;
		this.other = other;
	}
	
	@Override
	public void onAction() {
		// TODO Auto-generated method stub
		if (leader.getParty() == null) {
			Party party = leader.createParty();
			other.joinParty(party);
		} else {
			other.joinParty(leader.getParty());
		}
		Player p = other.getPlayer().getPlayer();
		p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
		p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.G));
		p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.E));
	}
}
