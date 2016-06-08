package com.skyisland.questmanager.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.ui.menu.RespondableMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Organizes, catches, and dispatches chat click events to the responsible menus
 * @author Skyler
 *
 */
public class ChatGuiHandler implements CommandExecutor, UITickable {
	
	public final static CharSequence idReplace = "=ID=";
	
	public final static CharSequence cmdBase = "=CMD=";
		
	public enum Commands {
		MENU("qmchatmenu");
		
		private String command;
		
		Commands(String command) {
			this.command = command;
		}
		
		public String getCommand() {
			return command;
		}
	}
	
	/**
	 * Internal record class for bringing together all required information about a menu.
	 * @author Skyler
	 *
	 */
	private static class MenuRecord {
		
		private ChatMenu menu;
		
		private boolean ticked;
		
		private int key;
		
		public MenuRecord(ChatMenu menu, int key) {
			this.menu = menu;
			this.key = key;
			
			this.ticked = false;
		}

		/**
		 * @return the menu
		 */
		public ChatMenu getMenu() {
			return menu;
		}

		/**
		 * @return the ticked
		 */
		public boolean isTicked() {
			return ticked;
		}

		/**
		 * @param ticked the ticked to set
		 */
		public void tick() {
			this.ticked = true;
		}

		/**
		 * @return the key
		 */
		public int getKey() {
			return key;
		}
		
		
	}
	
	private Map<UUID, MenuRecord> menus;
	
	private static Random rand;
	
	/**
	 * Should we send messages about expired messages?
	 */
	private boolean verboseMode;
	
	/**
	 * Creates a new GUI Handler for the provided plugin.
	 * @param plugin 
	 * @param verboseMode Whether or not to send messages to players about expires menus
	 */
	public ChatGuiHandler(JavaPlugin plugin, boolean verboseMode) {
		for (Commands command : Commands.values()) {
			plugin.getCommand(command.getCommand()).setExecutor(this);
		}
		
		menus = new TreeMap<>();
		if (ChatGuiHandler.rand == null) {
			ChatGuiHandler.rand = new Random();
		}
		
		//schedule ourselves for ticking
		UIScheduler.getScheduler().schedule(this, 10.0f);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase(Commands.MENU.getCommand())) {
			if (args.length != 2) {
				sender.sendMessage("Something went wrong! [Invalid Option Length!]");
				return false;
			}
			return menuCommand(sender, args);
		}
		
		
		return false;
		
	}
	
	/**
	 * Executes the menu command with the given parameters
	 * @param sender Who send the command
	 * @param args All passes arguments
	 * @return 
	 */
	private boolean menuCommand(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can execute this command!");
			return true;
		}
		
		int menuID;
		try {
			menuID = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		String arg = args[1];
		
		Player player = (Player) sender;
		
		if (!menus.containsKey(player.getUniqueId())) {
			if (verboseMode) {
				sender.sendMessage("This menu has expired!");
			}
			return true;
		}
		
		MenuRecord record = menus.get(player.getUniqueId());
		
		if (record.getKey() != menuID) {
			if (verboseMode)  {
				sender.sendMessage("This menu has expired!");
			}
			return true;
		}
		
		ChatMenu menu = record.getMenu();
		menus.remove(player.getUniqueId());
		
		return menu.input(player, arg);
		
	}
	
	
	
	/**
	 * Registers the menu 
	 * @param menu
	 */
	public void showMenu(Player player, ChatMenu menu) {
		if (player == null || menu == null) {
			return;
		}
		
		if (!player.isOnline()) {
			return;
		}
		
		int id = rand.nextInt();
		
		
		FancyMessage preformat = new FancyMessage("").then(menu.getMessage());
		String raw = preformat.toJSONString();
		raw = raw.replace(cmdBase, "/" + Commands.MENU.getCommand() + " " + id + " ");
		raw = raw.replace(idReplace, "" + id);
		FancyMessage postformat = FancyMessage.deserialize(raw);
		
		postformat.send(player);
		
		if (menu instanceof RespondableMenu) {
			menus.put(player.getUniqueId(), 
				new MenuRecord(menu, id));
		}
		
	}
	
	/**
	 * Performs internal timer check and update on menu entries.<br />
	 * Calling this method in a manner inconsistent with a timeout timer results in 
	 * undefined behavior, and is not recommended.
	 */
	public void tick() {
		
		if (menus.isEmpty()) {
			return;
		}

		for (UUID key : (new ArrayList<>(menus.keySet()))) {
			MenuRecord record = menus.get(key);

			//check if they've already been ticked
			if (record.isTicked()) {
				menus.remove(key);
				Bukkit.getPlayer(key).sendMessage(
					ChatColor.GRAY + "Your menu has expired.");
			} else {
				record.tick();
			}
		}
	}
}
