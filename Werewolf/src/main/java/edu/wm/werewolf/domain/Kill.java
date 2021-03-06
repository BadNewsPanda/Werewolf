package edu.wm.werewolf.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "kills")
public class Kill {
	
	private String killerID;
	private String victimID;
	private Date timestamp;
	private double lat;
	private double lon;
	public Kill(String killerID, String victimID, Date timestamp, double d,
			double e) {
		super();
		this.killerID = killerID;
		this.victimID = victimID;
		this.timestamp = timestamp;
		this.lat = d;
		this.lon = e;
	}
	public String getKillerID() {
		return killerID;
	}
	public void setKillerID(String killerID) {
		this.killerID = killerID;
	}
	public String getVictimID() {
		return victimID;
	}
	public void setVictimID(String victimID) {
		this.victimID = victimID;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
}
