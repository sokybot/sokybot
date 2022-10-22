package org.sokybot.machine;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.LoggerFactory;
import org.sokybot.app.logger.AppenderWrapper;
import org.sokybot.common.GuiAppender;
import org.sokybot.machine.page.IMachinePage;
import org.sokybot.machinegroup.service.IMachinePageViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.formdev.flatlaf.icons.FlatSearchIcon;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MachineRunner {

	
	@Autowired
	ApplicationContext ctx ; 
	
	
	
	
	@Bean
	@Order(1)
	ApplicationRunner installMachineGUIComponents(@Value("${machineName}")String machineName ,
			IMachinePageViewer pageViewer) { 
		return args->{
			log.info("Installing machine gui components ");
			
			JPanel tmpPanel = new JPanel(new BorderLayout());
			tmpPanel.add(new JLabel(machineName)) ;
			pageViewer.registerPage(machineName, new FlatSearchIcon(), tmpPanel);
		};
	}
	
	@Bean
	@Order(2)
	ApplicationRunner installMachinePages(@Value("${machineName}") String machineName ,
			List<IMachinePage> pages, 
			IMachinePageViewer pageViewer ) { 
		return args->{
			log.info("Installing machine pages");

				log.debug("Machine Pages {}"  , pages);
			  pages.forEach((p)-> {
				  log.debug("Insalling page {} " , p.getName());
 				  pageViewer.registerPage(machineName, p.getName(),p.getRepresentativeIcon() , p) ; 
			  });
		};
	}
}
