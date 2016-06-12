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

import com.skyisland.questmanager.fanciful.FancyMessage;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Wraps arounds a simple, single -use- message.
 * @author Skyler
 *
 */
public class PlainMessage extends Message {

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(PlainMessage.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(PlainMessage.class);
	}
	

	private enum aliases {
		DEFAULT(PlainMessage.class.getName()),
		SIMPLE("PlainMessage");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	
	private FancyMessage message;
	
	private PlainMessage() {
		super();
	}
	
	
	public PlainMessage(FancyMessage msg) {
		this.message = msg;
	}
	
	public PlainMessage(String msg) {
		this.message = new FancyMessage(msg);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		
		map.put("text", message);
		
		return map;
	}
	
	public static PlainMessage valueOf(Map<String, Object> map) {
		Object obj = map.get("text");
		
		PlainMessage msg = new PlainMessage();
		
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
		return message;
	}
}
