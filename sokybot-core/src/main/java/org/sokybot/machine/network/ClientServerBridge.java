package org.sokybot.machine.network ; 
import org.sokybot.packet.ImmutablePacket;
import org.sokybot.packet.MutablePacket;
import org.sokybot.packet.NetworkPeer;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Sharable
@Component
@Slf4j
public class ClientServerBridge extends SimpleChannelInboundHandler<ImmutablePacket> {
	
	private ChannelHandlerContext clientCtx ; 
	private ChannelHandlerContext serverCtx ; 
	
 
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	 
		NetworkPeer peer = ctx.channel().attr(NetworkAttributes.TRANSPORT).get() ;
		if(peer == NetworkPeer.CLIENT) { 
			this.clientCtx = ctx ; 
		}else if(peer == NetworkPeer.SERVER) { 
			this.serverCtx = ctx ; 
		}
		
		super.channelActive(ctx);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ImmutablePacket msg) throws Exception {
		NetworkPeer peer = ctx.channel().attr(NetworkAttributes.TRANSPORT).get() ;
		
		MutablePacket packet = MutablePacket.wrap(msg.toBytes()) ; 
		packet.setPacketSource(peer);
		
		if(peer == NetworkPeer.CLIENT) { 
			// here we must apply filters 
			this.serverCtx.writeAndFlush(packet) ; 
		}else { 	
			this.clientCtx.writeAndFlush(packet) ; 
		}
		
		ctx.fireChannelRead(msg);
		
	}
}
