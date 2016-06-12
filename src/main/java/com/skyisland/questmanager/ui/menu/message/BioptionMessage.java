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
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.skyisland.questmanager.ui.ChatGuiHandler;

/**
 * Wrapping/formatting class for a message with two options.
 *
 */
public class BioptionMessage extends Message {

	public static final String OPTION1 = "1";
	
	public static final String OPTION2 = "2";
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(BioptionMessage.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(BioptionMessage.class);
	}
	

	private enum aliases {
		DEFAULT(BioptionMessage.class.getName()),
		SIMPLE("BioptionMessage");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private FancyMessage body;
	
	private FancyMessage option1Label;
	
	private FancyMessage option2Label;
	
	private FancyMessage option1Msg;
	
	private FancyMessage option2Msg;
	
	private BioptionMessage() {
		super();
	}
	
	public BioptionMessage(FancyMessage body, FancyMessage option1Label, FancyMessage option2Label,
			FancyMessage option1Msg, FancyMessage option2Msg) {
		super();
		this.body = body;
		this.option1Label = option1Label;
		this.option2Label = option2Label;
		this.option1Msg = option1Msg;
		this.option2Msg = option2Msg;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("body", body);
		map.put("option1Label", option1Label);
		map.put("option2Label", option2Label);
		map.put("option1", option1Msg);
		map.put("option2", option2Msg);
		
		return map;
	}
	
	public static BioptionMessage valueOf(Map<String, Object> map) {
		//a little more work, cause we want to accept regular strings too!
		Object oBody = map.get("body");
		Object oOption1 = map.get("option1");
		Object oOption2 = map.get("option2");
		Object oOption1Label = map.get("option1Label");
		Object oOption2Label = map.get("option2Label");
		
		BioptionMessage msg = new BioptionMessage();
		
		//load body
		if (oBody instanceof FancyMessage) {
			msg.body = (FancyMessage) oBody;
		} else {
			msg.body = new FancyMessage((String) oBody);
		}
		
		//load option labels
		if (oOption1Label instanceof String) {
			msg.option1Label = new FancyMessage((String) oOption1Label);
		} else {
			msg.option1Label = (FancyMessage) oOption1Label;
		}

		if (oOption2Label instanceof String) {
			msg.option2Label = new FancyMessage((String) oOption2Label);
		} else {
			msg.option2Label = (FancyMessage) oOption2Label;
		}
		
		//load option responses
		if (oOption1 instanceof String) {
			msg.option1Msg = new FancyMessage((String) oOption1);
		} else {
			msg.option1Msg = (FancyMessage) oOption1;
		}
		
		if (oOption2 instanceof String) {
			msg.option2Msg = new FancyMessage((String) oOption2);
		} else {
			msg.option2Msg = (FancyMessage) oOption2;
		}

		
		return msg;
	}
	
	public FancyMessage getBody() {
		return body;
	}

	@Override
	public FancyMessage getFormattedMessage() {
		return new FancyMessage("--------------------------------------------\n")
				.style(ChatColor.BOLD)
			.then(sourceLabel == null ? 
						new FancyMessage("Unknown")	: sourceLabel)
					.color(ChatColor.DARK_GRAY)
					.style(ChatColor.BOLD)
			.then(":\n")
			.then(body).then("\n\n   ")
				.then(option1Label).command(ChatGuiHandler.cmdBase + " " + OPTION1)
					.color(ChatColor.DARK_GREEN)
					.style(ChatColor.ITALIC)
				.then("   -   ")
				.then(option2Label).command(ChatGuiHandler.cmdBase + " " + OPTION2)
					.color(ChatColor.DARK_GREEN)
					.style(ChatColor.ITALIC)
				.then("--------------------------------------------")
					.style(ChatColor.BOLD)
				;
	}
	
	public FancyMessage getResponse1() {
		return new FancyMessage("")
		.then(sourceLabel == null ? 
				new FancyMessage("Unknown")	: sourceLabel)
			.color(ChatColor.DARK_GRAY)
			.style(ChatColor.BOLD)
		.then(":\n")
		.then(option1Msg);
	}
	
	public FancyMessage getResponse2() {
		return new FancyMessage("")
		.then(sourceLabel == null ? 
				new FancyMessage("Unknown")	: sourceLabel)
			.color(ChatColor.DARK_GRAY)
			.style(ChatColor.BOLD)
		.then(":\n")
		.then(option2Msg);
	}
}
