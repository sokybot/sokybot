package org.sokybot.machine.network;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

@Component
@Lazy
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	
	@Autowired
	Logger log ;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

		log.info("Initializing client channel ") ; 
		
		
	}
}

