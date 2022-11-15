package org.sokybot.machine.network;

import java.util.Set;

import org.slf4j.Logger;
import org.sokybot.network.NetworkPeer;
import org.sokybot.network.packet.ImmutablePacket;
import org.sokybot.network.packet.MutablePacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Sharable
@Component
@Lazy
public class ClientServerBridge extends SimpleChannelInboundHandler<ImmutablePacket> {

	private ChannelHandlerContext clientCtx;
	private ChannelHandlerContext serverCtx;

	private Set<Integer> preventedOpcodes;

	@Autowired
	Logger log;

	public ClientServerBridge() {

		this.preventedOpcodes = Set.of(
				// 0x5000
				// , 0x9000
				// ,0x2001
				// 0x6100
				// ,0x2002
				0xA102
				// , 0x6101
				, 0x6103  // authentication
				 , 0x7001 // Joining characters , this request with always occurs by machine 
		// ,0x7007
		);

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		NetworkPeer peer = ctx.channel().attr(NetworkAttributes.TRANSPORT).get();
		if (peer == NetworkPeer.CLIENT) {
			this.clientCtx = ctx;
			log.info("Client channel is now active");
		} else if (peer == NetworkPeer.SERVER) {
			this.serverCtx = ctx;
			log.info("Server channel is now active");
		}
		super.channelActive(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ImmutablePacket msg) throws Exception {
		NetworkPeer peer = ctx.channel().attr(NetworkAttributes.TRANSPORT).get();

		if (msg.getOpcode() != 0x2002)
			log.info("{} Send [{}] {} ", (peer == NetworkPeer.CLIENT) ? "Client" : "Server",
					(this.preventedOpcodes.contains(msg.getOpcode())) ? "Blocked" : "Allowed", msg);

		if (!this.preventedOpcodes.contains(msg.getOpcode())) {
			MutablePacket packet = MutablePacket.wrap(msg.toBytes());
			packet.setPacketSource(peer);
			if (peer == NetworkPeer.CLIENT) {

				this.serverCtx.writeAndFlush(packet);

			} else {

				this.clientCtx.writeAndFlush(packet);
			}

		}

		ctx.fireChannelRead(msg);

	}
}
