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

package com.skyisland.questmanager.quest.requirements;

import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.npc.NPC;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.utils.CompassTrackable;
import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;
import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.ui.ChatMenu;
import com.skyisland.questmanager.ui.menu.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Requirement that a participant must talk to an npc.
 *
 */
public class TalkRequirement extends Requirement implements Listener, CompassTrackable {
	
	public static class TalkRequirementFactory extends RequirementFactory<TalkRequirement> {
		
		public TalkRequirement fromConfig(Goal goal, ConfigurationSection config) {
			TalkRequirement req = new TalkRequirement(goal);
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	private NPC npc;
	
	private ChatMenu menu;
	
	private TalkRequirement(Goal goal) {
		super(goal);
	}
	
	public TalkRequirement(Goal goal, NPC npc, ChatMenu menu) {
		this(goal);
		this.npc = npc;
		this.state = false;
		this.menu = menu;
	}

	@Override
	public void activate() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	

	/**
	 * Catches a player's interaction and sees if it's the one we've been waiting for
	 */
	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e) {
		
		if (state) {
			HandlerList.unregisterAll(this);
			return;
		}

		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getPlayer().getWorld().getName())) {
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().isOnline() && qp.getPlayer().getPlayer().getUniqueId()
						.equals(e.getPlayer().getUniqueId())) {
					//one of our participants
					//actually check interaction now
					if (e.getRightClicked().equals(npc.getEntity())) {
						//cancel and interact
						e.setCancelled(true);
						this.state = true;
						HandlerList.unregisterAll(this);
						updateQuest();
						
						menu.show(e.getPlayer(), getGoal().getQuest());
					}
				}
			}
		}
		
	}
	
	/**
	 * Nothing to do
	 */
	@Override
	public void update() {
		;
	}

	@Override
	public void fromConfig(ConfigurationSection config) throws InvalidConfigurationException {
		/*
		 * type: talk
		 * npc: [name]
		 * message: [menu]
		 */
		
		if (!config.contains("type") || !config.getString("type").equals("talk")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected 'talk' but got " + config.get("type", "null"));
		}
		if (config.getString("npc") == null) {
			System.out.println("npc-null");
		}
		npc = QuestManagerPlugin.questManagerPlugin.getManager().getNPC(
			config.getString("npc")
				);
		
		Message message = (Message) config.get("message");
		
		message.setSourceLabel(new FancyMessage(npc.getName()));

		menu = ChatMenu.getDefaultMenu(message);
		
		this.desc = config.getString("description", config.getString("action", "Right")
				+ " click the area");
	}
	
	public void stop() {
		;
	}
	
	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public Location getLocation() {
		if (npc == null) {
			return null;
		}
		
		return npc.getEntity().getLocation();
	}
}
