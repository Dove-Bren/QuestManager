package com.skyisland.questmanager.ui.menu.action;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.CraftingSkill;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.ui.menu.InventoryMenu;
import com.skyisland.questmanager.ui.menu.inventory.BasicInventory;
import com.skyisland.questmanager.ui.menu.inventory.BasicInventoryItem;

public class ShowSkillMenuAction implements MenuAction {
	
	private QuestPlayer player;
	
	public ShowSkillMenuAction(QuestPlayer player) {
		this.player = player;
	}
	
	@Override
	public void onAction() {
		BasicInventory inv = new BasicInventory();
		List<String> descList;
		for (Skill.Type type : Skill.Type.values()) {
			
			boolean spoil = player.getOptions().getOption(PlayerOptions.Key.SKILL_REVEAL);
			for (Skill s : QuestManagerPlugin.questManagerPlugin.getSkillManager().getSkills(type)) {
				//get a formatted description. (Code from QuestPlayer's magic menu)
				
				if (!spoil && player.getSkillLevel(s) <= 0 && player.getSkillExperience(s) <= 0) {
					continue;
				}
				
				descList = formatDescription(s.getDescription(player));

				descList.add(0, ChatColor.DARK_GREEN + "" + player.getSkillLevel(s) + "."
						+ ((int) (player.getSkillExperience(s)*100)) + "");
				
				descList.add(0, s.getName());
				
				MenuAction responseAction = null;
				
				
				if (s instanceof CraftingSkill) {
					descList.add(" ");
					descList.add(ChatColor.BLUE + "Click here for recipes");
					responseAction = new ShowSkillRecipesAction(player, (CraftingSkill) s);
				}
				
				inv.addInventoryItem(new BasicInventoryItem(
						s.getIcon(), descList, responseAction
						));
				
				
			}
		}
		
		
		InventoryMenu menu = new InventoryMenu(player, inv);
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(
				player.getPlayer().getPlayer(), menu);
	}
	
	private List<String> formatDescription(String desc) {
		List<String> descList = new LinkedList<>();
		String mid;
		int pos;
		while (desc.length() > 30) {
			
			desc = desc.trim();
			
			pos = desc.indexOf("\n");
			if (pos != -1 && pos < 30) {
				descList.add(ChatColor.WHITE + desc.substring(0, pos));
				desc = desc.substring(pos + 1);
				continue;
			}
			
			//find first space before 30
			mid = desc.substring(0, 30);
			pos = mid.lastIndexOf(" ");
			if (pos == -1) {
				descList.add(ChatColor.WHITE + mid);
				desc = desc.substring(30);
				continue;
			}
			//else we found a space
			descList.add(ChatColor.WHITE + mid.substring(0, pos));
			desc = desc.substring(pos);
		}
		
		descList.add(ChatColor.WHITE + desc.trim());
		
		return descList;
	}
}
