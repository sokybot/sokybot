package org.sokybot.machine.network;

import org.sokybot.security.Blowfish;
import org.sokybot.security.CRCSecurity;
import org.sokybot.security.CountSecurity;
import org.sokybot.security.IBlowfish;
import org.sokybot.security.ICRCSecurity;
import org.sokybot.security.ICountSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NetworkConfig {

	
	
	@Bean
	IBlowfish blowfish() { 
		return new Blowfish() ; 
	}
	
	@Bean
	ICRCSecurity crcSecurity() { 
		return new CRCSecurity() ; 
	}
	
	@Bean
	ICountSecurity countSecurity() { 
		return new CountSecurity() ; 
	}
}
