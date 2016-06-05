package com.skyisland.questmanager.player.skill.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.QualityItem;

/**
 * Called when a {@link QuestPlayer QuestPlayer} is crafting
 * an object. Specifically, called when the player is using a non-standard crafting interface.
 * @author Skyler
 *
 */
public class CraftEvent extends Event {
	
	public enum CraftingType {
		COOKING,
		SMITHING,
		FASHIONING,
		VANILLA;
	}

	private static final HandlerList handlers = new HandlerList();
		
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private QuestPlayer player;
	
	private boolean isFail;
	
	private int difficulty;
	
	private QualityItem outcome;
	
	private double quantityModifier;
	
	private double qualityModifier;
	
	private CraftingType type;
	
	public CraftEvent(QuestPlayer player, CraftingType type, int difficulty, QualityItem outcome) {
		this.player = player;
		this.isFail = false;
		this.difficulty = difficulty;
		this.outcome = outcome;
		this.type = type;
		
		this.qualityModifier = this.quantityModifier = 1.0;
	}

	public QuestPlayer getPlayer() {
		return player;
	}

	public void setPlayer(QuestPlayer player) {
		this.player = player;
	}

	public boolean isFail() {
		return isFail;
	}

	public void setFail(boolean isFail) {
		this.isFail = isFail;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public double getQualityModifier() {
		return qualityModifier;
	}

	public void setQualityModifier(double qualityModifier) {
		this.qualityModifier = qualityModifier;
	}

	public double getQuantityModifier() {
		return quantityModifier;
	}

	public void setQuantityModifier(double quantityModifier) {
		this.quantityModifier = quantityModifier;
	}

	public CraftingType getType() {
		return type;
	}

	public QualityItem getOutcome() {
		return outcome;
	}

	public void setOutcome(QualityItem outcome) {
		this.outcome = outcome;
	}
}
