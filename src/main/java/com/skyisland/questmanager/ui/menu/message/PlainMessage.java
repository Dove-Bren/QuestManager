package com.skyisland.questmanager.ui.menu.message;

import java.util.HashMap;
import java.util.Map;

import com.skyisland.questmanager.fanciful.FancyMessage;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Wraps arounds a simple, single -use- message.<br />
 * @author Skyler
 *
 */
public class PlainMessage extends Message {

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(PlainMessage.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(PlainMessage.class);
	}
	

	private enum aliases {
		DEFAULT(PlainMessage.class.getName()),
		SIMPLE("PlainMessage");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	
	private FancyMessage message;
	
	private PlainMessage() {
		super();
	}
	
	
	public PlainMessage(FancyMessage msg) {
		this.message = msg;
	}
	
	public PlainMessage(String msg) {
		this.message = new FancyMessage(msg);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		
		map.put("text", message);
		
		return map;
	}
	
	public static PlainMessage valueOf(Map<String, Object> map) {
		Object obj = map.get("text");
		
		PlainMessage msg = new PlainMessage();
		
		if (obj instanceof FancyMessage) {
			msg.message = (FancyMessage) obj;
			return msg;
		}
		
		//else just assume it's a string!?
		msg.message = new FancyMessage((String) obj);
		return msg;
	}

	@Override
	public FancyMessage getFormattedMessage() {
		return message;
	}
	
	
	
}
