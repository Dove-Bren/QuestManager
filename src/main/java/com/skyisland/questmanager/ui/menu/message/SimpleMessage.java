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

package com.skyisland.questmanager.ui.menu.message;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.skyisland.questmanager.fanciful.FancyMessage;

/**
 * Wraps arounds a simple, single -use- message.
 *
 */
public class SimpleMessage extends Message {

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(SimpleMessage.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(SimpleMessage.class);
	}
	

	private enum aliases {
		DEFAULT(SimpleMessage.class.getName()),
		SIMPLE("SimpleMessage");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	
	private FancyMessage message;
	
	private SimpleMessage() {
		super();
	}
	
	
	public SimpleMessage(FancyMessage msg) {
		this.message = msg;
	}
	
	public SimpleMessage(String msg) {
		this.message = new FancyMessage(msg);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		
		map.put("text", message);
		
		return map;
	}
	
	public static SimpleMessage valueOf(Map<String, Object> map) {
		Object obj = map.get("text");
		
		SimpleMessage msg = new SimpleMessage();
		
		if (obj instanceof FancyMessage) {
			msg.message = (FancyMessage) obj;
			return msg;
		}
		
		//else just assume it's a string!?
		msg.message = new FancyMessage((String) obj);
		return msg;
	}

	@Override
	public FancyMessage getFormattedMessage() {
		return new FancyMessage("")
		.then(sourceLabel == null ? 
				new FancyMessage("Unknown")	: sourceLabel)
			.color(ChatColor.DARK_GRAY)
			.style(ChatColor.BOLD)
		.then(":\n")
		.then(message);	
		
	}
}
