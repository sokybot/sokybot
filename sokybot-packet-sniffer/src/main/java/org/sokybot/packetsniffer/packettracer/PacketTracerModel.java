package org.sokybot.packetsniffer.packettracer;

import org.dizitart.no2.objects.Id;

import sokybot.bot.network.packets.PacketDirection;

public class PacketTracerModel {

	private PacketDirection source ; 
	private String name ; 
	private boolean ignored ;
	
	@Id
	private short opcode ; 
	
	private transient int count ;
	private String description ; 
	
	
	
	public PacketDirection getSource() {
		return source;
	}
	public void setSource(PacketDirection source) {
		this.source = source;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isIgnored() {
		return ignored;
	}
	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
	public short getOpcode() {
		return opcode;
	}
	public void setOpcode(short opcode) {
		this.opcode = opcode;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "PacketTracerModel [source=" + source + ", name=" + name + ", ignored=" + ignored + ", opcode=" + opcode
				+ ", description=" + description + "]";
	} 
	
	
	
	
	
}
