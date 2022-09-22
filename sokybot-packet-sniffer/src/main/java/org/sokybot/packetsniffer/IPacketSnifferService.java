package org.sokybot.packetsniffer;

import org.sokybot.packet.ImmutablePacket;
import org.sokybot.packetsniffer.packettracer.PacketTracerModel;

public interface IPacketSnifferService {

	
	public void ignore(short opcode) ; 
	
	public void analyze() ; 
	
	public void display(ImmutablePacket packet) ; 
	
	public void pasueMonitoring() ; 
	public void resumeMonitoring() ; 
	
	public void saveOrUpdate(PacketTracerModel tracer) ; 
	public void clearMonitor() ; 
	
	
}
