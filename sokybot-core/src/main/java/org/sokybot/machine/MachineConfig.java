package org.sokybot.machine;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@ComponentScan({"org.sokybot.machine"})
//@EnableStateMachine
public class MachineConfig extends StateMachineConfigurerAdapter<MachineState, MachineEvent> {

	
	
	@Override
	public void configure(StateMachineTransitionConfigurer<MachineState, MachineEvent> transitions) throws Exception {
	    
		super.configure(transitions);
	}
	
	@Override
	public void configure(StateMachineStateConfigurer<MachineState, MachineEvent> states) throws Exception {
		
		super.configure(states);
	}
	
}
