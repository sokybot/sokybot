package org.sokybot.packetsniffer;

import sokybot.bot.IBotContext;
import sokybot.bot.network.packetpublisher.IPacketObserver;
import sokybot.bot.network.packets.ImmutablePacket;

public class PacketSniffer implements IPacketObserver{

	private IPacketSnifferService service ; 
	
    public PacketSniffer(IBotContext botCtx , IPacketSnifferService service) {
	
    	botCtx.getBotNetwork().getClientPacketPublisher().subscribe(this, 0);
    	botCtx.getBotNetwork().getServerPacketPublisher().subscribe(this, 0);
    	this.service = service ; 
    }
	
	
	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onNext(int opcode, ImmutablePacket packet) {
		
		this.service.display( packet);
	}
}
