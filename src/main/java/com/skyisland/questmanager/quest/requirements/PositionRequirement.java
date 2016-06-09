package com.skyisland.questmanager.quest.requirements;

import com.skyisland.questmanager.player.utils.CompassTrackable;
import com.skyisland.questmanager.quest.Goal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.LocationState;
import com.skyisland.questmanager.player.Participant;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;

/**
 * Requirement that a participant must be at the provided location.<br />
 * Unlike the {@link ArriveRequirement} this requirement is that someone be located there
 * for this to be completed. That means if they leave, the requirement will no longer
 * be satisfied!
 * @author Skyler
 * @see {@link ArriveRequirement}
 */
public class PositionRequirement extends Requirement implements Listener, CompassTrackable {
	
	public static class PositionFactory extends RequirementFactory<PositionRequirement> {
		
		public PositionRequirement fromConfig(Goal goal, ConfigurationSection config) {
			PositionRequirement req = new PositionRequirement(goal);
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	
	/**
	 * Where the participant must be
	 */
	private Location destination;
	
	/**
	 * How close they can be to the destination to count as satisfied
	 */
	private double targetRange;
	
	private PositionRequirement(Goal goal) {
		super(goal);
	}
	
	public PositionRequirement(Goal goal, String description, Participant participants, Location destination, double range) {
		super(goal, description);
		this.participants = participants;
		this.destination = destination;
		this.targetRange = range;
		this.state = false;
		
	}
	
	@Override
	public void activate() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}

	/**
	 * @return the participants
	 */
	public Participant getParticipants() {
		return participants;
	}

	/**
	 * @return the destination
	 */
	public Location getDestination() {
		return destination;
	}

	/**
	 * @return the targetRange
	 */
	public double getTargetRange() {
		return targetRange;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (participants == null) {
			return;
		}
		
		for (QuestPlayer qp : participants.getParticipants()) {
			if (qp.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {	
				update();
				return;
			}
		}
	}
	
	/**
	 * Checks whether at least one participant is in the required area
	 */
	@Override
	public void update() {
		sync();
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().isOnline())
			if (player.getPlayer().getPlayer().getLocation().getWorld().getName().equals(destination.getWorld().getName()))
			if ((player.getPlayer().getPlayer()).getLocation().distance(destination) <= targetRange) {
				if (!state) {
					state = true;
					updateQuest();
				}
				return;
			}
		}
		
		state = false;
	}

	@Override
	public void fromConfig(ConfigurationSection config)
			throws InvalidConfigurationException {
		//we need location information and range information
		//  type: "posr"
		//  range: [double]
		//  destination: [location]
		
		if (!config.contains("type") || !config.getString("type").equals("posr")) {
			throw new InvalidConfigurationException();
		}
		
		this.desc = config.getString("description", "Be in the target area");
		this.targetRange = config.getDouble("range", 1.0);
		this.destination = ((LocationState) config.get("destination")).getLocation();
	}
	
	@Override
	public String getDescription() {
		return desc;
	}
	
	@Override
	public Location getLocation() {
		return this.destination;
	}
}
