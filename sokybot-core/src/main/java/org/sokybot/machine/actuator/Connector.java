package org.sokybot.machine.actuator;

import org.slf4j.Logger;
import org.sokybot.app.Constants;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.Transition;
import org.sokybot.machine.UserConfig;
import org.sokybot.machine.network.ClientChannelInitializer;
import org.sokybot.machine.network.ServerChannelInitializer;
import org.sokybot.service.IGameDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.annotation.WithStateMachine;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


@WithStateMachine
public class Connector  {

	
	@Autowired
	UserConfig config ;
	
	
	@Autowired
	ApplicationContext ctx ; 
	
	
	@Autowired
	Logger log ;

	@Transition(source = MachineState.DISCONNECTED , target = MachineState.CONNECTED)
	public void connect() { 
		// 1 - if bot type is client then listen to client on port 15000
		// 2 - load client using gameLoader 
		// 3 - once client connected then connect to target server 
		
		
		System.out.println("Trying to log with " + log.getName()) ; 
		
		log.info("Trying to reach  {} "  , this.config.getTargetHost());
		
	}
	
	private ChannelFuture connectToTargetServer() { 
		return new Bootstrap()
				.group(this.ctx.getBean(EventLoopGroup.class))
				.channel(NioSocketChannel.class)
				.handler(this.ctx.getBean(ServerChannelInitializer.class))
				.connect(this.ctx.getBean(UserConfig.class).getTargetHost(),
						this.ctx.getBean(IGameDAO.class).getPort());
				
	}
	private ChannelFuture listenToClient() { 
		 return  new ServerBootstrap()
				 .group(this.ctx.getBean(EventLoopGroup.class))
				 .channel(NioServerSocketChannel.class)
				 .childHandler(this.ctx.getBean(ClientChannelInitializer.class))
				 .bind(Constants.CLIENT_DEFAULT_PORT); 
		
	}
	

	@Transition(source = MachineState.CONNECTED, target = MachineState.DISCONNECTED)
	public void disconnect()  { 
		log.info("Disconnecting from  {} "  , this.config.getTargetHost());


	}
}
