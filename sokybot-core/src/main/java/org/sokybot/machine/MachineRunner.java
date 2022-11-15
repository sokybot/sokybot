package org.sokybot.machine;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sokybot.app.Constants;
import org.sokybot.app.logger.AppenderWrapper;
import org.sokybot.common.GuiAppender;
import org.sokybot.machine.dashboard.MachineDashboard;
import org.sokybot.machine.page.IMachinePage;
import org.sokybot.machinegroup.service.IMachinePageViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.statemachine.StateMachine;

import com.formdev.flatlaf.icons.FlatSearchIcon;


@Configuration
public class MachineRunner {

	@Autowired
	ApplicationContext ctx;

	@Autowired
	Logger log ; 
	
	@Autowired
	StateMachine<MachineState, IMachineEvent> machine ; 
	
	
	@Bean
	@Order(1)
	ApplicationRunner installMachineGUIComponents(@Value("${" + Constants.MACHINE_NAME + "}") String machineName,
			IMachinePageViewer pageViewer) {
		return args -> {

			JPanel tmpPanel = new JPanel(new BorderLayout());
			tmpPanel.add(new JLabel(machineName));
			pageViewer.registerPage(machineName, new FlatSearchIcon(), tmpPanel);
			pageViewer.registerDashborad(machineName, this.ctx.getBean(MachineDashboard.class));
		};
	}

	@Bean
	@Order(2)
	ApplicationRunner installMachinePages(@Value("${" +Constants.MACHINE_NAME+"}") String machineName, List<IMachinePage> pages,
			IMachinePageViewer pageViewer) {
		return args -> {
			pages.forEach((p) -> {
				pageViewer.registerPage(machineName, p.getName(), p.getRepresentativeIcon(), p);
			});
		};
	}

	@Bean
	@Order(3)
	ApplicationRunner initializeUserConfig() {
		return args -> {
			
		        UserConfig config = this.ctx.getBean(UserConfig.class) ; 
		        
		        if(args.containsOption(Constants.MACHINE_AUTO_LOGIN)) { 
		        	config.setAutoLogin(true);
		        }
		        //config.setAutoLogin(args.containsOption(Constants.MACHINE_AUTO_LOGIN));
		        
		        args.getNonOptionArgs()
		        .stream()
		        .map((l)->l.split("="))
		        
		        .forEach((pair)->{
		        	
		        	switch(pair[0]) { 
		        	
		        	case Constants.MACHINE_TARGET_GATEWAY : 
		        		config.setTargetGateway(pair[1]) ;
		        		break ; 
		        	case Constants.MACHINE_USER_NAME : 
		        		config.setUsername(pair[1]) ; 
		        		break ; 
		        	case Constants.MACHINE_PASSWORD : 
		        		config.setPassword(pair[1]) ; 
		        		break ; 
		        	case Constants.MACHINE_PASSCODE : 
		        		config.setPasscode(pair[1]) ; 
		        		break ; 
		        	case Constants.MACHINE_TARGET_AGENT : 
		        		config.setTargetAgent(pair[1]) ; 
		        		break ; 
		        		
		         	
		      	}
		        });
		        
		     boolean isAcceptable =    machine.sendEvent(UserAction.CONFIG_COMMIT) ; 
		    log.info("CONFIG_COMMIT event is acceptable {} " , isAcceptable);
		};
	}
}
