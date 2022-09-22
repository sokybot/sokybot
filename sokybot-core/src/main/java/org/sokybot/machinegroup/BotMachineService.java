package org.sokybot.machinegroup;

import org.sokybot.machine.MachineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class BotMachineService implements IBotMachineService{

	@Autowired
	private ConfigurableApplicationContext groupCtx ; 
	
	
	@Override
	public void createBotMachine(String name) {
		new SpringApplicationBuilder(MachineConfig.class)
					.parent(groupCtx)
					.build() ; 
		
	}

	
}
