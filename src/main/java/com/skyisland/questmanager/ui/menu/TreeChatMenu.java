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

package com.skyisland.questmanager.ui.menu;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.skyisland.questmanager.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.ui.ChatGuiHandler;
import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.message.Message;
import com.skyisland.questmanager.ui.menu.message.TreeMessage;
import com.skyisland.questmanager.ui.menu.message.TreeMessage.Option;

/**
 * Menu with multiple options that can lead to other menus.
 * @author Skyler
 *
 */
public class TreeChatMenu extends ChatMenu implements RespondableMenu {
	
	/**
	 * A map between unique menu keys and options
	 */
	private Map<String, Option> options;
	
	private int keyindex;
	
	public TreeChatMenu(TreeMessage body) {
		this(body, body.getOptions());
	}
	
	public TreeChatMenu(Message body, Option ... options) {		
		super(body.getFormattedMessage());

		
		this.options = new TreeMap<>();
		
		keyindex = 1;
		
		for (Option opt : options) {
			addOption(opt);
		}
		
		this.setMessage(formatMessage(body));
	}
	
	public TreeChatMenu(Message body, Collection<? extends Option> options) {
		super(body.getFormattedMessage());

		
		this.options = new TreeMap<>();
		
		keyindex = 1;

		options.forEach(this::addOption);
		
		this.setMessage(formatMessage(body));
	}

	/**
	 * Adds the given option to the list of options used in the menu.
	 */
	private void addOption(Option option) {
		this.options.put(genKey(), option);
	}

	@Override
	protected boolean input(Player player, String arg) {
		
		if (options.isEmpty()) {
			return false;
		}
		
		for (String key : options.keySet()) {
			if (key.equals(arg)) {
				Option opt = options.get(key);
				Message msg = opt.getResult();
				
				try {
					FancyMessage newSL = opt.getLabel().clone();
					msg.setSourceLabel(newSL);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.setSourceLabel(new FancyMessage("(Continued)"));
				}
				
				ChatMenu.getDefaultMenu(msg).show(player, this.getQuestBacker());
				return true;
			}
		}
		
		
		player.sendMessage("Something went wrong! [Invalid Mopt Argument!]");
		return false;
		
	}
	
	/**
	 * Uses the internal key index to generate the next key for registration
	 */
	private String genKey() {
		String key = "M" + keyindex;
		keyindex++;
		
		return key;
		//return "M" + keyindex++;
	}

	private FancyMessage formatMessage(Message rawBody) {
		FancyMessage msg = new FancyMessage("").then(rawBody.getFormattedMessage())
				.then("\n\n");
				
		if (!options.isEmpty())
		for (String key : options.keySet()) {
			Option opt = options.get(key);
			msg.then("    ")
			.then(opt.getLabel()).command(ChatGuiHandler.cmdBase + " " + key)
				.color(ChatColor.DARK_GREEN)
				.style(ChatColor.ITALIC);
		}
		
		msg.then("--------------------------------------------\n")
		.style(ChatColor.BOLD);
		
		return msg;
	}
}
