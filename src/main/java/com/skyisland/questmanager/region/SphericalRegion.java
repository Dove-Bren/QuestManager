package com.skyisland.questmanager.region;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class SphericalRegion extends Region {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(SphericalRegion.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(SphericalRegion.class);
	}
	

	private enum aliases {
		DEFAULT(SphericalRegion.class.getName()),
		SIMPLE("RSphere");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private World world;
	
	private Vector center;
	
	private double radius;
	
	public SphericalRegion(World world, Vector center, double radius) {
		this.world = world;
		this.center = center;
		this.radius = radius;
	}

	@Override
	public boolean isIn(Entity e) {
		return e.getLocation().getWorld().equals(world) && 
				e.getLocation().toVector().distance(center) <= radius;
	}

	@Override
	public boolean isIn(Location loc) {
		return loc.getWorld().equals(world) &&
				loc.toVector().distance(center) <= radius;
	}

	@Override
	public int hashCode() {
		return (int) (112000 
				+ (800 * world.getName().hashCode())
				+ (7 * center.getY())
				+ (17 * center.getZ())
				+ (23 * center.getX())
				+ (13 * radius));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SphericalRegion) {
			SphericalRegion r = (SphericalRegion) o;
			if (r.center.equals(center) 
					&& Math.abs(r.radius - radius) < .05) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public Location randomLocation(boolean safe) {
		Location loc = world.getSpawnLocation().clone();
		loc.setX(center.getX());
		loc.setY(center.getY());
		loc.setZ(center.getZ());
		
		Random rand = new Random();
		double rad = rand.nextDouble() * radius;
		double dir = rand.nextDouble() * (2 * Math.PI);
		
		loc.add(rad * Math.cos(dir), 0, rad * Math.sin(dir));
		
		if (!safe) {
			return loc;
		}
		
		//check y, climb up till we are out of our range
		while (loc.add(0,1,0).getBlock().getType().isSolid() ||
				loc.clone().add(0,1,0).getBlock().getType().isSolid()) {
			if (loc.toVector().distance(center) > radius) {
				return randomLocation(safe);
			}
		}
		
		//now move down to the ground
		while (!loc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
			//there's not ground below
			loc.add(0, -1, 0);
			if (center.distance(loc.toVector()) > radius) {
				break;
			}
		}
		
		loc.setX(Math.floor(loc.getX()) + .5);
		loc.setZ(Math.floor(loc.getZ()) + .5);
		loc.setY(Math.floor(loc.getY()));
		return loc;
	}
	
	public static SphericalRegion valueOf(Map<String, Object> map) {
		World world = Bukkit.getWorld((String) map.get("world"));
		
		Vector vector = (Vector) map.get("center");
		Double radius = (Double) map.get("radius");
		
		return new SphericalRegion(world, vector, radius);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("world", world.getName());
		map.put("center", center);
		map.put("radius", radius);
		
		return map;
	}
	
}
