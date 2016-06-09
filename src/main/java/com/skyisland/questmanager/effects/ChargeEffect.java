package com.skyisland.questmanager.effects;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Charging-like effect.
 * A circle of particles appears around the target, then moves up quickly
 * @author Skyler
 *
 */
public class ChargeEffect extends QuestEffect implements Runnable {
	
	private Effect effect;
	
	private Entity player;
	
	private int count;
	
	public ChargeEffect(Effect effect) {
		this.effect = effect;
	}
	
	@Override
	public void play(Entity player, Location location) {
		this.player = player;
		count = 0;
		spark();
		Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, 
				4);
	}

	@Override
	public void run() {
		count++;
		spark();
		if (count < 4) {
			Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, 
					4);			
		}
	}
	
	private void spark() {
		Location loc = player.getLocation().clone(),
				tmp;
		loc.add(0, .4 * count, 0);
		for (int i = 0; i < 6; i++) {
			tmp = loc.clone();
			tmp.add(Math.cos(i *Math.PI / 3), 0, Math.sin(i * Math.PI / 3));
			player.getWorld().playEffect(tmp, effect, 0);
		}
	}
}
