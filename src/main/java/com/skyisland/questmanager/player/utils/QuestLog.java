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

package com.skyisland.questmanager.player.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.CraftingSkill;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.player.skill.defaults.MagerySkill;
import com.skyisland.questmanager.quest.Quest;

/**
 * Utility class for the quest log.
 * Provides nice, simple wrapper functions for the elaborate workings of the Quest Log.
 * 4-1-16 Quest Log will also now hold Skill information
 *
 */
public class QuestLog {
	
	private static String escq = "\\\"";
	
	public static void addQuestlog(QuestPlayer qp) {
		if (!qp.getPlayer().isOnline()) {
			return;
		}
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
				.getWorlds().contains(qp.getPlayer().getPlayer().getWorld().getName())) {
			return;
		}
		
		Player play = qp.getPlayer().getPlayer();
		Inventory inv = play.getInventory();
		
		if (inv.firstEmpty() == -1) {
			//no room!
			return;
		}
		
		ItemStack book = null;
		
		for (ItemStack item : inv.all(Material.WRITTEN_BOOK).values()) {
			if (item.hasItemMeta()) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.getTitle().equals("Quest Log")
						&& meta.getAuthor().equals(play.getName())
						&& item.getEnchantmentLevel(Enchantment.LUCK) == 5) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
		
			book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bookMeta = (BookMeta) book.getItemMeta();
			
			bookMeta.setTitle("Quest Log");
			bookMeta.setAuthor(play.getName());
			bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			
			book.setItemMeta(bookMeta);
			
			book.addUnsafeEnchantment(Enchantment.LUCK, 5);
			
			inv.addItem(book);
			
			play.sendMessage(ChatColor.GRAY + "A " + ChatColor.DARK_GREEN 
					+ "Quest Log" + ChatColor.GRAY + " has been added to your inventory."
					 + ChatColor.RESET);
		}

		
		updateQuestlog(qp, true);
	}
	
	public static void updateQuestlog(QuestPlayer qp, boolean silent) {
		if (!qp.getPlayer().isOnline()) {
			return;
		}
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
				.getWorlds().contains(qp.getPlayer().getPlayer().getWorld().getName())) {
			return;
		}
		
		Player play = qp.getPlayer().getPlayer();
		Inventory inv = play.getInventory();
		ItemStack book = null;
		int slot = 0;
		
		for (slot = 0; slot <= 35; slot++) {
			ItemStack item = inv.getItem(slot);
			if (item == null || item.getType() == Material.AIR) {
				continue;
			}
			if (item.hasItemMeta() && item.getType() == Material.WRITTEN_BOOK) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.getTitle().equals("Quest Log")
						&& meta.getAuthor().equals(play.getName())
						&& item.getEnchantmentLevel(Enchantment.LUCK) == 5) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
			//they don't have a quest log
			return;
		}
		
		String builder = "replaceitem entity ";
		builder += play.getName() + " ";
		
		builder += getSlotString(slot) + " written_book 1 0 ";
		
		//now start putting pages
		builder += "{pages:[";
		
		//get title page
		FancyMessage title = new FancyMessage("      Quest Log\n\n\n  This book details your current quest progress & history.")
				.color(ChatColor.BLACK);
		builder += generatePageJSON(title.toJSONString().replace("\"", escq));
		
		builder += ",";
		
		//get stats page
		title = new FancyMessage(qp.getPlayer().getName())
				.color(ChatColor.DARK_PURPLE)
			.then("\n")
			.then(qp.getTitle())
				.color(ChatColor.DARK_RED)
				.tooltip(ChatColor.BLUE + "Click to change your title")
				.command("/player title")
			.then("\nLevel: " + qp.getLevel())
				.color(ChatColor.BLACK)
			.then("\n-----\n  Fame: " + qp.getAlphaFame() + " (" + qp.getFame() + ")\n  Gold: " + qp.getMoney())
				.color(ChatColor.GOLD)
			.then("\n\n  Current Quests: " + qp.getCurrentQuests().size())
				.color(ChatColor.DARK_GREEN)
			.then("\n\n  Completed Quests: " + qp.getCompletedQuests().size())
				.color(ChatColor.DARK_BLUE)
				.tooltip(qp.getCompletedQuests())
			.then("\n\n\n\n         Options")
				.color(ChatColor.DARK_GRAY)
				.tooltip(ChatColor.BLUE + "Click to manage options")
				.command("/player options");
		
		builder += generatePageJSON(title.toJSONString().replace("\"", escq));
		
		
		if (qp.getOptions().getOption(PlayerOptions.Key.SKILL_LIST)) {
			title = new FancyMessage("        Skills")
					.color(ChatColor.BLACK)
					.tooltip(ChatColor.BLUE + "Click here to open the skills menu")
					.command("/player skills");
			int lines = 0;
			
			
			//combat skills
			for (Skill.Type type : Skill.Type.values()) {
				
				title.then("\n\n" + toNormalCase(type.name()))
					.color(ChatColor.DARK_RED).style(ChatColor.BOLD);
				lines += 2;
				boolean spoil = qp.getOptions().getOption(PlayerOptions.Key.SKILL_REVEAL);
				for (Skill s : QuestManagerPlugin.questManagerPlugin.getSkillManager().getSkills(type)) {
					//get a formatted description. (Code from QuestPlayer's magic menu)
					
					if (!spoil && qp.getSkillLevel(s) <= 0 && qp.getSkillExperience(s) <= 0) {
						continue;
					}
					
					List<String> descList = new LinkedList<>();
					String desc;
					desc = s.getDescription(qp);
					
					String mid;
					int pos;
					while (desc.length() > 30) {
						
						desc = desc.trim();
						
						//first, check for newline before 30 limit
						pos = desc.substring(0, 30).indexOf("\n");
						if (pos != -1) {
							//there's a newline, so split before it
							//[and some sting\nwith a newline]
							mid = desc.substring(0, pos);
							mid = mid.substring(0, 1 + mid.length() - (("\n").length())); //chop off the \n
							descList.add(mid);
							desc = desc.substring(pos);
							continue;
						}
						
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
					
					desc = "";
					for (int i = 0; i < descList.size() - 1; i++) {
						desc += descList.get(i) + "\n";
					}
					desc += descList.get(descList.size() - 1);
					
					
					if (lines > 12) {
						//not enough room on the page
						//write page, continue
						builder += ", ";
						builder += generatePageJSON(title.toJSONString().replace("\"", escq));
						lines = 1;
						title = new FancyMessage("\n  " + s.getName())
								.color(ChatColor.BLACK);
						
						if (s instanceof CraftingSkill) {
							title.tooltip(desc + "\n\n" + ChatColor.BLUE + "Click here for recipes")
							.command("/player recipe " + s.getName());
						} else if (s instanceof MagerySkill) {
							title.tooltip(desc + "\n\n" + ChatColor.BLUE + "Click here to see your spells")
							.command("/player spells");
						} else {
							title.tooltip(desc);
						}
						
						title.then(" " + qp.getSkillLevel(s) + "."
								+ ((int) (qp.getSkillExperience(s)*100)) + "")
								.color(ChatColor.DARK_GREEN);
					} else {
						title.then("\n  " + s.getName())
								.color(ChatColor.BLACK);
						
						if (s instanceof CraftingSkill) {
							title.tooltip(desc + "\n\n" + ChatColor.BLUE + "Click here for recipes")
							.command("/player recipe " + s.getName());
						} else {
							title.tooltip(desc);
						}
						
						title.then(" " + qp.getSkillLevel(s) + "."
								+ ((int) (qp.getSkillExperience(s)*100)) + "")
								.color(ChatColor.DARK_GREEN);
						lines++;
					}
				}
			}
		builder += ", ";
		builder += generatePageJSON(title.toJSONString().replace("\"", escq));
		}
		
		
		
		//13 lines
		
		//add quests
		if (qp.getCurrentQuests().isEmpty()) {
			builder += ",";
			builder += generatePage("\nYou do not have any active quests!");
		} else {
			for (Quest quest : qp.getCurrentQuests())  {
				builder += ",";
				builder += generatePageJSON(quest.getJSONDescription().replace("\"", escq));
			}
		}
		
		
		//bind
		builder += "], title:\"Quest Log\",author:" + play.getName() + ",ench:[{id:61s,lvl:5s}],HideFlags:1}";

		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), builder);
		
		if (!silent) {
			play.sendMessage(ChatColor.GRAY + "Your "
					+ ChatColor.DARK_GREEN + "Quest Log" + ChatColor.GRAY + " has been"
					+ " updated!" + ChatColor.RESET);

			play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.D));
			play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.G));
			play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.B));
		}
		
		play.setLevel(qp.getMoney());
	}
	
	private static String getSlotString(int rawslot) {
		if (rawslot < 0) {
			return "invalid.slot";
		}
		if (rawslot < 9) {
			return "slot.hotbar." + rawslot;
		}
		
		return "slot.inventory." + (rawslot - 9);
	}
	
	
	private static String generatePageJSON(String JSON) {
		String ret = "\"[" + escq + escq + ",";
		
		if (JSON != null) {
			ret += JSON;
		}
		
		ret += "]\"";
		
		return ret;
	}
	
	/**
	 * Used to build pages for primitive strings
	 */
	private static String generatePage(String line) {
		String ret = "\"[" + escq + escq + ",";
		
		if (line != null) {
			ret += formatText(line);
		}
		
		ret += "]\"";
		
		return ret;
	}
	
	@SuppressWarnings("unused")
	private static String generatePage(List<String> lines) {
		String ret = "\"[" + escq + escq + ",";
		
		String line = "";
		
		if (lines != null && !lines.isEmpty()) {
			Iterator<String> it = lines.iterator();
			
			while (it.hasNext()) {
				line += (it.next());
				if (it.hasNext()) {
					line += "\n";
				}
			}
		}
			
		ret += formatText(line);
		ret += "]\"";
		
		return ret;
		
		//"[\"\",{\"text\":\"Title Page\"},{\"text\":\"page1\"}]"
	}
		
	private static String formatText(String str) {
		return "{" + escq + "text" + escq + ": " + escq + str + escq + "}";
	}
	
	private static String toNormalCase(String wonkWord) {
		return (wonkWord.substring(0, 1).toUpperCase()) + (wonkWord.substring(1).toLowerCase());
	}
}
