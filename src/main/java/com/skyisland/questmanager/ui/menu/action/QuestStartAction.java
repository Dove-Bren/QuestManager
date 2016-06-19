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

package com.skyisland.questmanager.ui.menu.action;

import com.skyisland.questmanager.configuration.QuestConfiguration;
import com.skyisland.questmanager.configuration.SessionConflictException;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.Participant;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.quest.history.HistoryEvent;
import com.skyisland.questmanager.quest.Quest;
import com.skyisland.questmanager.QuestManagerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import io.puharesource.mc.titlemanager.api.TitleObject;

public class QuestStartAction implements MenuAction {

	private QuestConfiguration template;
	
	private FancyMessage startingMessage;
	
	private FancyMessage acceptMessage;
	
	private Player player;
	
	private static final Sound QUEST_ACCEPT_SOUND = Sound.ENTITY_PLAYER_LEVELUP;
	
	private static final String PARTY_DENIAL = ChatColor.YELLOW + "This quest requires a party..." + ChatColor.RESET;
	
	private static final String SESSION_DENIAL = ChatColor.YELLOW + "A session of this quest is already going! Please wait until it's finished." + ChatColor.RESET;
	
	public QuestStartAction(QuestConfiguration questTemplate, FancyMessage start, FancyMessage accept, Player player) {
		this.template = questTemplate;
		this.player = player;
		this.startingMessage = start;
		this.acceptMessage = accept;
	}
	
	@Override
	public void onAction() {
		
		//Instantiate the template
		Quest quest;
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		//check to make sure this doesn't require a party
		if (template.getRequireParty())
			if (qp.getParty() == null) {
				//TODO make prettier
				player.sendMessage(QuestStartAction.PARTY_DENIAL);
				return;
		}
		
        Participant participant;
        
		if (template.getUseParty() && qp.getParty() != null) {
        	participant = qp.getParty();
        } else {
			participant = qp;
        }
		
		try {
			quest = template.instanceQuest(participant);
		} catch (InvalidConfigurationException e) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning(
					"Could not instance quest for player " + player.getName());
			player.sendMessage("An error occured. Please notify your administrator with what you " +
					"did to get this message, and the following message:\n Invalid Quest Template!");
			return;
		} catch (SessionConflictException e) {
			player.sendMessage(SESSION_DENIAL);
			return;
		}

		quest.addHistoryEvent(new HistoryEvent(startingMessage.toOldMessageFormat()
				.replaceAll(ChatColor.WHITE + "", ChatColor.BLACK + "")));
		quest.addHistoryEvent(new HistoryEvent(acceptMessage.toOldMessageFormat()
				.replaceAll(ChatColor.WHITE + "", ChatColor.BLACK + "")));
		

        
		QuestManagerPlugin.questManagerPlugin.getManager().registerQuest(quest);
		
		for (QuestPlayer qpe : participant.getParticipants()) {
			qpe.updateQuestBook(false);
			if (qpe.getPlayer().isOnline()) {
				
				(new TitleObject(ChatColor.DARK_RED + template.getName(),
						ChatColor.GOLD + template.getDescription()))
				.setFadeIn(30).setFadeOut(30).setStay(80).send(qpe.getPlayer().getPlayer());
				
				qpe.getPlayer().getPlayer().playSound(qpe.getPlayer().getPlayer().getLocation(),
					QUEST_ACCEPT_SOUND, 2, 1);
			}
		}
	}
}
