package org.sokybot.machine.network ; 

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.sokybot.packet.Encoding;
import org.sokybot.packet.IPacketReader;
import org.sokybot.packet.ImmutablePacket;
import org.sokybot.packet.MutablePacket;
import org.sokybot.packet.NetworkPeer;
import org.sokybot.security.IBlowfish;
import org.sokybot.security.ICRCSecurity;
import org.sokybot.security.ICountSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class HandshakeHandler extends SimpleChannelInboundHandler<ImmutablePacket> {

	@Autowired
	private IBlowfish blowfish;

	@Autowired
	private ICRCSecurity crcSecurity;

	@Autowired
	private ICountSecurity countSecurity;

	private int clientSecret;
	private int serverSecret;
	private int sharedSecret;
	private final int RND = 0x33;
	
	private final String MODULE_NAME = "SR_Client" ; 

	private byte[] finalKey = null;

	private ByteBuffer privateKey;

	private ChannelHandlerContext ctx;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		super.handlerAdded(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ImmutablePacket packet) throws Exception {

		if (packet.getOpcode() == 0x5000) {

			IPacketReader packetReader = packet.getPacketReader();

			byte flagByte = packetReader.readByte(0);
			if (flagByte == 0x0E) {

				setupSecurityProtocol(packetReader);
			} else if (flagByte == 0x10) {
				challengeServer(packetReader);
			}

		}

	}

	private void setupSecurityProtocol(IPacketReader reader) {
		// Setup Security Count and CRC

		int countSeed = reader.readInt(9);
		int crcSeed = reader.readInt(13);
		int finalKeySeed1 = reader.readInt(17);
		int finalKeySeed2 = reader.readInt(21);
		int primitiveRoot = reader.readInt(25);
		int primaryNumber = reader.readInt(29);
		this.serverSecret = reader.readInt(33);

		this.crcSecurity.configur(crcSeed);
		this.countSecurity.configur(countSeed);

		this.clientSecret = (int) generateSecrets(primitiveRoot, RND, primaryNumber);

		this.sharedSecret = (int) generateSecrets(serverSecret, RND, primaryNumber);

		// set Final Key

		ByteBuffer finalKeySeeds = ByteBuffer.allocate(8);
		finalKeySeeds.order(ByteOrder.LITTLE_ENDIAN);
		finalKeySeeds.putInt(finalKeySeed1);
		finalKeySeeds.putInt(finalKeySeed2);

		ByteBuffer sharredSecretBuffer = ByteBuffer.allocate(4);
		sharredSecretBuffer.order(ByteOrder.LITTLE_ENDIAN);
		sharredSecretBuffer.putInt(sharedSecret);

		transformValue(finalKeySeeds.array(), sharredSecretBuffer.array(), (byte) 0x3);

		this.finalKey = finalKeySeeds.array();

		// generate Private Key
		byte PK_KeyByte = (byte) (this.sharedSecret & 0x03);
		this.privateKey = ByteBuffer.allocate(8);
		this.privateKey.order(ByteOrder.LITTLE_ENDIAN);
		this.privateKey.putInt(this.serverSecret);
		this.privateKey.putInt(this.clientSecret);

		transformValue(privateKey.array(), sharredSecretBuffer.array(), PK_KeyByte);

		blowfish.configur(privateKey.array());

		// generate Private Key Data
		byte PD_KeyByte = (byte) (this.clientSecret & 0x07);

		ByteBuffer privateData = ByteBuffer.allocate(8);
		privateData.order(ByteOrder.LITTLE_ENDIAN);
		privateData.putInt(this.clientSecret);
		privateData.putInt(this.serverSecret);

		transformValue(privateData.array(), sharredSecretBuffer.array(), PD_KeyByte);

		blowfish.encode(0, privateData.array());

		this.blowfish.configur(this.finalKey);

		// reset Secrets
		// this.serverSecret = packet.getServerSecret();
		this.clientSecret = (int) generateSecrets(primitiveRoot, RND, primaryNumber);
		this.sharedSecret = (int) generateSecrets(this.serverSecret, RND, primaryNumber);

		ByteBuffer authPacket = ByteBuffer.allocate(12);
		authPacket.order(ByteOrder.LITTLE_ENDIAN);
		authPacket.putInt(clientSecret);
		authPacket.put(privateData.array());

		MutablePacket packet = MutablePacket.getBuilder(12).putBytes(authPacket.array()).build();
		packet.setPacketEncoding(Encoding.PLAIN);
		packet.setDataEncoding(Encoding.PLAIN);
		packet.setPacketSource(NetworkPeer.BOT);
		packet.setOpcode((short) 0x5000);
		packet.setPacketSize((short) 0x0c);
		this.ctx.writeAndFlush(packet);
	
		// PacketPrinter.print(this.getClass().toString() ,
		// ImmutablePacket.wrap(packet.unwrap(), packet.getPacketEncoding()),
		// PacketDirection.BOT,
		// true);

	}
	
private void challengeServer(IPacketReader reader) {

		
		System.out.println("On Challenge Server "); 
		ByteBuffer actualPrivateData = ByteBuffer.wrap(reader.readBytes(1, 8));

		ByteBuffer expectedPrivateData = ByteBuffer.allocate(8);
		expectedPrivateData.order(ByteOrder.LITTLE_ENDIAN);
		expectedPrivateData.putInt(this.serverSecret);
		expectedPrivateData.putInt(this.clientSecret);

		byte PD_KeyByte = (byte) (this.serverSecret & 0x07);

		ByteBuffer sharredSecretBuffer = ByteBuffer.allocate(4);
		sharredSecretBuffer.order(ByteOrder.LITTLE_ENDIAN);
		sharredSecretBuffer.putInt(sharedSecret);

		transformValue(expectedPrivateData.array(), sharredSecretBuffer.array(), PD_KeyByte);

		this.blowfish.configur(this.privateKey.array());
		byte[] encoded = this.blowfish.encode(0, expectedPrivateData.array());

		this.blowfish.configur(this.finalKey);
		
		expectedPrivateData = ByteBuffer.wrap(encoded);

		if (expectedPrivateData.equals(actualPrivateData)) {

			
			MutablePacket acceptancePacket = MutablePacket.wrap(new byte[6]);
			acceptancePacket.setPacketSize((short) 0x00);
			acceptancePacket.setOpcode((short) 0x9000);
			acceptancePacket.setPacketEncoding(Encoding.PLAIN);
			acceptancePacket.setDataEncoding(Encoding.PLAIN);
			acceptancePacket.setPacketSource(NetworkPeer.BOT);
			this.ctx.writeAndFlush(acceptancePacket);
			//PacketPrinter.print(this.getClass().toString() , ImmutablePacket.wrap(acceptancePacket.unwrap(), acceptancePacket.getPacketEncoding()),
				//	PacketDirection.BOT, true);
			log.debug("Final Blowfish Key : " + ByteBufUtil.hexDump(this.finalKey));
			
			int packetSize = MODULE_NAME.length() + 3;

			MutablePacket moduleIdentification = MutablePacket.getBuilder(packetSize)
					.putShort((short) MODULE_NAME.length())
					.putBytes(MODULE_NAME.getBytes()).put((byte) 0x00).build();

			moduleIdentification.setPacketSource(NetworkPeer.BOT);
			moduleIdentification.setPacketEncoding(Encoding.ENCRYPTED);
			moduleIdentification.setDataEncoding(Encoding.PLAIN);
			moduleIdentification.setPacketSize((short) packetSize);
			moduleIdentification.setOpcode((short) 0x2001);

			//PacketPrinter.print(this.getClass().toString() , 
					//ImmutablePacket.wrap(moduleIdentification.unwrap(), moduleIdentification.getDataEncoding()),
					//PacketDirection.BOT, true);

			this.ctx.writeAndFlush(moduleIdentification);
//			PacketPrinter.print(this.getClass().toString() ,
	//				ImmutablePacket.wrap(moduleIdentification.unwrap(), moduleIdentification.getDataEncoding()),
		//			PacketDirection.BOT, true);


			log.info("Authentication Accepted");
			
			// here we must remove this handler from channel pipeline 
			this.ctx.pipeline().remove(this) ; 
			
		} else {

			log.info("Connection to server  refused");
		}

	}


	private long generateSecrets(long g, int x, long p) {

		long result = 1;
		long mult = g;
		if (x == 0)
			return 0;

		while (x > 0) {

			int y = x & 1;
			if (1 == y) {

				result = (mult * result) % p;

			}
			x = x >> 1;
			mult = (mult * mult) % p;

		}

		return result;
	}

	private void transformValue(byte[] stream, byte[] Dkey, byte keyByte) {

		stream[0] ^= (byte) (stream[0] + Dkey[0] + keyByte);
		stream[1] ^= (byte) (stream[1] + Dkey[1] + keyByte);
		stream[2] ^= (byte) (stream[2] + Dkey[2] + keyByte);
		stream[3] ^= (byte) (stream[3] + Dkey[3] + keyByte);

		stream[4] ^= (byte) (stream[4] + Dkey[0] + keyByte);
		stream[5] ^= (byte) (stream[5] + Dkey[1] + keyByte);
		stream[6] ^= (byte) (stream[6] + Dkey[2] + keyByte);
		stream[7] ^= (byte) (stream[7] + Dkey[3] + keyByte);
	}

}
