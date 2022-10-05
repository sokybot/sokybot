package org.sokybot.applauncher;
import javax.swing.JFrame;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.springframework.boot.ApplicationRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class AppLifecycle {

	@Bean
	@Order(1)
	ApplicationRunner launchOsgiContainer(Bundle bundle ) {
		return args -> {
			bundle.start();
		};

	}

 
	@Bean
	@Order(2)
	ApplicationRunner displayMainFrame(JFrame mainFrame) { 
		return args -> mainFrame.setVisible(true);
	}
}
