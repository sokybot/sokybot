package org.sokybot.machine.actuator;

import org.slf4j.Logger;
import org.sokybot.app.Constants;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.StateEntry;
import org.sokybot.machine.UserConfig;
import org.sokybot.machine.UserConfig.BotType;
import org.sokybot.machine.service.IAuthenticationService;
import org.sokybot.machine.service.ICharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.annotation.WithStateMachine;

@WithStateMachine
public class AgentHandler {

	
	@Autowired
	private Logger log ; 
	
	
	
	@Autowired
	private ApplicationContext ctx ; 
	
	@Autowired
	private UserConfig config ;
	
	@Value("${" + Constants.MACHINE_NAME + "}")
	private String trainerName ; 
	
	@StateEntry(source =  MachineState.IDENTIFYING , target = MachineState.AUTHENTICATING)
	public void authenticating() { 
		log.info("Authenticating login ");
		this.ctx.getBean(IAuthenticationService.class).authenticate();  
		
	}
	
	@StateEntry(source =  MachineState.AUTHENTICATING , target =MachineState.LISTING)
	public void listingCharacters() {  
		if(this.config.getBotType() == BotType.CLIENTLESS) { 
			this.ctx.getBean(ICharacterService.class).listCharacters(); 
		}
	}
	
	
	
	@StateEntry(source =  MachineState.LISTING , target =MachineState.JOINING)
	public void joining() { 
		this.ctx.getBean(ICharacterService.class).joinCharacter(this.trainerName);
	}
	
}
