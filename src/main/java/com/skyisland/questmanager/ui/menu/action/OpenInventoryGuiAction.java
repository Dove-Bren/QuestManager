/*
 *  QuestManager: An RPG plugin for the Bukkit API.
 *  Copyright (C) 2015-2016 Github Contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
