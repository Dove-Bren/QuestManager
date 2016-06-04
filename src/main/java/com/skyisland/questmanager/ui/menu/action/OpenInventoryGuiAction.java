package com.skyisland.questmanager.ui.menu.action;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.InventoryMenu;
import com.skyisland.questmanager.ui.menu.inventory.GuiInventory;

public class OpenInventoryGuiAction implements MenuAction {
	
	private GuiInventory inv;
	
	private QuestPlayer player;
	
	public OpenInventoryGuiAction(QuestPlayer player, GuiInventory inv) {
		this.inv = inv;
		this.player = player;
	}
	
	@Override
	public void onAction() {
		InventoryMenu menu = new InventoryMenu(player, inv);
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(
				player.getPlayer().getPlayer(), menu);
	}

}
