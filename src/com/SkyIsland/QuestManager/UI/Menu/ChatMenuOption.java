package com.SkyIsland.QuestManager.UI.Menu;

import com.SkyIsland.QuestManager.Fanciful.FancyMessage;
import com.SkyIsland.QuestManager.UI.Menu.Action.MenuAction;
import com.SkyIsland.QuestManager.UI.Menu.Message.Message;

/**
 * An 'option' for a chat menu.<br />
 * This includes a label for the option and a corresponding action to be executed upon selection of that
 * option.
 * @author Skyler
 *
 */
public class ChatMenuOption {
	
	private Message label;
	
	private FancyMessage tooltip;
	
	private MenuAction action;
	
	public ChatMenuOption(Message message, MenuAction action, FancyMessage tooltip) {
		this.label = message;
		this.action = action;
		this.tooltip = tooltip;
	}
	
	public ChatMenuOption(Message message, MenuAction action) {
		this(message, action, null);
	}
	
	/**
	 * @return the label
	 */
	public Message getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(Message label) {
		this.label = label;
	}

	/**
	 * @return the action
	 */
	public MenuAction getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(MenuAction action) {
		this.action = action;
	}
	
	public void setTooltip(FancyMessage tooltip) {
		this.tooltip = tooltip;
	}
	
	public FancyMessage getTooltip() {
		return tooltip;
	}
}
