package org.sokybot.machine.actuator;

import static org.sokybot.app.Constants.CLIENT_MODULE_NAME;

import org.slf4j.Logger;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.StateEntry;
import org.sokybot.machine.UserConfig;
import org.sokybot.machine.UserConfig.BotType;
import org.sokybot.machine.network.NetworkAttributes;
import org.sokybot.machine.service.IAgentServerList;
import org.sokybot.machine.service.IAuthenticationService;
import org.sokybot.network.NetworkPeer;
import org.sokybot.network.packet.Encoding;
import org.sokybot.network.packet.MutablePacket;
import org.sokybot.service.IGameDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.annotation.WithStateMachine;

import io.netty.channel.group.ChannelGroup;

@WithStateMachine
public class GatewayHandler {

	@Autowired
	private ChannelGroup channelGroup;

	@Autowired
	private IGameDAO gameDAO;

	@Autowired
	private UserConfig config;


	@Autowired
	private Logger log;

	@Autowired
	private ApplicationContext ctx;

	@StateEntry(source = MachineState.IDENTIFYING, target = MachineState.VERIFYING)
	public void verifying() {
		if (this.config.getBotType() == BotType.CLIENTLESS) {
			int packetSize = 7 + CLIENT_MODULE_NAME.length();

			MutablePacket patchRequestPacket = MutablePacket.getBuilder(packetSize, 0x6100)
					.packetEncoding(Encoding.ENCRYPTED)
					.dataEncoding(Encoding.PLAIN)
					.put(this.gameDAO.getDivisionInfo().local)
					.putShort((short) CLIENT_MODULE_NAME.length())
					.putBytes(CLIENT_MODULE_NAME.getBytes())
					.putInt(this.gameDAO.getVersion())
					.build();

			// patchRequestPacket.setPacketSize((short) packetSize);
			writeToServer(patchRequestPacket);

			log.info("Patch requested");

		}
	}

	@StateEntry(source = MachineState.VERIFYING, target = MachineState.DISCOVERING)
	public void discoveringAgents() {
		if (this.config.getBotType() == BotType.CLIENTLESS) {

			log.info("Discovering agent servers ");
			this.ctx.getBean(IAgentServerList.class).discoverAgents();
			
		}
	}

	@StateEntry(source = MachineState.DISCOVERING, target = MachineState.LOGINING)
	public void logining() {
		if (config.isAutoLogin()) {
			log.info("Perform logining") ; 
			this.ctx.getBean(IAuthenticationService.class)
					.login(config.getUsername(), config.getPassword(), config.getTargetAgent());

		}
		
	}

	private void writeToServer(MutablePacket packet) {

		this.channelGroup.writeAndFlush(packet,
				(ctx) -> ctx.attr(NetworkAttributes.TRANSPORT).get() == NetworkPeer.SERVER);

	}
}
