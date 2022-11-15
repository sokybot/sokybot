package org.sokybot.machine.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sokybot.domain.AgentServer;
import org.sokybot.machine.IMachineEvent;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.ServerFeed;
import org.sokybot.machine.network.NetworkAttributes;
import org.sokybot.network.IPacketObserver;
import org.sokybot.network.IPacketPublisher;
import org.sokybot.network.NetworkPeer;
import org.sokybot.network.packet.Encoding;
import org.sokybot.network.packet.IStreamReader;
import org.sokybot.network.packet.ImmutablePacket;
import org.sokybot.network.packet.MutablePacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import io.netty.channel.group.ChannelGroup;
import lombok.ToString;

@Service
public class AgentServerList implements IAgentServerList, IPacketObserver {

	private final List<AgentServer> agents = new ArrayList<>();

	@Autowired
	private ChannelGroup channelGroup ;
	
	@Autowired
	private StateMachine<MachineState, IMachineEvent> machine;

	public AgentServerList(IPacketPublisher packetPublisher) {
		packetPublisher.subscribe(this, 0xA101);
	}

	@Override
	public void discoverAgents() {
		MutablePacket sharedRequest = MutablePacket.getBuilder(0, 0x6101)
				.packetEncoding(Encoding.ENCRYPTED)
				.dataEncoding(Encoding.PLAIN)
				.packetSource(NetworkPeer.BOT)
				.build();
		this.channelGroup.write(sharedRequest, (channel)->channel.attr(NetworkAttributes.TRANSPORT).get() == NetworkPeer.SERVER) ; 
		
	}
	@Override
	public Optional<AgentServer> findByName(String name) {

		return this.agents.stream().filter((agent) -> agent.getServerName().equalsIgnoreCase(name)).findFirst();

	}

	@Override
	public Optional<AgentServer> findById(int id) {

		return this.agents.stream().filter((agent) -> agent.getServerId() == id).findFirst();
	}

	@Override
	public String[] listNames() {
		return this.agents.stream().map((a) -> a.getServerName()).toArray(String[]::new);
	}

	@Override
	public int count() {

		return this.agents.size();
	}

	@Override
	public void onComplete() {
	}

	@Override
	public void onError(Throwable ex) {
	}

	@Override
	public void onNext(int opcode, ImmutablePacket packet) {

		if (opcode == 0xA101) {
			this.agents.clear();

			byte hasEntity;

			IStreamReader reader = packet.getPacketReader().asStreamReader();
			hasEntity = reader.getByte();

			if (hasEntity == 0x01) {
				reader.getByte();
				short farmSize = reader.getShort();
				String farmName = new String(reader.getBytes(farmSize));
				reader.getByte(); // spirator..
				hasEntity = reader.getByte();

				while (hasEntity == 0x01) {
					AgentServer agent = AgentServer.builder()
							.serverId(reader.getShort())
							.serverName(new String(reader.getBytes(reader.getShort())))
							.onlineUsers(reader.getShort())
							.maxUsers(reader.getShort())
							.operating(reader.getByte())
							.build();

					reader.getByte(); // fram id
					hasEntity = reader.getByte();
					this.agents.add(agent);
				}

			}
			this.machine.sendEvent(ServerFeed.AGENT_lISTED) ; 
		}

	}

}
