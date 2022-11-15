package org.sokybot.domain.map;

public class NavObject {

	private int id ; 
	private Position position ; 
	private short collisionFlag ;  //0x00 = No, 0xFFFF = Yes
	private float yaw ; 
	private short uniqueId ; 
	private short scale ; 
	private short eventZoneFlag ; 
	private short regionID ;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public short getCollisionFlag() {
		return collisionFlag;
	}
	public void setCollisionFlag(short collisionFlag) {
		this.collisionFlag = collisionFlag;
	}
	public float getYaw() {
		return yaw;
	}
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	public short getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(short uniqueId) {
		this.uniqueId = uniqueId;
	}
	public short getScale() {
		return scale;
	}
	public void setScale(short scale) {
		this.scale = scale;
	}
	public short getEventZoneFlag() {
		return eventZoneFlag;
	}
	public void setEventZoneFlag(short eventZoneFlag) {
		this.eventZoneFlag = eventZoneFlag;
	}
	public short getRegionID() {
		return regionID;
	}
	public void setRegionID(short regionID) {
		this.regionID = regionID;
	}
	@Override
	public String toString() {
		return "NavObject [id=" + id + ", position=" + position + ", collisionFlag=" + collisionFlag + ", yaw=" + yaw
				+ ", uniqueId=" + uniqueId + ", scale=" + scale + ", eventZoneFlag=" + eventZoneFlag + ", regionID="
				+ regionID + "]";
	} 
	
	
	
	
}
