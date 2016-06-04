package com.skyisland.questmanager.ui.menu.action;

import com.skyisland.questmanager.fanciful.FancyMessage;
import org.bukkit.ChatColor;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.SimpleChatMenu;

public class ChangeTitleAction implements MenuAction {

	private String newTitle;
	
	private QuestPlayer player;
	
	public ChangeTitleAction(QuestPlayer player, String title) {
		this.newTitle = title;
		this.player = player;
	}
	
	@Override
	public void onAction() {
		player.setTitle(newTitle);
		
		if (player.getPlayer().isOnline())
		new SimpleChatMenu(
				new FancyMessage("You changed your title to ")
					.color(ChatColor.GRAY)
				.then(newTitle))
		.show(player.getPlayer().getPlayer());
	}

}
