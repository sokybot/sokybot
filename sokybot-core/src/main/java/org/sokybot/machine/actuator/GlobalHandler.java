package org.sokybot.machine.actuator;

import org.jline.utils.Log;
import org.slf4j.Logger;
import org.sokybot.app.Constants;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.StateEntry;
import org.sokybot.machine.UserConfig;
import org.sokybot.machine.UserConfig.BotType;
import org.sokybot.machine.network.NetworkAttributes;
import org.sokybot.network.NetworkPeer;
import org.sokybot.network.packet.Encoding;
import org.sokybot.network.packet.MutablePacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.annotation.WithStateMachine;

import io.netty.channel.group.ChannelGroup;


@WithStateMachine
public class GlobalHandler {


	@Autowired
	Logger log ; 
	

	@Autowired
	private ChannelGroup channelGroup ; 
	
	@Autowired
	private UserConfig config ; 
	
	
	@StateEntry(source =  MachineState.CHALENGING , target = MachineState.IDENTIFYING)
	public void identifying() { 
		
		
		if(config.getBotType() == BotType.CLIENTLESS) { 
		log.info("Identifing service");
		int packetSize = Constants.CLIENT_MODULE_NAME.length() + 3;

		MutablePacket moduleIdentification = MutablePacket
				.getBuilder(packetSize , 0x2001)
				.packetEncoding(Encoding.ENCRYPTED)
				.putShort((short) Constants.CLIENT_MODULE_NAME.length())
				.putBytes(Constants.CLIENT_MODULE_NAME.getBytes()).put((byte) 0x00).build();

		//moduleIdentification.setPacketSize((short) packetSize);
	//	moduleIdentification.setOpcode((short) 0x2001);


		writeToServer(moduleIdentification);
		}
		
	}
	
	private void writeToServer(MutablePacket packet) { 
		this.channelGroup.writeAndFlush(packet, 
				(ctx)-> ctx.attr(NetworkAttributes.TRANSPORT).get() == NetworkPeer.SERVER);
		
	}
}
