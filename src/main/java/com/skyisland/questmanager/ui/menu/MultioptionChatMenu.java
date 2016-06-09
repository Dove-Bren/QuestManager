package com.skyisland.questmanager.ui.menu;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.ui.ChatMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.ui.ChatGuiHandler;
import com.skyisland.questmanager.ui.menu.message.Message;

public class MultioptionChatMenu extends ChatMenu implements RespondableMenu {
	
	/**
	 * A map between unique menu keys and options
	 */
	private Map<String, ChatMenuOption> options;
	
	private int keyindex;
	
	/**
	 * Creates (but does not show!) a menu with zero or more options. Each option is associated with<br />
	 * The provided MenuActions allow for more control over the action of the menu buttons. If
	 * there is no desired action for a corresponding action, <i>null</i> should be passed.
	 * @param msg The fully-encoded message used for menu text
	 * @param opt1 Action enacted when option 1 is clicked by the user
	 * @param opt2 Action enacted when option 2 is clicked by the user
	 */
	public MultioptionChatMenu(Message body, ChatMenuOption option) {
		super(body.getFormattedMessage());
		
		options = new TreeMap<>();
		addOption(option);
		
		keyindex = 1;
		
		this.setMessage(formatMessage(body));
	}
	
	public MultioptionChatMenu(Message body) {
		super(body.getFormattedMessage());
		
		options = new TreeMap<>();
		
		keyindex = 1;
		
		this.setMessage(formatMessage(body));
	}
	
	public MultioptionChatMenu(Message body, ChatMenuOption ... options) {
		super(body.getFormattedMessage());

		
		this.options = new TreeMap<>();
		
		keyindex = 1;
		
		for (ChatMenuOption opt : options) {
			addOption(opt);
		}
		
		this.setMessage(formatMessage(body));
	}
	
	public MultioptionChatMenu(Message body, Collection<ChatMenuOption> options) {
		super(body.getFormattedMessage());

		
		this.options = new TreeMap<>();
		
		keyindex = 1;

		options.forEach(this::addOption);
		
		this.setMessage(formatMessage(body));
	}
	
	/**
	 * Adds the given option to the list of options used in the menu.
	 * @param option
	 */
	private void addOption(ChatMenuOption option) {
		this.options.put(genKey(), option);
	}

	@Override
	protected boolean input(Player player, String arg) {
		
		if (options.isEmpty()) {
			return false;
		}
		
		for (String key : options.keySet()) {
			if (key.equals(arg)) {
				ChatMenuOption opt = options.get(key);
				opt.getAction().onAction();
				return true;
			}
		}
		
		
		player.sendMessage("Something went wrong! [Invalid Mopt Argument!]");
		return false;
		
	}
	
	/**
	 * Uses the internal key index to generate the next key for registration
	 * @return
	 */
	private String genKey() {
		String key = "M" + keyindex;
		keyindex++;
		
		return key;
		//return "M" + keyindex++;
	}

	private FancyMessage formatMessage(Message rawBody) {
		FancyMessage msg = new FancyMessage("--------------------------------------------\n")
					.style(ChatColor.BOLD)
				.then(rawBody.getFormattedMessage())
				.then("\n\n");
				
		if (!options.isEmpty())
		for (String key : options.keySet()) {
			ChatMenuOption opt = options.get(key);
			msg.then("    ")
			.then(opt.getLabel().getFormattedMessage()).command(ChatGuiHandler.cmdBase + " " + key)
				.color(ChatColor.DARK_GREEN)
				.style(ChatColor.ITALIC);
			if (opt.getTooltip() != null) {
				msg.formattedTooltip(opt.getTooltip());
			}
		}
		
		msg.then("--------------------------------------------\n")
		.style(ChatColor.BOLD);
		
		return msg;
	}
}
