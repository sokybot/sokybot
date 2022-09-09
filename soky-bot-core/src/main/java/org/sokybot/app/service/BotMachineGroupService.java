package org.sokybot.app.service;

import org.sokybot.machinegroup.MachineGroupConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class BotMachineGroupService implements IBotMachineGroupService {

	@Autowired
	private ConfigurableApplicationContext ctx ; 
	
	@Autowired
	private IGameDistributionMaintainer gameMaintainer; 
	
	
	@Override
	public void createNewGroup(String name, String gamePath) {
		
		
		 
		System.out.println("User Trying to create group with name "  + name  + " gamePath " + gamePath) ; 
		new SpringApplicationBuilder(MachineGroupConfig.class)
		.parent(this.ctx)
		 .bannerMode(Banner.Mode.OFF)
		 .properties("groupName:" + name  , "gamePath:" + gamePath)
		 .logStartupInfo(false)
		 .web(WebApplicationType.NONE)
		.run() ;
		
	}
 
	
	
}
