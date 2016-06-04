package com.skyisland.questmanager.ui.menu.inventory;

import java.util.LinkedList;
import java.util.List;

import com.skyisland.questmanager.npc.utils.Service;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.npc.utils.ServiceOffer;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.action.MenuAction;
import com.skyisland.questmanager.ui.menu.action.OfferServiceAction;
import com.skyisland.questmanager.ui.menu.message.Message;
import com.skyisland.questmanager.ui.menu.message.PlainMessage;

/**
 * Represents a craft the service NPC can perform.
 * @author Skyler
 *
 */
public class ServiceOfferItem extends ServiceItem {
	
	private static final Message denialMessage = new PlainMessage(ChatColor.RED + "You were missing some components of the craft");
	
	private ServiceOffer offer;
	
	public ServiceOfferItem(ServiceOffer service) {
		super(service.getItem());
		this.offer = service;
	}
	
	/**
	 * Returns the item that should be used to display the item to the given player.<br />
	 * This method formats the lore, etc to display correctly (and with correct colors) to
	 * the provided player given their fame and money.<br /><br />
	 * 
	 * If the passed player is null, the item without lore is returned.
	 * @param player
	 * @return
	 */
	@Override
	public ItemStack getDisplay(QuestPlayer player) {
		if (player == null) {
			return getRawDisplayItem();
		}
		ItemStack ret = getRawDisplayItem().clone();
		ItemMeta meta = ret.getItemMeta();
		List<String> lore = new LinkedList<String>();
		lore.add(
				ChatColor.BLUE + "Offer               " + ChatColor.GOLD + offer.getPrice());
		lore.add(ChatColor.DARK_RED + "Requested:");
		
		if (offer.getItem().hasItemMeta() && offer.getItem().getItemMeta().hasDisplayName()) {
			lore.add((player.hasItem(offer.getItem()) ? ChatColor.GREEN : ChatColor.RED) 
					+ offer.getItem().getItemMeta().getDisplayName());
		} else {
			lore.add( (player.hasItem(offer.getItem()) ? ChatColor.GREEN : ChatColor.RED)
					+ ServiceCraftItem.toCase(offer.getItem().getType().toString()));
		}
			
		meta.setLore(lore);
		ret.setItemMeta(meta);
		
		return ret;
	}
	
	/**
	 * Returns the item this offer desires
	 * @return
	 */
	public ItemStack getItem() {
		return offer.getItem();
	}
	
	@Override
	public MenuAction getAction(QuestPlayer player) {
		return new OfferServiceAction(offer, player, denialMessage);
	}

	/**
	 * @return the cost
	 */
	public int getPrice() {
		return offer.getPrice();
	}

	@Override
	public Service getService() {
		return offer;
	}

	
	
	//extend properly
	//make offerServiceAction
	//finish doing w/e with the service inventory
	
	
	
}
