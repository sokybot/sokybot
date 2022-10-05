package org.sokybot.applauncher;

import java.util.Map;
import java.util.ServiceLoader;

import org.apache.felix.atomos.Atomos;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.connect.ConnectFrameworkFactory;
import org.osgi.framework.launch.Framework;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringOsgiDriver {

	
	
	
	@Bean
	Bundle systemBundle() { 
		ServiceLoader<ConnectFrameworkFactory> loader = ServiceLoader.load(ConnectFrameworkFactory.class);
	     ConnectFrameworkFactory factory = loader.findFirst().get();
	     Framework framework = factory.newFramework(
	                               Map.of(
	                                  Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT),
	                               Atomos.newAtomos().getModuleConnector());
	     
	     try {
			framework.init();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return framework ; 
		
	}
	
	public static void main(String arg[]) { 
		

	   new SpringApplicationBuilder(SpringOsgiDriver.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .properties("spring.application.name:sokybot" ,
                		//"logging.config:classpath:org/sokybot/logback-prod.xml",
                		"logging.level.root:INFO" , 
                		"spring.output.ansi.enabled:always")
                .profiles("dev"   , "init")
                .logStartupInfo(true)
                .run() ; 

	}
	
	
}
