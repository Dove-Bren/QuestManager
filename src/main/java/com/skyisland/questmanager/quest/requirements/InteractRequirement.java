package com.skyisland.questmanager.quest.requirements;

import com.skyisland.questmanager.configuration.state.RequirementState;
import com.skyisland.questmanager.player.utils.CompassTrackable;
import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.state.StatekeepingRequirement;
import com.skyisland.questmanager.configuration.utils.LocationState;
import com.skyisland.questmanager.player.QuestPlayer;

/**
 * Requirement that a participant must interact (right click or left click or both) a certain block.
 * @author Skyler
 *
 */
public class InteractRequirement extends Requirement implements Listener, StatekeepingRequirement, CompassTrackable {
	
	public static class InteractFactory extends RequirementFactory<InteractRequirement> {
		
		public InteractRequirement fromConfig(Goal goal, ConfigurationSection config) {
			InteractRequirement req = new InteractRequirement(goal);
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	private Location location;
	
	private BlockFace face;
	
	private Action action;
	
	private InteractRequirement(Goal goal) {
		super(goal);
	}
	
	public InteractRequirement(Goal goal, Location blockLocation) {
		this(goal, blockLocation, null);
	}
	
	public InteractRequirement(Goal goal, Location blockLocation, Action action) {
		this(goal, "", blockLocation, null, action);
	}
	
	public InteractRequirement(Goal goal, Location blockLocation, BlockFace face, Action action) {
		this(goal, "", blockLocation, face, action);
	}
	
	public InteractRequirement(Goal goal, String description, Location blockLocation, BlockFace face, Action action) {
		super(goal, description);
		this.location = blockLocation;
		this.face = face;
		this.action = action;
		state = false;
		
	}

	@Override
	public void activate() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	public BlockFace getFace() {
		return face;
	}

	public void setFace(BlockFace face) {
		this.face = face;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Catches a player's interaction and sees if it's the one we've been waiting for
	 * @param e
	 */
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		if (state) {
			HandlerList.unregisterAll(this);
			return;
		}

		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getPlayer().getWorld().getName())) {
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().isOnline() && qp.getPlayer().getPlayer().getUniqueId()
						.equals(e.getPlayer().getUniqueId())) {
					//one of our participants
					//actually check interaction now
					if (face == null || face == e.getBlockFace()) 
					if (actionsMatch(e.getAction()))
					if (e.getClickedBlock() != null && e.getClickedBlock().getLocation().equals(location.getBlock().getLocation())){
						state = true;
						HandlerList.unregisterAll(this);
						updateQuest();
					}
				}
			}
		}
		
	}
	
	/**
	 * Nothing to do
	 */
	@Override
	public void update() {
		;
	}

	@Override
	public RequirementState getState() {
		YamlConfiguration myState = new YamlConfiguration();
		
		myState.set("state", state);
		
		return new RequirementState(myState);
	}

	@Override
	public void loadState(RequirementState reqState) throws InvalidConfigurationException {
		
		
		ConfigurationSection myState = reqState.getConfig();
		
		if (myState == null) {
			state = false;
			update();
			return;
		}
		
		state = myState.getBoolean("state", false);
		
		update();
	}

	@Override
	public void fromConfig(ConfigurationSection config) throws InvalidConfigurationException {
		/*
		 * type: intr
		 * location: [loc]
		 * [face]: [face enum name]
		 * [action]: {LEFT/RIGHT}
		 */
		
		if (!config.contains("type") || !config.getString("type").equals("intr")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected 'intr' but got " + config.get("type", "null"));
		}
		
		this.location = ((LocationState) config.get("location")).getLocation();
		
		if (config.contains("face")) {
			this.face = BlockFace.valueOf(config.getString("face"));
		}
		
		if (config.contains("action")) {
			this.action = getAction(config.getString("action"));
		}
		
		this.desc = config.getString("description", config.getString("action", "Right")
				+ " click the area");
	}
	
	public void stop() {
		;
	}
	
	@Override
	public String getDescription() {
		return desc;
	}
	
	@Override
	public Location getLocation() {
		return location;		
	}
	
	/**
	 * Check to see if the nature of the actions are the same.<br />
	 * Specifically, check to see if both are 'left click' or 'right click' cause we don't
	 * really care about the whole block/air thing
	 * @param otherAction
	 * @return
	 */
	private boolean actionsMatch(Action otherAction) {
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			return otherAction == Action.LEFT_CLICK_AIR || otherAction == Action.LEFT_CLICK_BLOCK;
		} else {
			return otherAction == Action.RIGHT_CLICK_AIR || otherAction == Action.RIGHT_CLICK_BLOCK;
		}
	}
	
	private Action getAction(String configActionName) {
		if (configActionName != null && configActionName.equalsIgnoreCase("LEFT")) {
			return Action.LEFT_CLICK_BLOCK;
		} else {
			return Action.RIGHT_CLICK_BLOCK;
		}
	}
}
