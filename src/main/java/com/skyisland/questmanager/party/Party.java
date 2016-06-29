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

import com.google.common.collect.ImmutableMap;
import com.skyisland.questmanager.player.Participant;
import com.skyisland.questmanager.player.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A group of players.
 *
 * Parties are displayed to the party members via a scoreboard.
 */
public class Party implements Participant {

	private final List<QuestPlayer> members;
	private final Scoreboard partyBoard;
	private final String name;
	private final Team membersTeam;
	private final UUID id;

	private QuestPlayer leader;

	Party(QuestPlayer leader) {
		this(leader, new ArrayList<>(), "");
	}

	private Party(QuestPlayer leader, List<QuestPlayer> members, String name) {
		this.members = members;
		this.leader = leader;
		this.name = name;
		this.id = leader.getPlayer().getUniqueId();

		partyBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		Team leaderTeam = partyBoard.registerNewTeam("Leader");
		membersTeam = partyBoard.registerNewTeam("members");
		leaderTeam.setPrefix(ChatColor.GOLD.toString());
		membersTeam.setPrefix(ChatColor.DARK_GREEN.toString());
		Objective board = partyBoard.registerNewObjective("side", "dummy");
		board.setDisplayName("Party");
		board.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public List<QuestPlayer> getMembers() {
		return members;
	}

	public QuestPlayer getLeader() {
		return leader;
	}

	void setLeader(QuestPlayer questPlayer) {
		this.leader = questPlayer;
	}

	public String getName() {
		return name;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public Collection<QuestPlayer> getParticipants() {
		Set<QuestPlayer> set = new HashSet<>(members);
		set.add(leader);
		return set;
	}

	@Override
	public String getIDString() {
		return id.toString();
	}

	public boolean isFull() {
		return (members.size() >= PartyManager.maxSize);
	}

	Scoreboard getPartyBoard() {
		return partyBoard;
	}

	Team getMembersTeam() {
		return membersTeam;
	}

	/**
	 * Creates a map representation of this party, for saving to config.
	 * @return the Party as a Map.
	 */
	@Override
	public Map<String, Object> serialize() {
		return ImmutableMap.<String, Object>builder()
			.put("name", name)
			.put("leader", leader.serialize())
			.put("members", members.stream().map(QuestPlayer::serialize).collect(Collectors.toCollection(LinkedList::new)))
			.build();
	}

	/**
	 * Creates a Party object from a given Map, for loading from config.
	 * @param map the given Map.
	 * @return the Party represented by the map.
	 */
	@SuppressWarnings("unchecked")
	public static Party valueOf(Map<String, Object> map) {

		if (map == null || !map.containsKey("leader")) {
			return null;
		}

		return new Party(
			QuestPlayer.valueOf((Map<String, Object>) map.get("leader")),
			((List<Map<String, Object>>) map.get("members")).stream().map(QuestPlayer::valueOf).collect(Collectors.toList()),
			(String) map.get("name"));
	}

	/** Registers this class as configuration serializable with all defined */
	public static void registerWithAliases() {
		Arrays.asList("com.SkyIsland.QuestManager.Player.Party", Party.class.getName(), "Party", "P", "QPP")
			.forEach(alias -> ConfigurationSerialization.registerClass(Party.class, alias));
	}

	/** Registers this class as configuration serializable with only the default alias */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(Party.class);
	}
}
