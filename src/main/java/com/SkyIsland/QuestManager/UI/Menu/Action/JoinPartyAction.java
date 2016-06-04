package com.SkyIsland.QuestManager.UI.Menu.Action;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Player;

import com.SkyIsland.QuestManager.Player.Party;
import com.SkyIsland.QuestManager.Player.QuestPlayer;

/**
 * Adds a player to another player's party, creating it if it doesn't exist
 * @author Skyler
 *
 */
public class JoinPartyAction implements MenuAction {
	
	private QuestPlayer leader;
	
	private QuestPlayer other;
	
	public JoinPartyAction(QuestPlayer leader, QuestPlayer other) {
		this.leader = leader;
		this.other = other;
	}
	
	@Override
	public void onAction() {
		// TODO Auto-generated method stub
		if (leader.getParty() == null) {
			Party party = leader.createParty();
			other.joinParty(party);
		} else {
			other.joinParty(leader.getParty());
		}
		Player p = other.getPlayer().getPlayer();
		p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
		p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.G));
		p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.E));
	}

}
