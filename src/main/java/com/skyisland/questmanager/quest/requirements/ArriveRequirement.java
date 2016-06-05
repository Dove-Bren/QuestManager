package com.skyisland.questmanager.quest.requirements;

import com.skyisland.questmanager.configuration.state.RequirementState;
import com.skyisland.questmanager.configuration.state.StatekeepingRequirement;
import com.skyisland.questmanager.configuration.utils.LocationState;
import com.skyisland.questmanager.player.Participant;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.utils.CompassTrackable;
import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;
import com.skyisland.questmanager.QuestManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Requirement that a participant must arrive at a location (or be within some radius of it)<br />
 * This requirement <b>does not require</b> that a participant <i>stay</i> at the location.
 * It only requires that someone get there at some point.
 * @author Skyler
 * @see {@link PositionRequirement}
 */
public class ArriveRequirement extends Requirement implements Listener, StatekeepingRequirement, CompassTrackable {
	
	public static class ArriveFactory extends RequirementFactory<ArriveRequirement> {

		@Override
		public ArriveRequirement fromConfig(Goal goal, ConfigurationSection config) {
			ArriveRequirement req = new ArriveRequirement(goal);
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
		
	}
		
	
	/**
	 * Where they need to go
	 */
	private Location destination;
	
	/**
	 * How close they can be to the destination to call it good (in blocks)
	 */
	private double targetRange;
	
	/**
	 * Super secret private constructor for factory call convenience
	 * @param goal
	 */
	private ArriveRequirement(Goal goal) {
		super(goal);	
		//Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	public ArriveRequirement(Goal goal, String description, Participant participants, Location location, double range) {
		this(goal);
		
		this.participants = participants;
		this.destination = location;
		this.targetRange = range;
		
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
	 * Checks if any of the involved participants is within range of the location.
	 */
	@Override
	public void update() {
		
		if (state) {
			return;
		}
		sync();
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().isOnline())
			if (player.getPlayer().getPlayer().getLocation().getWorld().getName().equals(destination.getWorld().getName()))
			if ((player.getPlayer().getPlayer()).getLocation().distance(destination) <= targetRange) {
				state = true;
				updateQuest();
				
				//unregister listener, cause we'll never switch to unsatisfied
				HandlerList.unregisterAll(this);
				return;
			}
		}
		
		state = false;
	}

	@Override
	public void fromConfig(ConfigurationSection config)
			throws InvalidConfigurationException {
		// same of Position Requirements's loading
		//  type: "arrr"
		//  range: [double]
		//  destination: [location]
			
		if (!config.contains("type") || !config.getString("type").equals("arrr")) {
			throw new InvalidConfigurationException();
		}
		
		this.desc = config.getString("description", "Arrive at the location");
		this.targetRange = config.getDouble("range", 1.0);
		this.destination = ((LocationState) config.get("destination")).getLocation();
		
	}

	@Override
	public RequirementState getState() {
		YamlConfiguration config = new YamlConfiguration();
		config.set("state", state);
		
		RequirementState image = new RequirementState(config);
		
		return image;
	}

	@Override
	public void loadState(RequirementState state) throws InvalidConfigurationException {
		ConfigurationSection config = state.getConfig();
		if (!config.contains("state")) {
			throw new InvalidConfigurationException();
		}
		
		this.state = config.getBoolean("state");
		
	}
	

	@Override
	public void stop() {
		; //do nothing, nothing to clean
	}

	@Override
	public String getDescription() {
		return this.desc;
	}
	
	@Override
	public Location getLocation() {
		return this.destination;
	}
}
