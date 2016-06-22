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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

final class PartyRepository {

	/** A map of player's UUIDs to their parties */
	private static final Map<UUID, Party> PARTY_MAP = new HashMap<>();

	private PartyRepository(){}

	public static Optional<Party> get(UUID uuid){
		return Optional.ofNullable(PARTY_MAP.get(uuid));
	}

	public static void put(UUID uuid, Party party){
		PARTY_MAP.put(uuid, party);
	}
}
