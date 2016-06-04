package com.SkyIsland.QuestManager.UI.Menu.Message;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.SkyIsland.QuestManager.Fanciful.FancyMessage;

/**
 * Holds a messaged used with a menu.
 * @author Skyler
 *
 */
public abstract class Message implements ConfigurationSerializable {
	
	protected FancyMessage sourceLabel;
	
	public void setSourceLabel(FancyMessage label) {
		this.sourceLabel = label;
	}
	
	public FancyMessage getSourceLabel() {
		return sourceLabel;
	}
	
	public abstract FancyMessage getFormattedMessage();
	
}
