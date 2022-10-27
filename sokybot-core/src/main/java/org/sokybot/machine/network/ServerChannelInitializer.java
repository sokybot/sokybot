package org.sokybot.machine.network;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

@Component
@Scope("prototype")
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel>{

	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
	}
}
