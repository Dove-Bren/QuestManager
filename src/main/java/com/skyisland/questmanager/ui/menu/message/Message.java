package com.skyisland.questmanager.ui.menu.message;

import com.skyisland.questmanager.fanciful.FancyMessage;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

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
