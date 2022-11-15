package org.sokybot.machine;

import java.util.concurrent.Executor;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.filters.FluentFilter;
import org.slf4j.Logger;
import org.sokybot.app.Constants;
import org.sokybot.machine.UserConfig.BotType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.DefaultManagedTaskExecutor;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@ComponentScan({ "org.sokybot.machine" })
@EnableStateMachine
@EnableAspectJAutoProxy
@EnableAsync
public class MachineConfig extends StateMachineConfigurerAdapter<MachineState, IMachineEvent> {

	@Autowired
	ApplicationContext ctx;


	
	
	@Autowired
	Logger log;

	
	@Override
	public void configure(StateMachineStateConfigurer<MachineState, IMachineEvent> states) throws Exception {

		states.withStates()
				.initial(MachineState.RUNNING)
				.and()
				.withStates()
				.parent(MachineState.RUNNING)
				.initial(MachineState.CONFIG_COMMITTED)
				.state(MachineState.CONFIG_UNCOMMITTED)
				.and()
				.withStates()
				.parent(MachineState.RUNNING)
				.initial(MachineState.READY)
				.state(MachineState.DISCONNECTING)
				.state(MachineState.INTERACTING)
				.and()
				.withStates()
				.parent(MachineState.INTERACTING)
				.initial(MachineState.STARTING)
				.choice(MachineState.CLIENTLESS)
				.state(MachineState.LAUNCHING)
				.state(MachineState.CONNECTING)
				.state(MachineState.REDIRECTING)
				.state(MachineState.HANDSHAKING)
				.state(MachineState.CHALENGING)
				.state(MachineState.REFUSING)
				.state(MachineState.IDENTIFYING)
				.state(MachineState.VERIFYING)
				.state(MachineState.UPDATING)
				.state(MachineState.DISCOVERING)
				.state(MachineState.LOGINING)
				.state(MachineState.AUTHENTICATING)
				.state(MachineState.LISTING)
				.state(MachineState.JOINING)
				.exit(MachineState.REFUSING)
				.exit(MachineState.UPDATING)
				.and()
				.withStates()
				.parent(MachineState.RUNNING)
				.initial(MachineState.WITHOUT_CLIENT)
				.state(MachineState.WITH_CLIENT);
				
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<MachineState, IMachineEvent> transitions) throws Exception {

		
		transitions
		.withExternal()
		.source(MachineState.WITHOUT_CLIENT)
		.target(MachineState.WITH_CLIENT)
		.event(UserAction.CLIENT_ATTACHED)
		.and()
		.withExternal()
		.source(MachineState.WITH_CLIENT)
		.target(MachineState.WITHOUT_CLIENT)
		.event(UserAction.KILL_CLIENT)
		.and()
		.withExternal()
		.source(MachineState.CONFIG_UNCOMMITTED)
		.target(MachineState.CONFIG_COMMITTED) 
		.event(UserAction.CONFIG_COMMIT)
		.and()
		.withExternal()
		.source(MachineState.CONFIG_COMMITTED) 
		.target(MachineState.CONFIG_UNCOMMITTED) 
		.event(UserAction.CONFIG_MODIFIED) 
		.and()
		.withLocal()
		.source(MachineState.READY)
		.target(MachineState.INTERACTING)
		.event(UserAction.CONNECT)
		.and()
		.withLocal()
		.source(MachineState.INTERACTING)
		.target(MachineState.DISCONNECTING)
		.event(UserAction.DISCONNECT)
		.and()
		.withLocal()
		.source(MachineState.DISCONNECTING) 
		.target(MachineState.READY)
		.and()
		.withLocal()
		.source(MachineState.STARTING) 
		.target(MachineState.CLIENTLESS) 
		.and()
		.withChoice()
		.source(MachineState.CLIENTLESS)
		.first(MachineState.CONNECTING, (ctx)->userConfig().getBotType() == BotType.CLIENTLESS)
		.last(MachineState.LAUNCHING) 
		.and()
		.withLocal()
		.source(MachineState.LAUNCHING)
		.target(MachineState.CONNECTING)
		.event(ClientFeed.CLIENT_CONNECTED)
		.and()
		.withLocal()
		.source(MachineState.CONNECTING)
		.target(MachineState.HANDSHAKING)
		.event(ServerFeed.SETUP)
		.and()
		.withLocal()
		.source(MachineState.REDIRECTING)
		.target(MachineState.HANDSHAKING)
		.event(ServerFeed.SETUP)
		.and()
		.withLocal()
		.source(MachineState.HANDSHAKING)
		.target(MachineState.CHALENGING)
		.event(ServerFeed.CHALLENGE)
		.and()
		.withLocal()
		.source(MachineState.CHALENGING)
		.target(MachineState.REFUSING)
		.event(ClientFeed.CONNECTION_REFUSED) 
		.and()
		.withExit()
		.source(MachineState.REFUSING)
		.target(MachineState.DISCONNECTING)
		.and()
		.withLocal()
		.source(MachineState.CHALENGING)
		.target(MachineState.IDENTIFYING)
		.event(ClientFeed.CONNECTION_ACCEPTED)
		.and()
		.withLocal()
		.source(MachineState.IDENTIFYING)
		.target(MachineState.VERIFYING)
		.event(ServerFeed.GATEWAY_CONNECTED)
		.and()
		.withLocal()
		.source(MachineState.VERIFYING)
		.target(MachineState.UPDATING)
		.event(ServerFeed.INCOMPATIBLE)
		.and()
		.withExit()
		.source(MachineState.UPDATING)
		.target(MachineState.DISCONNECTING)
		.and()
		.withLocal()
		.source(MachineState.VERIFYING)
		.target(MachineState.DISCOVERING)
		.event(ServerFeed.COMPATIBLE)
		.and()
		.withLocal()
		.source(MachineState.DISCOVERING)
		.target(MachineState.LOGINING)
		.event(ServerFeed.AGENT_lISTED)
		.and()
		.withLocal()
		.source(MachineState.LOGINING)
		.target(MachineState.REDIRECTING)
		.event(ServerFeed.LOGIN_SUCCESS)
		.and()
		.withLocal()
		.source(MachineState.IDENTIFYING)
		.target(MachineState.AUTHENTICATING)
		.event(ServerFeed.AGENT_CONNECTED)
		.and()
		.withLocal()
		.source(MachineState.AUTHENTICATING)
		.target(MachineState.LISTING)
		.event(ServerFeed.AUTHENTICATED)
		.and()
		.withLocal()
		.source(MachineState.LISTING)
		.target(MachineState.JOINING)
		.event(ServerFeed.LISTED);
		
		
		
		
		
		
	}


	@Override
	public void configure(StateMachineConfigurationConfigurer<MachineState, IMachineEvent> config) throws Exception {

		config.withConfiguration().taskExecutor(new DefaultManagedTaskExecutor()).autoStartup(true);
	}

	@Bean
	UserConfig userConfig() {
		Nitrite db = this.ctx.getBean(Nitrite.class);
		String groupName = this.ctx.getParent().getEnvironment().getProperty(Constants.GROUP_NAME);
		String trainerName = this.ctx.getEnvironment().getProperty(Constants.MACHINE_NAME);
		NitriteCollection machineRegister = db.getCollection(Constants.MACHINE_REGISTER);

		Document doc = machineRegister.find(FluentFilter.where(Constants.GROUP_NAME)
				.eq(groupName)
				.and(FluentFilter.where(Constants.MACHINE_NAME).eq(trainerName))).firstOrNull();

		if (doc != null) {
			UserConfig res = doc.get(Constants.MACHINE_USER_CONFIG, UserConfig.class);
			if (res == null) {
				res = new UserConfig();
				doc.put(Constants.MACHINE_USER_CONFIG, res);
				machineRegister.update(doc , true);
			}

			log.info("Loadded UserConfig : {} ", res);
			return res;
		} else
			throw new IllegalStateException("Machine register is missing");

	}

}
