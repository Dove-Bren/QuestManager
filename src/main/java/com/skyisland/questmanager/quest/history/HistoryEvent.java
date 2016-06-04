package com.skyisland.questmanager.quest.history;

/**
 * Keeps track of a single event in a history.
 * @author Skyler
 *
 */
public class HistoryEvent {
	
	private String desc;
	
	public HistoryEvent(String description) {
		this.desc = description;
	}
	
	public void updateDescription(String description) {
		this.desc = description;
	}
	
	/**
	 * Returns this event's description
	 * @return it's description, or an empty string
	 */
	public String getDescription() {
		return desc == null ? "" : desc;
	}
}
