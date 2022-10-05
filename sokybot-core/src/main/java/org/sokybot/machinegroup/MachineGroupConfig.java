package org.sokybot.machinegroup;


import javax.swing.Icon;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.formdev.flatlaf.icons.FlatSearchIcon;

@Configuration
@ComponentScan(basePackages = { "org.sokybot.machinegroup" })
public class MachineGroupConfig {

	@Bean
	Icon gameIcon(@Value("${gamePath}") String gamePath) {

		// here we must load game icon from game path and resize it to
		// this statement for test
		return new FlatSearchIcon();

	}

	

}
