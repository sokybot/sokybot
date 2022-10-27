package org.sokybot.machine.network;

import java.util.Set;

import org.sokybot.network.NetworkPeer;
import org.sokybot.network.packet.ImmutablePacket;
import org.sokybot.network.packet.MutablePacket;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
@Component
@Lazy
public class ClientServerBridge extends SimpleChannelInboundHandler<ImmutablePacket> {

	private ChannelHandlerContext clientCtx;
	private ChannelHandlerContext serverCtx;
	private Set<Short> preventedOpcodes;

	public ClientServerBridge() {

		this.preventedOpcodes = Set.of(
				(short)0x5000
				,(short) 0x9000
				,(short) 0x2001
				//,(short) 0x6100
				//,(short) 0x6101
				//,(short) 0x6103
				//,(short)0x7007
				);

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		NetworkPeer peer = ctx.channel().attr(NetworkAttributes.TRANSPORT).get();
		if (peer == NetworkPeer.CLIENT) {
			this.clientCtx = ctx;
			log.debug("Client channel is now active");
		} else if (peer == NetworkPeer.SERVER) {
			this.serverCtx = ctx;
			log.debug("Server channel is now active");
		}
		super.channelActive(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ImmutablePacket msg) throws Exception {
		NetworkPeer peer = ctx.channel().attr(NetworkAttributes.TRANSPORT).get();

		MutablePacket packet = MutablePacket.wrap(msg.toBytes());
		packet.setPacketSource(peer);

		if (peer == NetworkPeer.CLIENT) {
			log.info("Client Send  {} ", packet);
			if (!this.preventedOpcodes.contains(packet.getOpcode())) {
				this.serverCtx.writeAndFlush(packet);
			}
		} else {
			log.info("Server Send {} ", packet);
			this.clientCtx.writeAndFlush(packet);
		}

		ctx.fireChannelRead(msg);

	}
}
