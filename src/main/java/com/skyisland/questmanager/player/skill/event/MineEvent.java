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

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.QualityItem;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MineEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	private QuestPlayer player;
	
	private QualityItem result;
	
	private int difficulty;
	
	private double hardnessModifier;
	
	private double amountModifier;
	
	private double hitsModifier;
	
	private double openSlotsModifier;
	
	private double qualityModifier;
	
	private boolean isCancelled;
	
	public MineEvent(QuestPlayer player, QualityItem result, int difficulty) {
		this.player = player;
		this.result = result;
		this.difficulty = difficulty;
		this.isCancelled = false;
		
		this.hardnessModifier = this.amountModifier = this.hitsModifier = this.openSlotsModifier
				= this.qualityModifier = 1.0;
	}

	public double getHardnessModifier() {
		return hardnessModifier;
	}

	public void setHardnessModifier(double hardnessModifier) {
		this.hardnessModifier = hardnessModifier;
	}

	public double getAmountModifier() {
		return amountModifier;
	}

	public void setAmountModifier(double amountModifier) {
		this.amountModifier = amountModifier;
	}

	public double getHitsModifier() {
		return hitsModifier;
	}

	public void setHitsModifier(double hitsModifier) {
		this.hitsModifier = hitsModifier;
	}

	public double getOpenSlotsModifier() {
		return openSlotsModifier;
	}

	public void setOpenSlotsModifier(double openSlotsModifier) {
		this.openSlotsModifier = openSlotsModifier;
	}

	public double getQualityModifier() {
		return qualityModifier;
	}

	public void setQualityModifier(double qualityModifier) {
		this.qualityModifier = qualityModifier;
	}

	public QuestPlayer getPlayer() {
		return player;
	}

	/**
	 * Returns a copy of the result item. <b>Note:</b> Changes to the quality here are not supported, and will
	 * not be carried over. To affect the quality of the result, use {@link #setQualityModifier(double)}
	 */
	public QualityItem getResult() {
		return result;
	}

	public int getDifficulty() {
		return difficulty;
	}

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.isCancelled = arg0;
	}
}
