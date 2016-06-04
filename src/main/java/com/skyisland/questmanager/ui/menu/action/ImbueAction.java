package com.skyisland.questmanager.ui.menu.action;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.magic.Imbuement;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;

public class ImbueAction implements MenuAction {
	
	private final String message = ChatColor.DARK_GREEN + "Your imbuement has been applied";
	
	private final Sound sound = Sound.BLOCK_BREWING_STAND_BREW;

	private QuestPlayer player;
	
	private Imbuement imbuement;
	
	public ImbueAction(QuestPlayer player, Imbuement imbuement) {
		this.player = player;
		this.imbuement = imbuement;
	}
	
	@Override
	public void onAction() {
		/*
		 * Apply has just happened. Just need to put it on, actually
		 */
		player.setCurrentImbuement(imbuement);
		
		if (!player.getPlayer().isOnline()) {
			return;
		}
		
		Player p = player.getPlayer().getPlayer();
		
		if (player.getOptions().getOption(PlayerOptions.Key.CHAT_COMBAT_RESULT)) {
			p.sendMessage(message);
		}
		
		p.getWorld().playSound(p.getLocation(), sound, 1, 1);
		
	}

}
