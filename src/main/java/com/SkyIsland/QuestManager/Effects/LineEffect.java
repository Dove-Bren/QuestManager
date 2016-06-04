package com.SkyIsland.QuestManager.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.SkyIsland.QuestManager.QuestManagerPlugin;

public class LineEffect extends QuestEffect implements Runnable {

	private Location cur;
	
	private Location target;
	
	private Vector dir;
	
	private double speed;
	
	private static final int ticksPerSecond = 20;
	
	private int delay;
	
	private int perTick;
	
	private Effect effect;

	public LineEffect(Effect effect, double blocksPerSecond) {
		this.speed = blocksPerSecond;
		this.effect = effect;
	}
	
	@Override
	public void play(Entity player, Location location) {
		
		// TODO Auto-generated method stub
		if (player instanceof LivingEntity) {
			cur = ((LivingEntity) player).getEyeLocation();
		} else {
			cur = player.getLocation().clone().add(0, 1.5, 0);
		}
		
		dir = location.toVector().subtract(
				player.getLocation().toVector());
		dir.normalize();
		target = location;
		
		double rate = ticksPerSecond / speed;
		
		if (rate >= 1) {
			perTick = 1;
			delay = (int) Math.round(rate);
		} else {
			delay = 1;
			perTick = (int) Math.round(1 / rate);
		}
		
		Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, delay);
	}

	@Override
	public void run() {
		
		for (int i = 0; i < perTick; i++) {	
			cur.add(dir);
			spark();
			if (cur.distance(target) < 2) {
				break;
			}
		}
		if (cur.distance(target) > 2) {
			Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, delay);
		}
	}
	
	private void spark() {
		cur.getWorld().playEffect(cur, effect, 0);
	}
}
