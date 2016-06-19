package com.skyisland.questmanager.ui.menu;

import org.bukkit.entity.Player;

import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.ui.ChatMenu;

/**
 * A basic text-only menu.
 * @author Skyler
 *
 */
public class SimpleChatMenu extends ChatMenu {

	public SimpleChatMenu(FancyMessage msg) {
		super(msg);
	}

	@Override
	protected boolean input(Player player, String arg) {
		return true; //do nothing. Just a text menu
	}
}
