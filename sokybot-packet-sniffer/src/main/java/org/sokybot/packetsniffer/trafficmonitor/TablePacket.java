package org.sokybot.packetsniffer.trafficmonitor;

import sokybot.bot.network.packets.ImmutablePacket;

public class TablePacket {

	private String name;
	private ImmutablePacket packet;

	private TablePacket(String name, ImmutablePacket packet) {
		this.name = name;
		this.packet = packet;
	}

	public static TablePacket createTablePacket(String name, ImmutablePacket packet) {
		return new TablePacket(name, packet);
	}

	public String getName() {
		return name;
	}

	public ImmutablePacket getPacket() {
		return packet;
	}

}
