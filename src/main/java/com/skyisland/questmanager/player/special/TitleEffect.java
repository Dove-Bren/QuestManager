package com.skyisland.questmanager.player.special;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;

public class TitleEffect implements Alarmable<Integer> {
	
	protected static class effectRunnable {
		
		public void run(Location loc) {
			;
		}
	}
	
	public enum TitleEffectType {
		
		SLIMEKING(new effectRunnable(){
			public void run(Location loc) {
				Location l = loc.clone();
				l.add(rand.nextDouble(), 0, rand.nextDouble());
				Slime s = (Slime) l.getWorld().spawnEntity(loc, EntityType.SLIME);
				s.setSize(1);
			}
		});
		
		private static Random rand = new Random();
		
		private effectRunnable effect;
		
		TitleEffectType(effectRunnable effect) {
			this.effect = effect;
		}
		
		public effectRunnable getEffect() {
			return effect;
		}
	}
	
	public TitleEffect() {
		Alarm.getScheduler().schedule(this, 1, 120);
	}
	
	@Override
	public void alarm(Integer key) {
		for (QuestPlayer qp : QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayers()) {
			if (qp.getPlayer().isOnline())
			if (qp.getTitle() != null)
			if (qp.getTitle().contains("Slime King")) {
				TitleEffectType.SLIMEKING.getEffect().run(qp.getPlayer().getPlayer().getLocation());
				continue;
			}
		}
		
		Alarm.getScheduler().schedule(this, 1, 120);
	}
}
