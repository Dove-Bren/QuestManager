package com.skyisland.questmanager.ui.menu.message;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.skyisland.questmanager.fanciful.FancyMessage;

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
