package org.sokybot.machine.service;

import java.util.Arrays;

import org.slf4j.Logger;
import org.sokybot.app.Constants;
import org.sokybot.machine.IMachineEvent;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.ServerFeed;
import org.sokybot.machine.UserAction;
import org.sokybot.machine.model.AuthenticationErrorCode;
import org.sokybot.machine.model.LoginBlockType;
import org.sokybot.machine.model.LoginErrorCode;
import org.sokybot.machine.network.NetworkAttributes;
import org.sokybot.network.IPacketObserver;
import org.sokybot.network.IPacketPublisher;
import org.sokybot.network.NetworkPeer;
import org.sokybot.network.packet.Encoding;
import org.sokybot.network.packet.IStreamReader;
import org.sokybot.network.packet.ImmutablePacket;
import org.sokybot.network.packet.MutablePacket;
import org.sokybot.service.IGameDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import io.netty.channel.group.ChannelGroup;

@Service
public class AuthenticationService implements IAuthenticationService, IPacketObserver {

	@Autowired
	private IGameDAO gameDAO;

	@Autowired
	private IAgentServerList agentServerList;

	@Autowired
	private ChannelGroup channelGroup;

	@Autowired
	private StateMachine<MachineState, IMachineEvent> machine;

	@Autowired
	private Logger log;

	private String userName;
	private String password;
	private int serverId;

	private int loginId;

	@Autowired
	public AuthenticationService(IPacketPublisher packetPublisher) {
		packetPublisher.subscribe(this, 0xA102);
		packetPublisher.subscribe(this, 0xA103) ; 
		packetPublisher.subscribe(this, 0x6102);

	}

	@Override
	public void authenticate() {
		this.log.info("Authenticating login...");

		int packetLen = 15 + this.userName.length() + this.password.length();

		MutablePacket authLogin = MutablePacket
				.getBuilder(packetLen , 0x6103)
				.packetEncoding(Encoding.ENCRYPTED)
				.dataEncoding(Encoding.PLAIN)
				.packetSource(NetworkPeer.BOT)
				.putInt(this.loginId)
				.putShort((short) this.userName.length())
				.putBytes(this.userName.toLowerCase().getBytes())
				.putShort((short) this.password.length())
				.putBytes(this.password.getBytes())
				.put(this.gameDAO.getLocal())
				.putBytes(new byte[6]) // we can ignore this																								// statement but for																				// readability
				.build();

		
		writeToServer(authLogin);
	

	}

	@Override
	public void login(String userName, String password, String agentName) {

		
		this.agentServerList.findByName(agentName).ifPresentOrElse((agentServer) -> {

			// int agentId = agentServer.getServerId();

			int packetLen = 7 + userName.length() + password.length();

			MutablePacket loginPacket = MutablePacket.getBuilder(packetLen, 0x6102)
					.packetEncoding(Encoding.ENCRYPTED)
					.dataEncoding(Encoding.PLAIN)
					.packetSource(NetworkPeer.BOT)
					.put(this.gameDAO.getLocal())
					.putShort((short) userName.length())
					.putBytes(userName.getBytes())
					.putShort((short) password.length())
					.putBytes(password.getBytes())
					.putShort((short) agentServer.getServerId())
					.build();

			this.userName = userName;
			this.password = password;

			// loginPacket.setPacketSize((short) packetLen);
			// loginPacket.setPacketEncoding(Encoding.ENCRYPTED);
			// loginPacket.setDataEncoding(Encoding.PLAIN);
			// loginPacket.setPacketSource(NetworkPeer.BOT);

			writeToServer(loginPacket);

		}, () -> {

			log.info("Could not perform auto login ");
		});

	}

	private void writeToServer(MutablePacket packet) {

		this.channelGroup.writeAndFlush(packet,
				(ctx) -> ctx.attr(NetworkAttributes.TRANSPORT).get() == NetworkPeer.SERVER);

	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Throwable ex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNext(int opcode, ImmutablePacket packet) {

		IStreamReader reader ; 
		switch (opcode) {
		case 0xA102:
			 reader = packet.getStreamReader();

			byte loginResult = reader.getByte();

			if (loginResult == 0x01) {

				log.info("Login Success....");

				this.loginId = reader.getInt();
				String agentHost = new String(reader.getBytes(reader.getShort()));
				short agentPort = reader.getShort();

				// parsing this packet and send login_SUCCESS event
				IMachineEvent event = ServerFeed.LOGIN_SUCCESS;
				this.machine.sendEvent(MessageBuilder.withPayload(event)
						.setHeader(Constants.MACHINE_AGENT_HOST, agentHost)
						.setHeader(Constants.MACHINE_AGENT_PORT, agentPort)
						.setHeader(Constants.MACHINE_LOGIN_ID, this.loginId)
						.build());

			} else if (loginResult == 0x02) {

				LoginErrorCode error = LoginErrorCode.getError(reader.getByte());
				if (error == LoginErrorCode.Blocked) {
					LoginBlockType blockType = LoginBlockType.getBlockType(reader.getByte());
					if (blockType == LoginBlockType.Punishment) {

						IMachineEvent event = blockType;

						this.machine.sendEvent(MessageBuilder.withPayload(event).setHeader("packet", packet).build());

					} else {
						this.machine.sendEvent(blockType);
					}
				} else {
					this.machine.sendEvent(error);
				}

			} else {
				log.info("Unknown login result {} ", Integer.toHexString(loginResult));
				this.machine.sendEvent(UserAction.DISCONNECT);
			}

			break;
		case 0xA103 : 
			 reader = packet.getStreamReader() ; 
			
			byte result = reader.getByte() ; 
			if(result == 0x02) { 
				log.info("Authentication faild");
				this.machine.sendEvent(AuthenticationErrorCode.getError(reader.getByte())) ; 
			}else { 
				log.info("Login Authenticated successuffly"); 
				this.machine.sendEvent(ServerFeed.AUTHENTICATED) ; 
			}
			break ; 
		case 0x6102: // manual login

			 reader = packet.getPacketReader().asStreamReader();

			reader.skip(1); // local
			this.userName = new String(reader.getBytes(reader.getShort()));
			this.password = new String(reader.getBytes(reader.getShort()));
			this.serverId = reader.getShort();

			break;

		}
	}
}
