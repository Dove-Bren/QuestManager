package com.skyisland.questmanager.configuration.utils;

/**
 * Group unique ID's! For ID'ing partys
 * @author Skyler
 *
 */
public class GUID {
	
	private static long nextID = 1l;
	
	private long ID;
	
	public static GUID generateGUID() {
		GUID n = new GUID(nextID);
		nextID++;
		return n;
	}
	
	private GUID(long ID) {
		this.ID = ID;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GUID)) {
			return false;
		}
		
		GUID other = (GUID) o;
		return (other.ID == ID);
	}
	
	@Override
	public String toString() {
		return "GI_" + this.ID;
	}
	
	public static GUID valueOf(String string) {
		if (!string.startsWith("GI_")) {
			return null;
		}
		
		return new GUID(Long.parseLong(string.substring(3)));
	}
}
