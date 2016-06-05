package com.skyisland.questmanager.npc;

import java.util.HashMap;
import java.util.Map;

import com.skyisland.questmanager.configuration.EquipmentConfiguration;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.LocationState;

/**
 * Basic NPC with no interactivity and no movement.
 * @author Skyler
 * @config <i>name</i>: "name"<br />
 * <i>type</i>: "EntityType.VALUE.toString"<br />
 * <i>location</i>: <br />
 * &nbsp;&nbsp;==: org.bukkit.Location or com.SkyIsland.QuestManager.Configuration.Utils.LocationState<br />
 * <i>equipment</i>:<br />
 * &nbsp;&nbsp;[valid {@link EquipmentConfiguration}]
 */
public class DummyNPC extends SimpleNPC {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(DummyNPC.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(DummyNPC.class);
	}
	

	private enum aliases {
		FULL("com.SkyIsland.QuestManager.NPC.DummyNPC"),
		DEFAULT(DummyNPC.class.getName()),
		SHORT("DummyNPC"),
		INFORMAL("DMMYNPC");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}

	private DummyNPC(Location startingLoc) {
		super(startingLoc);
	}

	public static DummyNPC valueOf(Map<String, Object> map) {
		if (map == null || !map.containsKey("name") || !map.containsKey("type") 
				 || !map.containsKey("location") || !map.containsKey("equipment")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Invalid NPC info! "
					+ (map.containsKey("name") ? ": " + map.get("name") : ""));
			return null;
		}
		
		EquipmentConfiguration econ = new EquipmentConfiguration();
		try {
			YamlConfiguration tmp = new YamlConfiguration();
			tmp.createSection("key",  (Map<?, ?>) map.get("equipment"));
			econ.load(tmp.getConfigurationSection("key"));
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LocationState ls = (LocationState) map.get("location");
		Location loc = ls.getLocation();
		
		EntityType type = EntityType.valueOf((String) map.get("type"));
		
		
		DummyNPC npc = new DummyNPC(loc);

		loc.getChunk();
		npc.setEntity(loc.getWorld().spawnEntity(loc, type));
		npc.setStartingLoc(loc);
		npc.getEntity().setCustomName((String) map.get("name"));
		
		npc.name = (String) map.get("name");

		if (npc.getEntity() instanceof LivingEntity) {
			EntityEquipment equipment = ((LivingEntity) npc.getEntity()).getEquipment();
			equipment.setHelmet(econ.getHead());
			equipment.setChestplate(econ.getChest());
			equipment.setLeggings(econ.getLegs());
			equipment.setBoots(econ.getBoots());
			equipment.setItemInMainHand(econ.getHeldMain());
			equipment.setItemInOffHand(econ.getHeldOff());
			
		}
		
		return npc;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>(4);
		
		map.put("name", name);
		map.put("type", getEntity().getType());
		map.put("location", new LocationState(getEntity().getLocation()));
		
		EquipmentConfiguration econ;
		
		if (getEntity() instanceof LivingEntity) {
			econ = new EquipmentConfiguration(
					((LivingEntity) getEntity()).getEquipment()
					);
		} else {
			econ = new EquipmentConfiguration();
		}
		
		map.put("equipment", econ);
	
		
		return map;
	}

	@Override
	protected void interact(Player player) {
		//we don't do anything on interact
		;		
	}
	
	@Override
	/**
	 * Render this NPC immobile using slowness instead of teleporting them
	 */
	public void tick() {
		Entity e = getEntity();
		
		if (e == null) {
			return;
		}
		

		if (!e.getLocation().getChunk().isLoaded()) {
			return;
		}
		
		if (e instanceof LivingEntity) {
			((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999999, 10, false, false), true);
		}
	}
}
