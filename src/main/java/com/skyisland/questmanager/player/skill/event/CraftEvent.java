/*
 *  QuestManager: An RPG plugin for the Bukkit API.
 *  Copyright (C) 2015-2016 Github Contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.skyisland.questmanager.player.skill.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.QualityItem;

/**
 * Called when a {@link QuestPlayer QuestPlayer} is crafting
 * an object. Specifically, called when the player is using a non-standard crafting interface.
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
