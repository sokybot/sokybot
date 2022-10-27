package org.sokybot;

import java.util.concurrent.Flow.Publisher;

import org.sokybot.network.packet.ImmutablePacket;

public interface IMachineContext {

	
	

	Publisher<ImmutablePacket> clientPacketPublisher() ; 
	Publisher<ImmutablePacket> serverPacketPublisher() ; 
}
