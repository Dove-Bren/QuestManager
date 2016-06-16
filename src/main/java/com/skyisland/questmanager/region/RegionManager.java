package com.skyisland.questmanager.region;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.enemy.Enemy;
import com.skyisland.questmanager.enemy.EnemyAlarms;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;
import com.skyisland.questmanager.util.WeightedList;

public final class RegionManager implements Alarmable<EnemyAlarms> {
	
	public static final double DEFAULT_DURATION = 30.0;
	
	/**
	 * Holds the enemy list and the music to play for the region
	 * @author Skyler
	 *
	 */
	private static class RegionRecord {
		
		private Sound music;
		
		private WeightedList<Enemy> enemies;
		
		public RegionRecord(Sound music, WeightedList<Enemy> enemies) {
			this.music = music;
			this.enemies = enemies;
		}
		
		public Sound getMusic() {
			return music;
			
		}
		
		public WeightedList<Enemy> getEnemies() {
			return enemies;
		}
	}
	
	private Map<Region, RegionRecord> regionMap;
	
	private double spawnrate;
	
	private Map<Sound, Double> musicDurations;
	
	private Map<UUID, Sound> currentSound;
	
	private Map<UUID, Double> secondsLeft;
	
	/**
	 * Creates an empty enemy manager with a default spawnrate of 3 seconds
	 */
	public RegionManager(Map<Sound, Double> soundDurations) {
		this(soundDurations, 3.0);
	}
	
	/**
	 * Creates an enemy manager with the provided spawn rate
	 */
	public RegionManager(Map<Sound, Double> soundDurations, double spawnrate) {
		regionMap = new HashMap<>();
		musicDurations = soundDurations;
		currentSound = new HashMap<>();
		secondsLeft = new HashMap<>();
		this.spawnrate = spawnrate;
		
		Alarm.getScheduler().schedule(this, EnemyAlarms.SPAWN, spawnrate);
	}
	
	/**
	 * Creates a new Enemy Manager using the provided file or files in the provided directory.
	 * Spawnrate defaults to 3.0
	 * @param target The file to load or the directory to search for files to load
	 */
	public RegionManager(File target, Map<Sound, Double> soundDurations) {
		this(target, soundDurations, 3.0);
	}
	
	/**
	 * Creates a new Enemy Manager using the provided file or files in the provided directory.
	 * @param target The file to load or the directory to search for files to load
	 */
	public RegionManager(File target, Map<Sound, Double> soundDurations, double spawnrate) {
		this(soundDurations, spawnrate);
		load(target);
	}
	
	/**
	 * Registers the region with the manager.
	 * Regions must be registered before they can start being associated with enemy types.
	 * @return false if the region is null or already in the map, true otherwise
	 */
	public boolean registerRegion(Region region) {
		if (region == null || regionMap.containsKey(region)) {
			return false;
		}
		
		regionMap.put(region, new RegionRecord(null, new WeightedList<>()));
		
		return true;
	}
	
	/**
	 * Adds the enemy to the list of enemies for a region.
	 * The underlying list does not make any checks against duplicates. Duplicate adds/inserts
	 * will result in duplicate entries.
	 * @return false if the region is not in the map, true otherwise
	 */
	public boolean addEnemy(Region key, Enemy enemy, double weight) {
		if (!regionMap.containsKey(key)) {
			return false;
		}
		
		WeightedList<Enemy> list = (regionMap.get(key)).enemies;
		list.add(enemy, weight);
		
		return true;
	}
	
	/**
	 * Adds the enemy to the region with a default weight 1
	 * @see #addEnemy(Region, Enemy, double)
	 */
	public boolean addEnemy(Region key, Enemy enemy) {
		return addEnemy(key, enemy, 1.0);
	}
	
	public void clear() {
		if (regionMap.isEmpty()) {
			return;
		}
		
		for (RegionRecord r : regionMap.values()) {
			r.getEnemies().clear();
		}
		
		regionMap.clear();
	}

	public double getSpawnrate() {
		return spawnrate;
	}

	public void setSpawnrate(double spawnrate) {
		this.spawnrate = spawnrate;
	}

	@Override
	public void alarm(EnemyAlarms reference) {
		switch (reference) {
		case SPAWN:
			adjustTimers();
			spawnEnemies();
			Alarm.getScheduler().schedule(this, EnemyAlarms.SPAWN, spawnrate);
			break;
		}
	}
	
	/**
	 * Cycle through players, reducing the time they have left by <i>spawnrate</i>
	 * @return
	 */
	private void adjustTimers() {
		double cache;
		for (UUID id : secondsLeft.keySet()) {
			cache = secondsLeft.get(id);
			if (cache - spawnrate <= 0)
				secondsLeft.remove(id);
			else
				secondsLeft.put(id, cache - spawnrate);
		}
	}
	
	/**
	 * Goes through all players in a quest world and spawns enemies if they are in a region.
	 */
	private void spawnEnemies() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds().contains(
					player.getWorld().getName())) {
				//is in a quest world
				for (Region r : regionMap.keySet()) {
					if (r.isIn(player)) {
						spawnInRegion(r);
						
						Sound music = regionMap.get(r).getMusic();
						if (music != null)
						if (!currentSound.containsKey(player.getUniqueId()) || currentSound.get(player.getUniqueId()) != music
							|| !secondsLeft.containsKey(player.getUniqueId())) {
							//player.playEffect(player.getLocation(), Effect.RECORD_PLAY,
							//		regionMap.get(r).getSound());
							
							//UPDATE //TODO
							//player.stopSound();
							
							player.playSound(player.getLocation(), music, 1000f, 1f);
							if (musicDurations.containsKey(music))
								secondsLeft.put(player.getUniqueId(), musicDurations.get(music));
							else
								secondsLeft.put(player.getUniqueId(), DEFAULT_DURATION);
							
							currentSound.put(player.getUniqueId(), music);
						}
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Spawns an enemy in the provided region from that region's list of enemies
	 */
	private void spawnInRegion(Region region) {
		if (regionMap.get(region).enemies == null || regionMap.get(region).enemies.isEmpty()) 
			return;
		Enemy e;
		WeightedList<Enemy> l = (regionMap.get(region)).enemies;
		
		e = l.getRandom();
		
		e.spawn(region.randomLocation(true));
	}
	
	/**
	 * Loads regions and enemies from the provided config file.
	 * Does not clear the current map before adding what's found in the config
	 */
	private void load(File target) {
		/*
		 * Is a file? If so, load it. If not, get all files and load them
		 */
		if (target == null || !target.exists()) {
			return;
		}
		
		if (!target.isDirectory()) {
			loadFile(target);
		} else {
			for (File file : target.listFiles()) {
				if (file.isDirectory()) {
					load(file);
				}
				
				String ln = file.getName().toLowerCase();
				
				if (ln.endsWith(".yml") || ln.endsWith(".yaml")) {
					loadFile(file);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadFile(File file) {
		/*
		 * Load the config. There should be regions and enemies associated with them, like
		 * region1:
		 *  type:	==: Cuboid
		 * 			etc
		 *  enemies:
		 *    - ==: enemy
		 */
		
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		for (String key : config.getKeys(false)) {
			ConfigurationSection regionSection = config.getConfigurationSection(key);
			Region region = (Region) regionSection.get("region");
			
			List<Enemy> enemies = null;

			if (regionSection.contains("enemies")) {
				//load enemies
				enemies = (List<Enemy>) regionSection.getList("enemies");
			}
			
			registerRegion(region);
			if (enemies != null && !enemies.isEmpty())
			for (Enemy e : enemies) {
				addEnemy(region, e);
			}
			
			if (regionSection.contains("music")) {
				try {
				regionMap.get(region).music = Sound.valueOf(regionSection.getString("music"));
				} catch (Exception e) {
					QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to match sound " + regionSection.getString("music"));
				}
				
			}
			//TODO add enemy weights?
		}
	}
	
	/**
	 * Attempts to find a registered region that the given location falls in
	 * @return The region the location lies in, or null if none were found
	 */
	public Region getRegion(Location location) {
		if (regionMap.isEmpty())
			return null;
		
		if (location == null) {
			return null;
		}
		
		for (Region region : regionMap.keySet()) {
			if (region.isIn(location)) {
				return region;
			}
		}
		
		return null;
	}
}
