package com.skyisland.questmanager.ui;

import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.ui.menu.BioptionChatMenu;
import com.skyisland.questmanager.ui.menu.message.TreeMessage;
import com.skyisland.questmanager.ui.menu.SimpleChatMenu;
import com.skyisland.questmanager.ui.menu.TreeChatMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.quest.Quest;
import com.skyisland.questmanager.quest.history.HistoryEvent;
import com.skyisland.questmanager.ui.menu.message.BioptionMessage;
import com.skyisland.questmanager.ui.menu.message.Message;

/**
 * A menu represented in chat buttons and links
 * @author Skyler
 *
 * register it with the handler, give it a unique ID and then do specific things based on argument
 * to command received!
 */
public abstract class ChatMenu {
	
	/*
	 * =========================
	 * How to load? How are we gonna load this stuff? If the FancyMessages really are
	 * serializable to config, I can just leave the format for loading/saving up to them. And
	 * just work with how to attach said messages to NPCs and make it do stuff.
	 * 
	 * For example, how from config do you specify that message 'a' starts you on a quest?
	 * It might be worth it to have quest templates specify a special starting NPC that is
	 * created when the template is loaded (and removed when unloaded) and give them a special
	 * menu (child of this, StartQuestMenu or something)
	 * 
	 * How to hook up messages for anything else is harder. Maybe first step is just get the
	 * starting-quest menus to work and don't worry about menus mid-quest for anything.
	 * 
	 */
	
	public static ChatGuiHandler handler;
	
	private FancyMessage message;
	
	private Quest questBacker;
	
	/**
	 * Constructs a menu around the provided FancyMessage.
	 */
	public ChatMenu(FancyMessage msg) {
		this.message = msg;
		
		if (ChatMenu.handler == null) {
			handler = QuestManagerPlugin.questManagerPlugin.getChatGuiHandler();
		}
	}
	
	/**
	 * Sets this menu to be backed by the provided quest.
	 * Backed menus will be logged into backer quests' histories.
	 */
	public void setQuestBacker(Quest quest) {
		this.questBacker = quest;
	}
	
	public Quest getQuestBacker() {
		return questBacker;
	}
	
	/**
	 * Shows this menu to the provided player.
	 */
	public void show(Player player) {
		show(player, questBacker);
	}
	
	/**
	 * Shows this menu to the provided player and logs the menu's outcome into a history event
	 * for the provided quest
	 * @param updateQuest The quest to log this menu under, or null for non-quest menues
	 */
	public void show(Player player, Quest updateQuest) {
		handler.showMenu(player, this);
		
		if (updateQuest == null) {
			return;
		}
		
		this.questBacker = updateQuest;
		updateQuestHistory(updateQuest, message.toOldMessageFormat()
				.replaceAll(ChatColor.WHITE + "", ChatColor.BLACK + ""));
	}
	
	
	protected abstract boolean input(Player player, String arg);
	
	public FancyMessage getMessage() {
		return message;
	}
	
	protected void setMessage(FancyMessage message) {
		this.message = message;
	}
	
	/**
	 * Defines the menu that goes with most standard message types.
	 * If you do not register your own message types here, they will always default
	 * to simple message menus when using this command!
	 * <p>
	 * It's important to note that any menus created from this command are not allowed to be
	 * provided menu actions. For example, the {@link BioptionChatMenu} that would be instantiated
	 * from a {@link BioptionMessage} would have its menu options as null, meaning no action
	 * would be executed when the options were clicked.
	 * </p>
	 */
	public static ChatMenu getDefaultMenu(Message message) {
		
		if (message instanceof BioptionMessage) {
			return new BioptionChatMenu((BioptionMessage) message, null, null);
		}

		if (message instanceof TreeMessage) {
			return new TreeChatMenu((TreeMessage) message);
		}
		
		
		
		
		//if message instanceof SimpleMessage (or DEFAULT)
		return new SimpleChatMenu(message.getFormattedMessage());
		
	}
	
	private void updateQuestHistory(Quest quest, String desc) {
		if (quest == null || desc == null) {
			return;
		}
		desc = desc.replace("-", "");

		for (HistoryEvent event : quest.getHistory().events()) {
			if (ChatColor.stripColor(event.getDescription()).equals(ChatColor.stripColor(desc))) {
				return; //already in there
			}
		}
		
		//wasn't in there, so add one
		quest.addHistoryEvent(new HistoryEvent(desc));
	}
}
