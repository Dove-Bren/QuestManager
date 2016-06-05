package com.skyisland.questmanager.magic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.skyisland.questmanager.QuestManagerPlugin;

public final class SummonManager {
	
	private List<Summon> summons;
	
	private int summonLimit;
	
	private Map<UUID, Integer> playerCount;
	
	public SummonManager() {
		this.summons = new LinkedList<>();
		playerCount = new HashMap<>();
		summonLimit = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getSummonLimit();
	}
	
	public boolean registerSummon(Player caster, Summon summon) {
		Integer count = playerCount.get(caster.getUniqueId());
		if (count == null) {
			count = 0;
		}
		
		if (count < summonLimit) {
			registerSummon(summon);
			count++;
			playerCount.put(caster.getUniqueId(), count);
			return true;
		}

		return false;
	}
	
	public void registerSummon(Summon summon) {
		summons.add(summon);
	}
	
	public void unregisterSummon(Summon summon) {
		summons.remove(summon);
		if (playerCount.containsKey(summon.getCasterID())) {
			int count = playerCount.get(summon.getCasterID());
			count = Math.max(count - 1, 0);
			playerCount.put(summon.getCasterID(), count);
		}
	}
	
	/**
	 * Goes through all summons and removes them, also clearing this manager's list
	 */
	public void removeSummons() {
		for (Summon s : summons) {
			s.remove();
		}
		
		summons.clear();
	}
}
