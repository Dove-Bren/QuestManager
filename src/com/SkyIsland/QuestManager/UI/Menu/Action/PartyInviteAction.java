package com.SkyIsland.QuestManager.UI.Menu.Action;

import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Player;

import com.SkyIsland.QuestManager.Fanciful.FancyMessage;
import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.UI.ChatMenu;
import com.SkyIsland.QuestManager.UI.Menu.ChatMenuOption;
import com.SkyIsland.QuestManager.UI.Menu.MultioptionChatMenu;
import com.SkyIsland.QuestManager.UI.Menu.SimpleChatMenu;
import com.SkyIsland.QuestManager.UI.Menu.Message.PlainMessage;

public class PartyInviteAction implements MenuAction {
	
	private static final String denyMessage = 
			"That player is already in a party!";
	
	private static final String sameMessage = 
			"You cannot invite yourself to a party!";
	
	private QuestPlayer leader;
	
	private QuestPlayer other;
	
	public PartyInviteAction(QuestPlayer leader, QuestPlayer other) {
		this.leader = leader;
		this.other = other;
	}
	
	@Override
	public void onAction() {
		
		if (!other.getPlayer().isOnline() || !leader.getPlayer().isOnline()) {
			return;
		}
		
		if (other.getParty() != null) {
			leader.getPlayer().getPlayer().sendMessage(PartyInviteAction.denyMessage);
			return;
		}
		
		if (leader.getPlayer().getUniqueId().equals(other.getPlayer().getUniqueId())) {
			leader.getPlayer().getPlayer().sendMessage(PartyInviteAction.sameMessage);
			return;
		}
		
		MenuAction join = new JoinPartyAction(leader, other);
		ChatMenuOption joinOpt = new ChatMenuOption(new PlainMessage("Accept"), join);
		MenuAction deny = new ShowChatMenuAction(new SimpleChatMenu(
				new FancyMessage(other.getPlayer().getName())
					.color(ChatColor.DARK_BLUE)
					.then(" refused your invitation.")), leader.getPlayer().getPlayer());
		ChatMenuOption denyOpt = new ChatMenuOption(new PlainMessage("Deny"), deny);
		PlainMessage body = new PlainMessage(
				new FancyMessage(leader.getPlayer().getName())
					.color(ChatColor.DARK_BLUE)
					.then(" invited you to join their party!")
				);
		
		ChatMenu menu = new MultioptionChatMenu(body, joinOpt, denyOpt);
		
		Player op = other.getPlayer().getPlayer();
		menu.show(op);
		op.playNote(op.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
		op.playNote(op.getLocation(), Instrument.PIANO, Note.natural(1, Tone.G));
		op.playNote(op.getLocation(), Instrument.PIANO, Note.natural(1, Tone.E));
		
		ChatMenu myMenu = new SimpleChatMenu(new FancyMessage("Your invitation has been sent."));
		myMenu.show(leader.getPlayer().getPlayer());
		
		
	}

}
