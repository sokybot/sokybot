package org.sokybot.machine.service;

import java.util.HashMap;
import java.util.Map;

import org.jline.utils.Log;
import org.slf4j.Logger;
import org.sokybot.machine.IMachineEvent;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.ServerFeed;
import org.sokybot.machine.model.ACADEMY_MEMBER_CLASS;
import org.sokybot.machine.model.Action;
import org.sokybot.machine.model.GUILD_MEMBER_CLASS;
import org.sokybot.machine.model.SelectableCharacter;
import org.sokybot.machine.network.NetworkAttributes;
import org.sokybot.network.IPacketObserver;
import org.sokybot.network.IPacketPublisher;
import org.sokybot.network.NetworkPeer;
import org.sokybot.network.packet.IStreamReader;
import org.sokybot.network.packet.ImmutablePacket;
import org.sokybot.network.packet.MutablePacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import io.netty.channel.group.ChannelGroup;

@Service
public class CharacterService implements ICharacterService, IPacketObserver {

	@Autowired
	private ChannelGroup channelGroup;

	@Autowired
	private StateMachine<MachineState, IMachineEvent> machine;

	@Autowired
	private Logger log ; 
	
	private final Map<String, SelectableCharacter> charList = new HashMap<>();

	@Autowired
	public CharacterService(IPacketPublisher packetPublisher) {
		packetPublisher.subscribe(this, 0xB001);
		packetPublisher.subscribe(this, 0xB007);
	}

	@Override
	public void joinCharacter(String charName) {

		if (this.charList.containsKey(charName)) {
			writeToServer(MutablePacket.getBuilder(charName.length() + 2, 0x7001)
					.putShort((short) charName.length())
					.putBytes(charName.getBytes())
					.build());
		}else { 
			log.info("Could not join to {} because it not exits" , charName);
		}
	}

	@Override
	public void listCharacters() {

		writeToServer(MutablePacket.getBuilder(1, 0x7007).put((byte) 0x02).build());

	}

	private void writeToServer(MutablePacket packet) {
		this.channelGroup.write(packet,
				(channel) -> channel.attr(NetworkAttributes.TRANSPORT).get() == NetworkPeer.SERVER);
	}

	@Override
	public void onComplete() {

	}

	@Override
	public void onError(Throwable ex) {

	}

	@Override
	public void onNext(int opcode, ImmutablePacket packet) {
		switch (opcode) {

		case 0xB007:

			parse(packet);
			this.machine.sendEvent(ServerFeed.LISTED) ; 
			break;
		case 0xB001:

			this.charList.clear();
			break;
		}

	}

	private void parse(ImmutablePacket packet) {

		IStreamReader reader = packet.getPacketReader().asStreamReader();

		byte action = reader.getByte();
		byte res = reader.getByte();
		if (res == 0x01) {
			if (Action.getAction(action) == Action.LIST) {

				byte charCount = reader.getByte();
				for (byte i = 0; i < charCount; i++) {

					SelectableCharacter chr = new SelectableCharacter();
					chr.setCharId(reader.getInt());
					short nameLen = reader.getShort();
					chr.setCharName(new String(reader.getBytes(nameLen)));
					chr.setCharScale(reader.getByte());
					chr.setCharLvl(reader.getByte());

					chr.setCharEXPOffset(reader.getLong());
					chr.setCharSTR(reader.getShort());
					chr.setCharINT(reader.getShort());
					chr.setCharStatPoint(reader.getShort());
					chr.setCharHP(reader.getInt());
					chr.setCharMP(reader.getInt());
					byte isDeleted = reader.getByte();

					if (isDeleted == 0x01) {
						chr.setDeleteTime(reader.getInt());
					}

					chr.setIsDeleting(isDeleted);

					chr.setGMC(GUILD_MEMBER_CLASS.getGMC(reader.getByte()));

					byte isGuildRenameReq = reader.getByte();

					if (isGuildRenameReq == 0x01) {
						short CGNL = reader.getShort(); // current Guild name len
						chr.setCurrentGuildName(new String(reader.getBytes(CGNL)));
					}

					chr.setAMC(ACADEMY_MEMBER_CLASS.getAMC(reader.getByte()));
					byte itemCount = reader.getByte();

					// Here we don`t care about character item data
					// so we will ignore it

					for (byte x = 0; x < itemCount; x++) {

						reader.skip(4); // item id
						reader.skip(1); // item plus

					}

					byte avaterItemCount = reader.getByte();

					for (byte z = 0; z < avaterItemCount; z++) {

						reader.skip(4); // item id
						reader.skip(1); // item plus

					}

					this.charList.put(chr.getCharName(), chr);
				}

			}
		}
	}

}
