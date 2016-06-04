package com.skyisland.questmanager.player;

import java.util.Collection;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * An entity involved in a quest.<br />
 * Specifically, a participant can either be a single player or a collection of players. It's
 * up to specific implementations of quests & requirements to specify which are allowed.
 * @author Skyler
 *
 */
public interface Participant extends ConfigurationSerializable {
	
	/**
	 * Return the involved participants. This can either be a collection of one element,
	 * or a larger set of a group of participants.<br />
	 * The returned collection is intended to be iterated over and a particular action/check
	 * performed on each member.
	 * @return
	 * @todo decouple implementation :'(
	 */
	public Collection<QuestPlayer> getParticipants();
	
	/**
	 * Get a string-version of the ID that can be used to identify this Participant.
	 * @return
	 */
	public String getIDString();
	
}
