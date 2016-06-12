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

import com.skyisland.questmanager.magic.spell.Spell;
import com.skyisland.questmanager.player.QuestPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Thrown when a {@link QuestPlayer QuestPlayer}
 * is casting a spell.
 *
 */
public class MagicCastEvent extends Event {
	
	public enum MagicType {
		MAGERY,
		SPELLWEAVING,
		UTILITY;
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
	
	private Spell castSpell;
	
	private MagicType type;
	
	private boolean isFail;
	
	public MagicCastEvent(QuestPlayer player, MagicType type, Spell spell) {
		this.player = player;
		this.type = type;
		this.castSpell = spell;
		isFail = false;
	}

	public MagicType getType() {
		return type;
	}
	
	public QuestPlayer getPlayer() {
		return player;
	}

	public void setPlayer(QuestPlayer player) {
		this.player = player;
	}

	public Spell getCastSpell() {
		return castSpell;
	}

	public void setCastSpell(Spell castSpell) {
		this.castSpell = castSpell;
	}

	public boolean isFail() {
		return isFail;
	}

	public void setFail(boolean isFail) {
		this.isFail = isFail;
	}
}
