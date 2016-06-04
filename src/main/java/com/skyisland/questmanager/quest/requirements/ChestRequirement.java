package com.skyisland.questmanager.quest.requirements;

import com.skyisland.questmanager.player.utils.CompassTrackable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.state.RequirementState;
import com.skyisland.questmanager.configuration.state.StatekeepingRequirement;
import com.skyisland.questmanager.configuration.utils.Chest;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;

/**
 * Requirement that a participant must interact (right click or left click or both) a certain block.
 * @author Skyler
 *
 */
public class ChestRequirement extends Requirement implements Listener, StatekeepingRequirement, CompassTrackable {
	
	public static class ChestRequirementFactory extends RequirementFactory<ChestRequirement> {
		
		public ChestRequirement fromConfig(Goal goal, ConfigurationSection config) {
			ChestRequirement req = new ChestRequirement(goal);
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	private Chest chest;
	
	private Inventory inv;
	
	private ChestRequirement(Goal goal) {
		super(goal);
	}
	
	public ChestRequirement(Goal goal, Chest chest) {
		this(goal, "", chest);
	}
	
	public ChestRequirement(Goal goal, String description, Chest chest) {
		super(goal, description);
		this.chest = chest;
		state = false;
	}

	@Override
	public void activate() {
		chest.getLocation().getBlock().setType(chest.getMaterial());
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}

	public void setChest(Chest chest) {
		inv = null;
		this.chest = chest;
	}

	/**
	 * Catches a player's interaction and sees if it's with our 'chest'
	 * @param e
	 */
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		if (e.getClickedBlock() == null) {
			return;
		}
		
		sync();
		
		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getPlayer().getWorld().getName())) {
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().isOnline() && qp.getPlayer().getPlayer().getUniqueId()
						.equals(e.getPlayer().getUniqueId())) {
					//one of our participants
					//actually check interaction now
					if (e.getClickedBlock().getLocation().equals(chest.getLocation().getBlock().getLocation())) {
						
						//actually give them an/the inventory
						if (inv == null) {
							inv = chest.getInventory(e.getPlayer());
						}
						e.setCancelled(true);
						e.getPlayer().openInventory(inv);
						
						if (!state) {
							state = true;
							updateQuest();
						}
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
		chest.getLocation().getBlock().setType(chest.getMaterial());
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
		 * type: chestr
		 * chest: [chest]
		 */
		
		if (!config.contains("type") || !config.getString("type").equals("chestr")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected 'chestr' but got " + config.get("type", "null"));
		}
		
		if (!config.contains("chest")) {
			throw new InvalidConfigurationException("\nChest configuration did not contain chest information!");
		}
		
		this.chest = (Chest) config.get("chest");
		this.inv = null;
		this.desc = config.getString("description", "Search the chest");
	}
	
	public void stop() {
		//clean up chest
		chest.getLocation().getBlock().setType(Material.AIR);
		if (inv != null) {
			inv.clear();
			inv = null;
		}
	}
	
	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public Location getLocation() {
		return chest.getLocation();
	}
	
}
