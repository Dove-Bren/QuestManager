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

package com.skyisland.questmanager.quest.history;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class History {
	
	private List<HistoryEvent> events;

	public static History fromConfig(ConfigurationSection configurationSection) throws InvalidConfigurationException {
		if (configurationSection == null) {
			return null;
		}
		if (!configurationSection.contains("HistoryEvents")) {
			throw new InvalidConfigurationException();
		}
		
		History history = new History();
		
		List<String> list;
		
		list = configurationSection.getStringList("HistoryEvents");
		
		if (list != null && !list.isEmpty()) {
			for (String line : list) {
				history.addHistoryEvent(new HistoryEvent(line));
			}
		}
		
		return history;
	}
	
	/**
	 * Creates a new history with no events
	 */
	public History() {
		this.events = new LinkedList<>();
	}
	
	public List<HistoryEvent> events() {
		return this.events;
	}
	
	public void addHistoryEvent(HistoryEvent event) {
		events.add(event);
	}
	
	/**
	 * Returns a formatted description of this history composed of each contained event's
	 * description
	 */
	@Override
	public String toString() {
		if (events == null || events.isEmpty()) {
			return "";
		}
		
		String builder = "";
		
		for (HistoryEvent event : events) {
			builder += event.getDescription() + "\n";
		}
		
		//get rid of trailing newline
		builder = builder.substring(0, builder.length() - 3);
		
		return builder;
	}
	
	/**
	 * Returns a config file (section) that stores the information contained in this history
	 */
	public YamlConfiguration toConfig() {
		
		YamlConfiguration config = new YamlConfiguration();
		
		if (events.isEmpty()) {
			return config;
		}
		List<String> list = new ArrayList<>(events.size());
		
		for (HistoryEvent e : events) {
			list.add(e.getDescription());
		}
		
		config.set("HistoryEvents", list);
		
		return config;
	}
}
