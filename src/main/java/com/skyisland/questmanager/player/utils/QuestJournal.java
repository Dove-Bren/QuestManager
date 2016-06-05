package com.skyisland.questmanager.player.utils;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.quest.history.HistoryEvent;

/**
 * A quest journal keeps track of the current target quest's history<br />
 * This class provides nice helper functions for making that happen
 * @author Skyler
 * @see QuestLog
 */
public class QuestJournal {
	
	public static final String escq = "\\\"";
	
	public static void addQuestJournal(QuestPlayer qp) {
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
		
		for (ItemStack item : inv.all(Material.BOOK_AND_QUILL).values()) {
			if (item.hasItemMeta()) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.hasTitle() && meta.getTitle().equals("Journal")
						&& meta.hasAuthor() && meta.getAuthor().equals(play.getName())
						&& item.getEnchantmentLevel(Enchantment.LUCK) == 5) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
		
			book = new ItemStack(Material.BOOK_AND_QUILL);
			BookMeta bookMeta = (BookMeta) book.getItemMeta();
			
			bookMeta.setTitle("Journal");
			bookMeta.setDisplayName("Journal");
			bookMeta.setAuthor(play.getName());
			
			book.setItemMeta(bookMeta);
			
			book.addUnsafeEnchantment(Enchantment.LUCK, 5);
			
			inv.addItem(book);
			
			play.sendMessage(ChatColor.GRAY + "A " + ChatColor.DARK_GREEN 
					+ "Quest Journal" + ChatColor.GRAY + " has been added to your inventory."
					 + ChatColor.RESET);
		}
		
		updateQuestJournal(qp, true);
	}
	
	public static void updateQuestJournal(QuestPlayer qp, boolean silent) {
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
			if (item.hasItemMeta() && item.getType() == Material.BOOK_AND_QUILL) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.hasTitle() && meta.getTitle().equals("Journal")
						&& meta.hasAuthor() && meta.getAuthor().equals(play.getName())
						&& item.getEnchantmentLevel(Enchantment.LUCK) == 5) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
			//they don't have a quest journal
			return;
		}
		
		String builder = "replaceitem entity ";
		builder += play.getName() + " ";
		
		builder += getSlotString(slot) + " writable_book 1 0 ";
		
		//now start putting pages
		builder += "{display:{Name: \"Journal\"},pages:[";
		
		//get title page
		FancyMessage title = new FancyMessage("      Journal\n  " + play.getName() + "\n\n  My own journal with details about my active quest")
				.color(ChatColor.BLACK);
		builder += generatePage(title.toOldMessageFormat());
		
		builder += ",";
		
		//get recent page
		title = new FancyMessage("    Recent events:\n")
					.color(ChatColor.DARK_GREEN);
		List<HistoryEvent> events;
		
		events = qp.getHistory().events();
		
		if (events == null || events.isEmpty()) {
			title.then(" Nothing recent!")
				.color(ChatColor.BLACK);
		} else {
			for (HistoryEvent event : events.subList(Math.max(0, events.size() - 6), events.size())) {
				title.then("-" + event.getDescription() + "\n")
					.color(ChatColor.BLACK);
			}
		}
		
		builder += generatePage(title.toOldMessageFormat());
		
		//add quests
		if (qp.getFocusQuest() == null) {
			builder += ",";
			builder += generatePage("\nYou are not focused on any quest!");
		} else {
			builder += ",";
			builder += generatePage("Your current focus:\n\n" + ChatColor.DARK_PURPLE + qp.getFocusQuest().getName());
			for (HistoryEvent event : qp.getFocusQuest().getHistory().events())  {
				builder += ",";
				builder += generatePage(event.getDescription());
			}
		}
		
		builder += ",";
		
		//add player notes title
		title = new FancyMessage("\n  Player Notes\n\n\n")
				.color(ChatColor.BLACK)
			.then("  Notes left after this page will be kept")
				.color(ChatColor.BLACK);
		
		builder += generatePage(title.toOldMessageFormat());
		
		if (qp.getPlayerNotes() != null && !qp.getPlayerNotes().isEmpty()) {
			for (String page : qp.getPlayerNotes()) {
				if (page.trim().isEmpty()) {
					continue;
				}
				builder += ",";
				builder += generatePage(page);
			}
		}
		
		//bind
		builder += "], title:\"Journal\",author:" + play.getName() + ",ench:[{id:61s,lvl:5s}],HideFlags:1}";

		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), builder);
		
		if (!silent) {
			play.sendMessage(ChatColor.GRAY + "Your "
					+ ChatColor.DARK_GREEN + "Journal" + ChatColor.GRAY + " has been"
					+ " updated!" + ChatColor.RESET);
			play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(0, Tone.G));
			play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(0, Tone.C));
			play.playNote(play.getLocation(), Instrument.PIANO, Note.sharp(0, Tone.D));
		}
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
	
	/**
	 * Used to build pages for primitive strings
	 * @param line
	 * @return
	 */
	private static String generatePage(String line) {
		if (line == null) {
			return "\"\"";	
		}
			String ret;

		if (line.length() > 260) {
			ret = generatePage(line.substring(0, 260));
			ret += ",";
			ret += generatePage(line.substring(260));
		} else {
			ret = "\"";
			ret += line;
			ret += "\"";
		}
		
		
		return ret;
	}
}
