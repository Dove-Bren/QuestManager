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
package com.skyisland.questmanager.party;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.event.PartyDisbandEvent;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ListIterator;
import java.util.Optional;
import java.util.UUID;

public class PartyManager {

	public static int maxSize = 4;

	private PartyManager(){}

	public static void createAndRegisterListeners(){
		new PartyEventListener();
	}

	public static Party createParty(QuestPlayer leader){
		Party party = new Party(leader);
		PartyRepository.put(party.getId(), party);
		return party;
	}

	public static Optional<Party> getParty(UUID uuid) {
		return PartyRepository.get(uuid);
	}

	private static void updateScore(Player player, Objective side) {
		side.getScore(player.getPlayer().getName()).setScore((int) player.getPlayer().getPlayer().getHealth());
	}

	public static void updateScoreboard(Party party) {

		QuestPlayer leader = party.getLeader();

		if (leader == null) {
			return;
		}

		Scoreboard partyBoard = party.getPartyBoard();

		if (leader.getPlayer().isOnline()) {
			leader.getPlayer().getPlayer().setScoreboard(partyBoard);
		}

		party.getMembers().stream()
			.map(QuestPlayer::getPlayer)
			.filter(OfflinePlayer::isOnline)
			.map(OfflinePlayer::getPlayer)
			.forEach(player -> player.setScoreboard(partyBoard));

		//now that everyone's registered, let's update health
		Objective side = party.getPartyBoard().getObjective(DisplaySlot.SIDEBAR);
		if (leader.getPlayer().isOnline()) {
			updateScore(leader.getPlayer().getPlayer(), side);
		}

		party.getMembers().stream()
			.map(QuestPlayer::getPlayer)
			.filter(OfflinePlayer::isOnline)
			.map(OfflinePlayer::getPlayer)
			.forEach(player -> updateScore(player, side));

	}

	/**
	 * Updates the scoreboard to reflect the given score for the given player.
	 */
	public static void updateScoreboard(Party party, QuestPlayer player, int score) {
		if (!party.getLeader().getIDString().equals(player.getIDString()) && !party.getMembers().contains(player)) {
			System.out.println("Not found in party!");
			return;
		}

		Objective side = party.getPartyBoard().getObjective(DisplaySlot.SIDEBAR);
		side.getScore(player.getPlayer().getName()).setScore(score);
	}

	/**
	 * Adds the player to the party, returning true if successful. If the player cannot be added,
	 * false is returned instead.
	 * @return true if successful
	 */
	@SuppressWarnings("deprecation")
	public static boolean addMember(Party party, QuestPlayer player) {
		if (party.getMembers().size() < maxSize) {
			tellMembers(party,
				new FancyMessage(player.getPlayer().getName())
					.color(ChatColor.DARK_BLUE)
					.then(" has joined the party")
			);
			party.getMembers().add(player);
			party.getMembersTeam().addPlayer(player.getPlayer());
			updateScoreboard(party);
			return true;
		} else {
			tellMembers(party,
				new FancyMessage("Unable to add ")
					.then(player.getPlayer().getName())
					.color(ChatColor.DARK_BLUE)
					.then(" becuase the party is full!")
			);
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public static boolean removePlayer(Party party, QuestPlayer player, String exitMessage) {

		player.leaveParty(exitMessage);

		if (player.getIDString().equals(party.getLeader().getIDString())) {

			party.getMembersTeam().removePlayer(party.getLeader().getPlayer());
			party.getPartyBoard().resetScores(party.getLeader().getPlayer().getName());

			if (party.getMembers().size() == 1) {
				//close party
				party.getMembers().get(0).leaveParty("The party has been closed");
				clean(party);
				return true;
			}

			party.setLeader(party.getMembers().remove(0));
			party.getMembersTeam().removePlayer(party.getLeader().getPlayer());
			updateScoreboard(party);
			tellMembers(party,
				new FancyMessage(player.getPlayer().getName())
					.color(ChatColor.DARK_BLUE)
					.then(" has left the party")
			);
			return true;
		}

		if (party.getMembers().isEmpty()) {
			return false;
		}

		ListIterator<QuestPlayer> it = party.getMembers().listIterator();
		QuestPlayer qp;

		while (it.hasNext()) {
			qp = it.next();
			if (qp.getIDString().equals(player.getIDString())) {
				party.getMembersTeam().removePlayer(qp.getPlayer());
				party.getPartyBoard().resetScores(qp.getPlayer().getName());
				it.remove();
				tellMembers(party,
					new FancyMessage(player.getPlayer().getName())
						.color(ChatColor.DARK_BLUE)
						.then(" has left the party")
				);

				//make sure leader isn't the only one left
				if (party.getMembers().isEmpty()) {
					party.getLeader().leaveParty("The party has been closed.");
					clean(party);
				} else {
					updateScoreboard(party);
				}

				return true;
			}
		}

		return false;
	}

	public static void disband(Party party) {
		for (QuestPlayer player : party.getMembers()) {
			player.leaveParty("The party has disbanded.");
		}
		party.getLeader().leaveParty("The party has disbanded");

		clean(party);
	}

	private static void clean(Party party) {
		QuestManagerPlugin.questManagerPlugin.getPlayerManager().removeParty(party);
		Bukkit.getPluginManager().callEvent(
			new PartyDisbandEvent(party));
	}

	public static void tellMembers(Party party, String message) {
		tellMembers(party, new FancyMessage(message));
	}

	public static void tellMembers(Party party, FancyMessage message) {
		if (party.getLeader() != null) {
			Player l = party.getLeader().getPlayer().getPlayer();
			message.send(l);
			l.playNote(l.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
			l.playNote(l.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.G));
			l.playNote(l.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.E));
		}
		if (party.getMembers().isEmpty()) {
			return;
		}
		for (QuestPlayer qp : party.getMembers()) {
			if (!qp.getPlayer().isOnline()) {
				continue;
			}
			Player p = qp.getPlayer().getPlayer();
			message.send(qp.getPlayer().getPlayer());
			p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
			p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.G));
			p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.E));
		}
	}
}
