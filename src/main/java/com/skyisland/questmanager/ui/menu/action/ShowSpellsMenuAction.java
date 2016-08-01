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

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.magic.spell.SimpleTargetSpell;
import com.skyisland.questmanager.magic.spell.Spell;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.InventoryMenu;
import com.skyisland.questmanager.ui.menu.inventory.BasicInventory;
import com.skyisland.questmanager.ui.menu.inventory.BasicInventoryItem;

public class ShowSpellsMenuAction implements MenuAction {
	
	private QuestPlayer player;
	
	public ShowSpellsMenuAction(QuestPlayer player) {
		this.player = player;
	}
	
	@Override
	public void onAction() {
		BasicInventory inv = new BasicInventory();
//		List<String> descList;
//		for (Skill.Type type : Skill.Type.values()) {
//			
//			boolean spoil = player.getOptions().getOption(PlayerOptions.Key.SKILL_REVEAL);
//			for (Skill s : QuestManagerPlugin.questManagerPlugin.getSkillManager().getSkills(type)) {
//				//get a formatted description. (Code from QuestPlayer's magic menu)
//				
//				if (!spoil && player.getSkillLevel(s) <= 0 && player.getSkillExperience(s) <= 0) {
//					continue;
//				}
//				
//				descList = formatDescription(s.getDescription(player));
//
//				descList.add(0, ChatColor.DARK_GREEN + "" + player.getSkillLevel(s) + "."
//						+ ((int) (player.getSkillExperience(s)*100)) + "");
//				
//				descList.add(0, s.getName());
//				
//				MenuAction responseAction = null;
//				
//				
//				if (s instanceof CraftingSkill) {
//					descList.add(" ");
//					descList.add(ChatColor.BLUE + "Click here for recipes");
//					responseAction = new ShowSkillRecipesAction(player, (CraftingSkill) s);
//				}
//				
//				inv.addInventoryItem(new BasicInventoryItem(
//						s.getIcon(), descList, responseAction
//						));
//				
//				
//			}
//		}
		
		if (player.getSpells() == null || player.getSpells().isEmpty()) {
			player.getPlayer().getPlayer().sendMessage(
					ChatColor.RED + "You do not know any spells");
			return;
		}
		
		for (String t : player.getSpells()) {
			List<String> descList = new LinkedList<>();
			Spell sp = QuestManagerPlugin.questManagerPlugin.getSpellManager().getSpell(t);
			if (sp != null) {
				
				descList.add(ChatColor.RED + sp.getName());
				descList.add(ChatColor.GOLD + "Difficulty: " + sp.getDifficulty());
				descList.add(ChatColor.BLUE + "Mana Cost: " + sp.getCost());
				if (sp instanceof SimpleTargetSpell) {
					SimpleTargetSpell spell = (SimpleTargetSpell) sp;
					descList.add(ChatColor.DARK_GREEN + "Range: " + spell.getMaxDistance());
					descList.add(ChatColor.DARK_PURPLE + "Speed: " + spell.getSpeed());
				}
				
				String desc;
				desc = sp.getDescription();
				
				String mid;
				int pos;
				while (desc.length() > 30) {
					
					desc = desc.trim();
					
					//find first space before 30
					mid = desc.substring(0, 30);
					pos = mid.lastIndexOf(" ");
					if (pos == -1) {
						descList.add(mid);
						desc = desc.substring(30);
						continue;
					}
					//else we found a space
					descList.add(mid.substring(0, pos));
					desc = desc.substring(pos);
				}
				
				descList.add(desc.trim());
			} else {
				descList.add("No Description");
			}
			
//			String desc = "";
//			for (int i = 0; i < descList.size() - 1; i++) {
//				desc += descList.get(i) + "\n";
//			}
//			desc += descList.get(descList.size() - 1);
//			opts.add(new ChatMenuOption(
//					new PlainMessage(t),
//					new ChangeSpellHolderAction(this, holder, t),
//					new FancyMessage("").then(desc)));
			ItemStack icon = new ItemStack(Material.EMPTY_MAP);
			ItemMeta meta = icon.getItemMeta();
			meta.setDisplayName(sp.getName());
			icon.setItemMeta(meta);
			inv.addInventoryItem(new BasicInventoryItem(
					icon, descList, null
					));
		}
		
		
		InventoryMenu menu = new InventoryMenu(player, inv);
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(
				player.getPlayer().getPlayer(), menu);
	}
	
}
