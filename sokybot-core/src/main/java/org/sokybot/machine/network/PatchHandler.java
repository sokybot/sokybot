package org.sokybot.machine.network ; 


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import static org.sokybot.app.Constants.CLIENT_MODULE_NAME;

import org.sokybot.network.packet.IPacketReader;
import org.sokybot.network.packet.ImmutablePacket; ; 

@Slf4j
@Component
@Scope("prototype")
public class PatchHandler extends SimpleChannelInboundHandler<ImmutablePacket>{

	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx, ImmutablePacket packet) throws Exception {
		
		if(packet.getOpcode() == 0x2000) { 
			IPacketReader reader = packet.getPacketReader();
			int serviceNameLen = reader.readShort(0);
			String name = new String(reader.readBytes(2, serviceNameLen));
			if (name.equals("GatewayServer")) {

				

			}

		}else if(packet.getOpcode() == 0xA100) { 
			
			
		}
		
		
	}
	private void requestPatch() {

		// 1-byte local
		// 2-byte name len
		// x-byte name bytes
		// 4-byte vers

		int packetSize = 7 + CLIENT_MODULE_NAME.length();

	//	MutablePacket patchRequestPacket = MutablePacket
	//										.getBuilder(packetSize)
	//										.put((byte) model.getLocal())
	//										.putShort((short) CLIENT_MODULE_NAME.length())
	//										.putBytes(CLIENT_MODULE_NAME.getBytes())
	//										.putInt(this.model.getClientVersion())
	//										.build();

	//	patchRequestPacket.setPacketSize((short) packetSize);
	//	patchRequestPacket.setOpcode((short) 0x6100);
	//	patchRequestPacket.setPacketEncoding(Encoding.ENCRYPTED);
	//	patchRequestPacket.setDataEncoding(Encoding.PLAIN);

	//	this.writer.write(patchRequestPacket);
	//	System.out.println("-----------------------------------------------Requesting patch");

	}

	
}
