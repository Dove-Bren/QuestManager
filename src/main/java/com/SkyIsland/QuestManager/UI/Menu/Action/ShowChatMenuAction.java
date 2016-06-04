package com.SkyIsland.QuestManager.UI.Menu.Action;

import org.bukkit.entity.Player;

import com.SkyIsland.QuestManager.UI.ChatMenu;

/**
 * Actions that causes a chat menu to be shown to a player
 * @author Skyler
 *
 */
public class ShowChatMenuAction implements MenuAction {

	private Player player;
	
	private ChatMenu menu;
	
	public ShowChatMenuAction(ChatMenu menu, Player player) {
		this.player = player;
		this.menu = menu;
	}
	
	@Override
	public void onAction() {
		menu.show(player);
	}

}
