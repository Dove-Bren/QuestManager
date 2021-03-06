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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.ui.ChatGuiHandler;
import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.message.Message;

public class MultioptionChatMenu extends ChatMenu implements RespondableMenu {
	
	/**
	 * A map between unique menu keys and options
	 */
	private Map<String, ChatMenuOption> options;
	
	private int keyindex;
	
	/**
	 * Creates (but does not show!) a menu with zero or more options. Each option is associated with
	 * The provided MenuActions allow for more control over the action of the menu buttons. If
	 * there is no desired action for a corresponding action, <i>null</i> should be passed.
	 */
	public MultioptionChatMenu(Message body, ChatMenuOption option) {
		super(body.getFormattedMessage());
		
		options = new TreeMap<>();
		addOption(option);
		
		keyindex = 1;
		
		this.setMessage(formatMessage(body));
	}
	
	public MultioptionChatMenu(Message body) {
		super(body.getFormattedMessage());
		
		options = new TreeMap<>();
		
		keyindex = 1;
		
		this.setMessage(formatMessage(body));
	}
	
	public MultioptionChatMenu(Message body, ChatMenuOption ... options) {
		super(body.getFormattedMessage());

		
		this.options = new TreeMap<>();
		
		keyindex = 1;
		
		for (ChatMenuOption opt : options) {
			addOption(opt);
		}
		
		this.setMessage(formatMessage(body));
	}
	
	public MultioptionChatMenu(Message body, Collection<ChatMenuOption> options) {
		super(body.getFormattedMessage());

		
		this.options = new TreeMap<>();
		
		keyindex = 1;

		options.forEach(this::addOption);
		
		this.setMessage(formatMessage(body));
	}
	
	/**
	 * Adds the given option to the list of options used in the menu.
	 */
	private void addOption(ChatMenuOption option) {
		this.options.put(genKey(), option);
	}

	@Override
	protected boolean input(Player player, String arg) {
		
		if (options.isEmpty()) {
			return false;
		}
		
		for (String key : options.keySet()) {
			if (key.equals(arg)) {
				ChatMenuOption opt = options.get(key);
				opt.getAction().onAction();
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
		FancyMessage msg = new FancyMessage("--------------------------------------------\n")
					.style(ChatColor.BOLD)
				.then(rawBody.getFormattedMessage())
				.then("\n\n");
				
		if (!options.isEmpty())
		for (String key : options.keySet()) {
			ChatMenuOption opt = options.get(key);
			msg.then("    ")
			.then(opt.getLabel().getFormattedMessage()).command(ChatGuiHandler.cmdBase + " " + key)
				.color(ChatColor.DARK_GREEN)
				.style(ChatColor.ITALIC);
			if (opt.getTooltip() != null) {
				msg.formattedTooltip(opt.getTooltip());
			}
		}
		
		msg.then("--------------------------------------------\n")
		.style(ChatColor.BOLD);
		
		return msg;
	}
}
