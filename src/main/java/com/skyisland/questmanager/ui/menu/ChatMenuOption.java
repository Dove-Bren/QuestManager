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

import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.ui.menu.action.MenuAction;
import com.skyisland.questmanager.ui.menu.message.Message;

/**
 * An 'option' for a chat menu.
 * This includes a label for the option and a corresponding action to be executed upon selection of that
 * option.
 * @author Skyler
 *
 */
public class ChatMenuOption {
	
	private Message label;
	
	private FancyMessage tooltip;
	
	private MenuAction action;
	
	public ChatMenuOption(Message message, MenuAction action, FancyMessage tooltip) {
		this.label = message;
		this.action = action;
		this.tooltip = tooltip;
	}
	
	public ChatMenuOption(Message message, MenuAction action) {
		this(message, action, null);
	}
	
	/**
	 * @return the label
	 */
	public Message getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(Message label) {
		this.label = label;
	}

	/**
	 * @return the action
	 */
	public MenuAction getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(MenuAction action) {
		this.action = action;
	}
	
	public void setTooltip(FancyMessage tooltip) {
		this.tooltip = tooltip;
	}
	
	public FancyMessage getTooltip() {
		return tooltip;
	}
}
