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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.skyisland.questmanager.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Message that has multiple options and multiple messages that happen because of that option.
 * Messages spawn new menus to display them. This means this tree can have a cascading tree flow
 * and branch into other trees.
 *
 */
public class TreeMessage extends Message {
	
	public static class Option {
		private FancyMessage label;
		
		private Message result;
		
		public Option(FancyMessage label, Message result) {
			this.label = label;
			this.result = result;
		}

		public FancyMessage getLabel() {
			return label;
		}

		public Message getResult() {
			return result;
		}
	}
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(TreeMessage.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(TreeMessage.class);
	}
	

	private enum aliases {
		DEFAULT(TreeMessage.class.getName()),
		SIMPLE("TreeMessage");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private FancyMessage body;
	
	private List<Option> options;
	
	private TreeMessage() {
		super();
		this.options = new LinkedList<>();
	}
	
	public TreeMessage(FancyMessage body, Option ...options) {
		super();
		this.body = body;
		Collections.addAll(this.options, options);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("body", body);
		List<Map<String, Object>> opts = new LinkedList<>();
		Map<String, Object> om;
		for (Option opt : options) {
			om = new TreeMap<>();
			om.put("label", opt.getLabel());
			om.put("message", opt.getResult());
			opts.add(om);
		}
		
		map.put("options", opts);		
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static TreeMessage valueOf(Map<String, Object> map) {
		//a little more work, cause we want to accept regular strings too!
		Object oBody = map.get("body");
		List<Map<String, Object>> opts = (List<Map<String, Object>>) map.get("options");
		
		TreeMessage msg = new TreeMessage();
		
		//load body
		if (oBody instanceof FancyMessage) {
			msg.body = (FancyMessage) oBody;
		} else {
			msg.body = new FancyMessage((String) oBody);
		}
		
		//load options
		List<Option> options = new LinkedList<>();
		Option o;
		Object oLab;
		FancyMessage label;
		for (Map<String, Object> opt : opts) {
			oLab = opt.get("label");
			if (oLab instanceof String) {
				label = new FancyMessage((String) oLab); 
			} else {
				label = (FancyMessage) oLab;
			}
			o = new Option(label, (Message) opt.get("message"));
			
			options.add(o);
		}
		
		msg.options = options;

		
		return msg;
	}
	
	public FancyMessage getBody() {
		return body;
	}

	@Override
	public FancyMessage getFormattedMessage() {
		FancyMessage msg = new FancyMessage("--------------------------------------------\n")
				.style(ChatColor.BOLD)
			.then(sourceLabel == null ? 
						new FancyMessage("Unknown")	: sourceLabel)
					.color(ChatColor.DARK_GRAY)
					.style(ChatColor.BOLD)
			.then(":\n")
			.then(body);
		
		return msg;
	}
	
	public List<Option> getOptions() {
		return options;
	}
}
