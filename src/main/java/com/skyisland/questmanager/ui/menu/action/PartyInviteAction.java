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

import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.ui.menu.ChatMenuOption;
import com.skyisland.questmanager.ui.menu.message.PlainMessage;
import com.skyisland.questmanager.ui.menu.SimpleChatMenu;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.MultioptionChatMenu;

public class PartyInviteAction implements MenuAction {
	
	private static final String DENY_MESSAGE =
			"That player is already in a party!";
	
	private static final String SAME_MESSAGE =
			"You cannot invite yourself to a party!";
	
	private QuestPlayer leader;
	
	private QuestPlayer other;
	
	public PartyInviteAction(QuestPlayer leader, QuestPlayer other) {
		this.leader = leader;
		this.other = other;
	}
	
	@Override
	public void onAction() {
		
		if (!other.getPlayer().isOnline() || !leader.getPlayer().isOnline()) {
			return;
		}
		
		if (other.getParty() != null) {
			leader.getPlayer().getPlayer().sendMessage(PartyInviteAction.DENY_MESSAGE);
			return;
		}
		
		if (leader.getPlayer().getUniqueId().equals(other.getPlayer().getUniqueId())) {
			leader.getPlayer().getPlayer().sendMessage(PartyInviteAction.SAME_MESSAGE);
			return;
		}
		
		MenuAction join = new JoinPartyAction(leader, other);
		ChatMenuOption joinOpt = new ChatMenuOption(new PlainMessage("Accept"), join);
		MenuAction deny = new ShowChatMenuAction(new SimpleChatMenu(
				new FancyMessage(other.getPlayer().getName())
					.color(ChatColor.DARK_BLUE)
					.then(" refused your invitation.")), leader.getPlayer().getPlayer());
		ChatMenuOption denyOpt = new ChatMenuOption(new PlainMessage("Deny"), deny);
		PlainMessage body = new PlainMessage(
				new FancyMessage(leader.getPlayer().getName())
					.color(ChatColor.DARK_BLUE)
					.then(" invited you to join their party!")
				);
		
		ChatMenu menu = new MultioptionChatMenu(body, joinOpt, denyOpt);
		
		Player op = other.getPlayer().getPlayer();
		menu.show(op);
		op.playNote(op.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
		op.playNote(op.getLocation(), Instrument.PIANO, Note.natural(1, Tone.G));
		op.playNote(op.getLocation(), Instrument.PIANO, Note.natural(1, Tone.E));
		
		ChatMenu myMenu = new SimpleChatMenu(new FancyMessage("Your invitation has been sent."));
		myMenu.show(leader.getPlayer().getPlayer());
	}
}
