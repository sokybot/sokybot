package org.sokybot.machinegroup;


import javax.swing.Icon;

import org.sokybot.gameloader.GameLoader;
import org.sokybot.service.IGameLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;

@Configuration
@ComponentScan(basePackages = { "org.sokybot.machinegroup" })
public class MachineGroupConfig {

	@Bean
	Icon gameIcon(@Value("${gamePath}") String gamePath) {

		// here we must load game icon from game path and resize it to
		// this statement for test
		// use FlatSVGIcon
		
		return new FlatSearchIcon();

	}

	
	@Bean 
	IGameLoader gameLoader() { 
		// now we depend on specific implementation 
	    // but we must get a target instance from GameLoaderRegistry service 
		// according to user configuration 
		return new GameLoader() ; 
	}
	

}
