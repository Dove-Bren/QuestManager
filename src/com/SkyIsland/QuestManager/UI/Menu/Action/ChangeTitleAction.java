package com.SkyIsland.QuestManager.UI.Menu.Action;

import org.bukkit.ChatColor;

import com.SkyIsland.QuestManager.Fanciful.FancyMessage;
import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.UI.Menu.SimpleChatMenu;

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
