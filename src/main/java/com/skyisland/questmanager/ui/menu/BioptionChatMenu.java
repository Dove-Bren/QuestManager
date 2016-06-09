package com.skyisland.questmanager.ui.menu;

import com.skyisland.questmanager.fanciful.FancyMessage;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.action.MenuAction;
import com.skyisland.questmanager.ui.menu.message.BioptionMessage;

public class BioptionChatMenu extends ChatMenu implements RespondableMenu {
	
	private MenuAction opt1;
	
	private MenuAction opt2;
	
	private BioptionMessage messageCache;
	
	/**
	 * Creates (but does not show!) a menu with two options. Menu message, option labels, and 
	 * responses to each option are loaded from the passed 
	 * {@link BioptionMessage BioptionMessage}.
	 * The provided MenuActions allow for more control over the action of the menu buttons. If
	 * there is no desired action for a corresponding action, <i>null</i> should be passed.
	 * @param msg The fully-encoded message used for menu text
	 * @param opt1 Action enacted when option 1 is clicked by the user
	 * @param opt2 Action enacted when option 2 is clicked by the user
	 */
	public BioptionChatMenu(BioptionMessage msg, MenuAction opt1, MenuAction opt2) {
		super(msg.getFormattedMessage());
		this.opt1 = opt1;
		this.opt2 = opt2;
		messageCache = msg;
	}
	
	private BioptionChatMenu(FancyMessage msg) {
		super(msg);
	}

	@Override
	protected boolean input(Player player, String arg) {

		//do different things based on our argument. We are only bioption, so we only have
		//two things to do. 
		if (arg.equals(BioptionMessage.OPTION1)) {
			
			
			if (messageCache.getResponse1() != null) {
				SimpleChatMenu menu = new SimpleChatMenu(messageCache.getResponse1());
				menu.show(player);
			}

			if (opt1 != null) {
				opt1.onAction();
			}
			
			return true;
		} else if (arg.equals(BioptionMessage.OPTION2)) {
			
			
			if (messageCache.getResponse2() != null) {
				SimpleChatMenu menu = new SimpleChatMenu(messageCache.getResponse2());
				menu.show(player);
			}
			
			if (opt2 != null) {
				opt2.onAction();
			}
			
			return true;
		} else {
			player.sendMessage("Something went wrong! [Invalid Biopt Argument!]");
			return false;
		}
	}
}
