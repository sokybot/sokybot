package org.sokybot.machine;

import org.aspectj.weaver.ast.And;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.filters.FluentFilter;
import org.slf4j.Logger;
import org.sokybot.app.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
public class MachineConfig extends EnumStateMachineConfigurerAdapter<MachineState, MachineEvent> {

	@Autowired
	ApplicationContext ctx;

	@Autowired
	Logger log ; 
	
	@Override
	public void configure(StateMachineTransitionConfigurer<MachineState, MachineEvent> transitions) throws Exception {

		transitions.withExternal()
				.source(MachineState.DISCONNECTED)
				.target(MachineState.CONNECTED)
				.event(MachineEvent.CONNECT)

				.and()
				
				.withExternal()
				.source(MachineState.CONNECTED)
				.target(MachineState.DISCONNECTED)
				.event(MachineEvent.DISCONNECT)
				
				.and()
				
				.withExternal()
				.source(MachineState.CONFIG_UNCOMMITTED)
				.target(MachineState.CONFIG_COMMITTED) 
				.event(MachineEvent.CONFIG_COMMIT)
				
				.and()
				
				.withExternal()
				.source(MachineState.CONFIG_COMMITTED) 
				.target(MachineState.CONFIG_UNCOMMITTED) 
				.event(MachineEvent.CONFIG_MODIFIED) ;
		
	}

	@Override
	public void configure(StateMachineStateConfigurer<MachineState, MachineEvent> states) throws Exception {
		states.withStates()
		.initial(MachineState.DISCONNECTED)
		.state(MachineState.CONNECTED).and()
		.withStates()
		.initial(MachineState.CONFIG_COMMITTED)
		.state(MachineState.CONFIG_UNCOMMITTED);
	}

	@Override
	public void configure(StateMachineConfigurationConfigurer<MachineState, MachineEvent> config) throws Exception {

		config.withConfiguration().autoStartup(true);
	}

	@Bean
	UserConfig userConfig() {
		Nitrite db = this.ctx.getBean(Nitrite.class);
		String groupName = this.ctx.getParent().getEnvironment().getProperty(Constants.GROUP_NAME);
		String trainerName = this.ctx.getEnvironment().getProperty(Constants.MACHINE_NAME);
		NitriteCollection machineRegister = db.getCollection(Constants.MACHINE_REGISTER) ; 
		
		Document doc = machineRegister
				.find(FluentFilter.where(Constants.GROUP_NAME)
						.eq(groupName)
						.and(FluentFilter.where(Constants.MACHINE_NAME).eq(trainerName)))
				.firstOrNull();

		if (doc != null) {
			UserConfig res = doc.get(Constants.MACHINE_USER_CONFIG, UserConfig.class);
			if (res == null) {
				res = new UserConfig() ; 
				doc.put(Constants.MACHINE_USER_CONFIG, res) ; 
				machineRegister.update(doc) ; 
			}

			log.info("Loadded UserConfig : {} " , res) ; 
			return res;
		} else 
			throw new IllegalStateException("Machine register is missing");
		

		

	}

}
