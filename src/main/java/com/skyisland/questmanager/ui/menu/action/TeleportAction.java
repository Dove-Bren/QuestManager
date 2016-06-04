package com.skyisland.questmanager.ui.menu.action;


import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.message.Message;
import com.skyisland.questmanager.ui.menu.SimpleChatMenu;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
