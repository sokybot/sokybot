package org.sokybot.packetsniffer.packettracer;

import sokybot.ui.bot.packetsniffer.IPacketSnifferService;

public class PacketTracerHandler  implements IPacketTracerHandler{

	private IPacketSnifferService service ; 
	
	public PacketTracerHandler(IPacketSnifferService service) {
	
		this.service = service ; 
	}
	
	@Override
	public void onTracerChanged(PacketTracerModel model) {
		
		this.service.saveOrUpdate(model);
	}
	
	
}
