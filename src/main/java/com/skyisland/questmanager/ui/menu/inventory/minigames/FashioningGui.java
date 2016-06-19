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

package com.skyisland.questmanager.ui.menu.inventory.minigames;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.effects.ChargeEffect;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.QualityItem;
import com.skyisland.questmanager.player.skill.defaults.FashioningSkill;
import com.skyisland.questmanager.player.skill.defaults.FashioningSkill.FashioningRecipe;
import com.skyisland.questmanager.ui.menu.inventory.GuiInventory;
import com.skyisland.questmanager.ui.menu.inventory.InventoryItem;

public class FashioningGui extends GuiInventory {
	
	public static final Random RANDOM = new Random();
	
	public static final Sound FASHION_SOUND = Sound.ITEM_ARMOR_EQUIP_LEATHER;
	/////
	public static final Sound FAIL_SOUND = Sound.ENTITY_CAT_DEATH;
	
	public static final Sound CLICK_SOUND = Sound.UI_BUTTON_CLICK;
	
	public static final String FAIL_MESSAGE = ChatColor.RED + "The recipe was good, but you lack the skill for that craft";
	
	public static final String NO_RECIPE_MESSAGE = ChatColor.RED + "The combination didn't result in anything useful";
	
	public static final String WIN_MESSAGE = "You created ";
	
	private static final ChargeEffect SUCCEED_EFFECT = new ChargeEffect(Effect.HAPPY_VILLAGER);
	
	private static final ChargeEffect FAIL_EFFECT = new ChargeEffect(Effect.VILLAGER_THUNDERCLOUD);
	
	private Player player;
	
	private FashioningSkill skillLink;
	
	private Inventory inv;
	
	public FashioningGui(FashioningSkill skill, Player player, String name) {
		if (skill == null) {
			QuestManagerPlugin.logger.warning("Fashioning Guis without skills "
					+ "do nothing. This fasioning gui is refusing to start");
			return;
		}
		this.player = player;
		this.inv = Bukkit.createInventory(player, InventoryType.ANVIL, name);
		this.skillLink = skill;
	}
	
	@Override
	public Inventory getFormattedInventory(QuestPlayer player) {
		return inv;
	}

	@Override
	public Map<String, Object> serialize() {
		return null; //Runtime only, not serializable
	}

	@Override
	public InventoryItem getItem(int pos) {
		if (skillLink == null) 
			return null;
		
		/*
		 * 0,1 are ingredient slots
		 * 2 is result slot
		 */
		
		if (pos == 2) {
			submit();
			player.updateInventory();
			return null;
		}
		
		if (pos > 2)
			submitItem(pos + 6);
		else {
			//ingredient slot. taking back?
			if (inv.getItem(pos) != null) {
				player.getInventory().addItem(inv.getItem(pos));
				inv.setItem(pos, null);
			}
		}
		
		QualityItem result = getResult(true);
		if (result == null) {
			inv.setItem(2, null);
			return null;
		}
		
		
		inv.setItem(2, result.getItem());
		
		return null; //tell it to do nothing
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FashioningGui) {
			return ((FashioningGui) o).player.getUniqueId().equals(player.getUniqueId());
		}
		
		return false;
	}
	
	private void submit() {
		//get all our items and look for a recipe, and then try to perform it
		
		if (inv.getItem(0) == null || inv.getItem(1) == null)
			return;
		
		FashioningRecipe recipe = skillLink.getRecipe(inv.getItem(0), inv.getItem(1));
		if (recipe == null) {

			inv.setItem(0, null);
			inv.setItem(1, null);
			inv.setItem(2, null);
			player.sendMessage(NO_RECIPE_MESSAGE);
			return;
		}
		

		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		QualityItem result = getResult(false);
		

		inv.setItem(0, null);
		inv.setItem(1, null);
		inv.setItem(2, null);
		
		double chance = skillLink.getFashioningChance(qp, recipe);
		boolean fail = false;
		if (RANDOM.nextDouble() >= chance) {
			fail = true;
		}
		
		if (fail) {
			player.sendMessage(FAIL_MESSAGE);
			FAIL_EFFECT.play(player, player.getLocation());
			player.getWorld().playSound(player.getLocation(), FAIL_SOUND, 1, 1);
			skillLink.performMinor(qp, recipe.difficulty, true);
			return;
		}
		
		//skillLink.perform(qp, recipe.difficulty, false);
		SUCCEED_EFFECT.play(player, player.getLocation());
		skillLink.performMinor(qp, recipe.difficulty, false);
		
		
		String name;
		if (result.getItem().getItemMeta() == null || result.getItem().getItemMeta().getDisplayName() == null) {
			name = YamlWriter.toStandardFormat(result.getItem().getType().toString());
		} else {
			name = result.getItem().getItemMeta().getDisplayName();
		}
		
		FancyMessage msg = new FancyMessage(WIN_MESSAGE)
				.color(ChatColor.GREEN)
			.then(result.getItem().getAmount() > 1 ? result.getItem().getAmount() + "x " : "a ")
			.then("[" + name + "]")
				.color(ChatColor.DARK_PURPLE)
				.itemTooltip(result.getItem());
		
		
		msg.send(player);
		player.getWorld().playSound(player.getEyeLocation(), FASHION_SOUND, 1, 1);
		if (!(player.getInventory().addItem(result.getItem())).isEmpty()) {
			player.sendMessage(ChatColor.RED + "There is no space left in your inventory");
			player.getWorld().dropItem(player.getEyeLocation(), result.getItem());
		}
	}
	
	private void submitItem(int slot) {
		
		int pos = -1; //where we're going to insert it. 0-2
		for (int i = 0; i < 2; i++) {
			if (inv.getItem(i) == null) {
				pos = i;
				break;
			}
		}
		
		if (pos == -1) {
			//no space, do nothing
			return;
		}
		
		slot = slot % 36;
		

		if (player.getInventory().getItem(slot) == null)
			return;
		
		ItemStack item = player.getInventory().getItem(slot);
		ItemStack replace = null;
		if (item.getAmount() > 1) {
			replace = new ItemStack(item);
			replace.setAmount(item.getAmount() - 1);
			replace.setItemMeta(item.getItemMeta().clone());
		}
		
		player.getInventory().setItem(slot, replace);
		
		item.setAmount(1);
		inv.setItem(pos, item);
		
	}
	
	private QualityItem getResult(boolean printInfo) {
		if (skillLink == null)
			return null;
		
		if (inv.getItem(0) == null && inv.getItem(1) == null && inv.getItem(2) == null)
			return null;
		
		List<ItemStack> args = new ArrayList<>(3);
		args.add(inv.getItem(0) == null ? null : inv.getItem(0).clone());
		args.add(inv.getItem(1) == null ? null : inv.getItem(1).clone());
		args.add(inv.getItem(2) == null ? null : inv.getItem(2).clone());
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		FashioningRecipe recipe = skillLink.getRecipe(inv.getItem(0), inv.getItem(1));
		if (recipe == null) {
			return null;
		} else {
			QualityItem result = new QualityItem(recipe.result.clone());
			
			double sum = 0;
			int count = 0;
			QualityItem qi;
			for (ItemStack item : args) {
				if (item == null)
					continue;
				qi = new QualityItem(item);
				count ++;
				sum += qi.getQuality();
			}
			
			result.setQuality(Math.max(0, sum / (double) count));
			
			if (printInfo) {
				ItemMeta meta = result.getUnderlyingItem().getItemMeta();
				List<String> lore = meta.getLore();
				if (lore == null)
					lore = new LinkedList<>();
				int level = qp.getSkillLevel(skillLink);
				int cutoff = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillCutoff();
				
				String builder;
				lore.add("  -  ");
				builder = "";
				if (level - recipe.difficulty > cutoff)
					builder += ChatColor.GREEN;
				else if (recipe.difficulty - level > 
					QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSkillUpperCutoff())
					builder += ChatColor.RED;
				else
					builder += ChatColor.BLUE;
				builder += "Difficulty: " + recipe.difficulty;
				lore.add(builder);
				builder = ChatColor.YELLOW + "";
				double chance = skillLink.getFashioningChance(qp, recipe);
				if (chance > .9)
					builder = ChatColor.GREEN + "";
				
				builder += String.format("Success Chance: %.2f", chance);
				lore.add(builder);				
				
				meta.setLore(lore);
				result.getUnderlyingItem().setItemMeta(meta);
			}
			
			return result;
		}
		
	}
	
	
}
