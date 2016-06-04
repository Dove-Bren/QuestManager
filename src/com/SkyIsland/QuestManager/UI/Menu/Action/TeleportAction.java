package com.SkyIsland.QuestManager.UI.Menu.Action;


import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.UI.ChatMenu;
import com.SkyIsland.QuestManager.UI.Menu.SimpleChatMenu;
import com.SkyIsland.QuestManager.UI.Menu.Message.Message;

/**
 * Ferries a player
 * @author Skyler
 *
 */
public class TeleportAction implements MenuAction {

	private int cost;
	
	private Location destination;
	
	private QuestPlayer player;
	
	private Message denial;
	
	public TeleportAction(int cost, Location destination, QuestPlayer player, Message denialMessage) {
		this.cost = cost;
		this.player = player;
		this.denial = denialMessage;
		this.destination = destination;
	}
	
	@Override
	public void onAction() {
		
		//check their money
		if (player.getMoney() >= cost) {
			//they have enough money
			
			//blindness for some time, but just teleportation & particles!
			
			if (!player.getPlayer().isOnline()) {
				System.out.println("Very bad TeleportAction error!!!!!!!!!!!!!");
				return;
			}
			
			player.addMoney(-cost);
			
			Player p = player.getPlayer().getPlayer();
			
			p.addPotionEffect(
					new PotionEffect(PotionEffectType.BLINDNESS, 60, 5));
			
			p.teleport(destination);
			destination.getWorld().playEffect(destination, Effect.STEP_SOUND, 0);
			
			
		} else {
			//not enough money
			//show them a menu, sorrow
						
			ChatMenu menu = new SimpleChatMenu(denial.getFormattedMessage());
			
			menu.show(player.getPlayer().getPlayer(), null);
		}
		
	}

}
