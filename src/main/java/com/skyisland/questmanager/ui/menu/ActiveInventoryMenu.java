package com.skyisland.questmanager.ui.menu;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.action.FillableInventoryAction;
import com.skyisland.questmanager.ui.menu.inventory.ReturnGuiInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.ui.menu.action.MenuAction;

/**
 * A menu implemented as an inventory that performs some action when closed
 * @author Skyler
 *
 */
public class ActiveInventoryMenu extends InventoryMenu implements RespondableMenu {
	
	private MenuAction action;
	
	public ActiveInventoryMenu(QuestPlayer player, ReturnGuiInventory inv, MenuAction closeAction) {
		super(player, inv);
		this.action = closeAction;		
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().getName() == null || !e.getInventory().getName().equals(inventory.getName())) {
			return;
		}
		
		if (!(e.getPlayer() instanceof Player) || !(((Player) e.getPlayer()).getUniqueId().equals(
				player.getPlayer().getUniqueId()))) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Inventory menu event matched names,"
					+ " but not players! [" + e.getPlayer().getName() + "]");
			return;
		}
		
		super.onInventoryClose(e);
		
		if (action instanceof FillableInventoryAction) {
			((FillableInventoryAction) action).provideItems(((ReturnGuiInventory) this.gui).getResult());
		}
		
		if (action != null)
			action.onAction();
		
	}
}
