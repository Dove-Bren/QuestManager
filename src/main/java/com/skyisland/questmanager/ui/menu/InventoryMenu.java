package com.skyisland.questmanager.ui.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.inventory.CloseableGui;
import com.skyisland.questmanager.ui.menu.inventory.GuiInventory;

/**
 * A menu implemented as an inventory
 * @author Skyler
 *
 */
public class InventoryMenu implements Listener {
	
	/**
	 * The inventory item to hold onto an manipulate
	 */
	protected Inventory inventory;
	
	/**
	 * The background collection of the inventory
	 */
	protected GuiInventory gui;
	/**
	 * The QuestPlayer involved with this menu
	 */
	protected QuestPlayer player;
	
	public InventoryMenu(QuestPlayer player, GuiInventory inv) {
		this.player = player;
		this.gui = inv;
		this.inventory = inv.getFormattedInventory(player);
		
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	/**
	 * @return the player
	 */
	public QuestPlayer getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(QuestPlayer player) {
		this.player = player;
	}

	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e) {
		if (e.isCancelled() || e.getInventory().getName() == null
				|| !e.getInventory().getName().equals(inventory.getName())) {
			return;
		}
		
		//our inventory event!
		int pos = e.getRawSlot();

		e.setCancelled(true);
		if (gui.getItem(pos) == null || gui.getItem(pos).getAction(player) == null) {
			return;
		}
		
		gui.getItem(pos).getAction(player).onAction();
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().getName() == null || !e.getInventory().getName().equals(inventory.getName())) {
			return;
		}
		
		if (!(e.getPlayer() instanceof Player) || !(((Player) e.getPlayer()).getUniqueId().equals(
				player.getPlayer().getUniqueId()))) {
			QuestManagerPlugin.logger.warning("Inventory menu event matched names,"
					+ " but not players! [" + e.getPlayer().getName() + "]");
			return;
		}
		

		if (gui instanceof CloseableGui)  {
			((CloseableGui) gui).onClose();
		}
		
		//our inventory is closing
		//unregister ourselves, unregister with handler, end
		HandlerList.unregisterAll(this);
		
		
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().closeMenu((Player) e.getPlayer());
		
	}
}
