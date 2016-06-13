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

package com.skyisland.questmanager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.skyisland.questmanager.configuration.PluginConfiguration;
import com.skyisland.questmanager.player.Participant;
import com.skyisland.questmanager.player.Party;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.special.TitleEffect;
import com.skyisland.questmanager.scheduling.IntervalScheduler;
import com.skyisland.questmanager.scheduling.Tickable;

/**
 * Stores a database of QuestPlayers for lookup and loading
 *
 */
public class PlayerManager implements Tickable {
	
	private Map<UUID, QuestPlayer> players;
	
	private Map<UUID, Party> parties;
	
	private TitleEffect titleEffect;
	
	/**
	 * Creates and loads player manager information from the provided configuration file.
	 */
	public PlayerManager(YamlConfiguration config) {
		
		players = new HashMap<>();
		parties = new HashMap<>();
		
		QuestManagerPlugin.questManagerPlugin.getLogger().info("Loading player database...");
		
		ConfigurationSection pSex = config.getConfigurationSection("players");
		
		if (!pSex.getKeys(false).isEmpty()) {
		QuestPlayer player;
			for (String key : pSex.getKeys(false)) {
				player = (QuestPlayer) pSex.get(key);
				players.put(UUID.fromString(player.getIDString()), player);
			}
		}
			
		ConfigurationSection gSex = config.getConfigurationSection("parties");
		
		if (!gSex.getKeys(false).isEmpty())
		for (String key : gSex.getKeys(false)) {
			parties.put(
					UUID.fromString(key), (Party) gSex.get(key));
		}
		
		//check if we need to do day/night regen
		PluginConfiguration pc = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration();
		if (pc.getMagicEnabled())
		if (pc.getMagicRegenDay() != 0 || pc.getMagicRegenNight() != 0) {
			IntervalScheduler.getScheduler().register(this);
		}
		
		this.titleEffect = new TitleEffect();
		
	}
	
	/**
	 * Returns the QuestPlayer corresponding the the passed OfflinePlayer.
	 * This method creates a new QuestPlayer wrapper for the provided UUID if there does not
	 * already exist a record for it.
	 */	
	public QuestPlayer getPlayer(OfflinePlayer player) {
		return (player == null ? null : getPlayer(player.getUniqueId()));
	}
	
	/**
	 * Returns the QuestPlayer corresponding the the passed UUID.
	 * This method creates a new QuestPlayer wrapper for the provided UUID if there does not
	 * already exist a record for it.
	 */
	public QuestPlayer getPlayer(UUID id) {
		if (players.containsKey(id)) {
			return players.get(id);
		}
		
		//initialize a player!
		QuestPlayer player = new QuestPlayer(Bukkit.getOfflinePlayer(id));
		players.put(id, player);
		
		return player;
	}
	
	/**
	 * Returns the party paired with the given ID.
	 * if the party doesn't exist, null is returned instead
	 */
	public Party getParty(UUID id) {
		if (!parties.containsKey(id)) {
			return null;
		}
		
		return parties.get(id);
	}
	
	public void addParty(Party party) {
		parties.put(party.getID(), party);
	}
	
	public void removeParty(Party party) {
		parties.remove(party.getID());
	}

	public Participant getParticipant(String idString) {

		Participant participant = parties.get(UUID.fromString(idString));

		if (participant != null) {
			return participant;
		}

		//assume it's a player string
		return getPlayer(UUID.fromString(idString));
	}
	
	public Collection<Party> getParties() {
		return parties.values();
	}
	
	public Collection<QuestPlayer> getPlayers() {
		return players.values();
	}
	
	public void save(File saveFile) {
		
		QuestManagerPlugin.questManagerPlugin.getLogger().info(
				"Saving player database...");
		
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection playSex = config.createSection("players");
		
		QuestPlayer qp;
		if (!players.isEmpty()) {
			for (UUID key : players.keySet()) {
				qp = getPlayer(key);
				String name = qp.getPlayer().getName();
				playSex.set(name == null ? key.toString() : name + key.toString().substring(0, 5), getPlayer(key));
			}
		}
		
		ConfigurationSection gSex = config.createSection("parties");
		if (!parties.isEmpty()) {
			for (UUID key : parties.keySet()) {
				if (key == null) {
					continue;
				}
				gSex.set(key.toString(), getParty(key));
			}
		}
		
		try {
			config.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tick() {
		PluginConfiguration pc = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration();
		double day = pc.getMagicRegenDay();
		double night = pc.getMagicRegenNight();
		for (QuestPlayer qp : players.values()) {
			OfflinePlayer p = qp.getPlayer();
			if (qp.getPlayer().isOnline() && QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
					.getWorlds().contains(p.getPlayer().getWorld().getName())) {
				//potential for regen
				long time = p.getPlayer().getWorld().getTime();
				Location ploc = p.getPlayer().getLocation();
				if (day != 0 && (time < 13000 || time >= 23000))
				if (!pc.getMagicRegenOutside() || (ploc.getBlockY() < ploc.getWorld().getMaxHeight()
						&& ploc.getBlock().getLightFromSky() > 13)) {
										
					qp.regenMP(day);
				}
				if (night != 0 && (time >= 13000 && time < 23000)) 
				if (!pc.getMagicRegenOutside() || (ploc.getBlockY() < ploc.getWorld().getMaxHeight()
						&& p.getPlayer().getLocation().getBlock().getLightFromSky() > 13)) {
										
					qp.regenMP(night);
				}
			}
		}
	}
	
	public TitleEffect getTitleEffect() {
		return titleEffect;
	}
}
