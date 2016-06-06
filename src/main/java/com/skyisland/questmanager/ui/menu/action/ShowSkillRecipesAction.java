package com.skyisland.questmanager.ui.menu.action;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.CraftingSkill;
import com.skyisland.questmanager.player.skill.SkillRecipe;
import com.skyisland.questmanager.ui.menu.InventoryMenu;
import com.skyisland.questmanager.ui.menu.inventory.BasicInventory;
import com.skyisland.questmanager.ui.menu.inventory.BasicInventoryItem;

public class ShowSkillRecipesAction implements MenuAction {
	
	private CraftingSkill skill;
	
	private QuestPlayer player;
	
	public ShowSkillRecipesAction(QuestPlayer player, CraftingSkill skill) {
		this.skill = skill;
		this.player = player;
	}
	
	@Override
	public void onAction() {
		BasicInventory inv = new BasicInventory();
		List<String> desc;
		String name;
		for (SkillRecipe recipe : skill.getRecipes()) {
			name = YamlWriter.toStandardFormat(recipe.getDisplay().getType().name());
			if (recipe.getDisplay().hasItemMeta() && recipe.getDisplay().getItemMeta().hasDisplayName())
				name = recipe.getDisplay().getItemMeta().getDisplayName();
			
			desc = formatDescription(recipe.getDescription());
			desc.add(0, ChatColor.LIGHT_PURPLE + name);
			inv.addInventoryItem(new BasicInventoryItem(
					recipe.getDisplay(), desc, null
					));
			
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
		
		return descList;
	}
}
