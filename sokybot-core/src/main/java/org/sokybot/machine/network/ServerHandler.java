package org.sokybot.machine.network;


import org.slf4j.Logger;
import org.sokybot.machine.IMachineEvent;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.ServerFeed;
import org.sokybot.machine.UserAction;
import org.sokybot.machine.model.LoginBlockType;
import org.sokybot.machine.model.LoginErrorCode;
import org.sokybot.network.IPacketPublisher;
import org.sokybot.network.packet.IPacketReader;
import org.sokybot.network.packet.IStreamReader;
import org.sokybot.network.packet.ImmutablePacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

//https://github.com/sokybot/SilkroadDoc/tree/master/Packets/GATEWAY

@Component
@Scope("prototype")
//this class is existed temporarily
public class ServerHandler extends SimpleChannelInboundHandler<ImmutablePacket> {

	public static final String NAME = "SERVER_HANDLER";

	@Autowired
	private Logger log;

	@Autowired
	private StateMachine<MachineState, IMachineEvent> dispatcher;

	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ImmutablePacket packet) throws Exception {

		
		
		
		
		IPacketReader reader;
		IStreamReader sReader;
		IMachineEvent event;

		int opcode = packet.getOpcode();

		switch (opcode) {
		case 0x5000:
			reader = packet.getPacketReader();

			byte flagByte = reader.readByte(0);
			if (flagByte == 0x0E) {
				
				dispatchPacket(ServerFeed.SETUP, packet);
		
			} else if (flagByte == 0x10) {
				
				dispatchPacket(ServerFeed.CHALLENGE, packet);
			}
			
			break;
	
		case 0x2001:
			reader = packet.getPacketReader();
			int serviceNameLen = reader.readShort(0);
			String name = new String(reader.readBytes(2, serviceNameLen));
			if (name.equals("GatewayServer")) {

				this.dispatcher.sendEvent(ServerFeed.GATEWAY_CONNECTED);

			} else if (name.equals("AgentServer")) {
				this.dispatcher.sendEvent(ServerFeed.AGENT_CONNECTED);
			}

			break;
	
		case 0xA100:
			if (packet.getPacketReader().readByte(0) == 1) {
				this.dispatcher.sendEvent(ServerFeed.COMPATIBLE);
			} else {
				this.dispatcher.sendEvent(ServerFeed.INCOMPATIBLE);
			}

			break;
		


		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.info("Server Error {} ", cause.getMessage());
		this.dispatcher.sendEvent(UserAction.DISCONNECT);
	}
	
	private void dispatchPacket(IMachineEvent event , ImmutablePacket packet) { 
		this.dispatcher.sendEvent(MessageBuilder.withPayload(event).setHeader("packet", packet).build()) ; 
	}

}
