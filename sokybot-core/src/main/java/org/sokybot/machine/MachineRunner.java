package org.sokybot.machine;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

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

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MachineRunner {

	@Autowired
	ApplicationContext ctx;

	@Autowired
	StateMachine<MachineState, MachineEvent> machine ; 
	
	@Bean
	@Order(1)
	ApplicationRunner installMachineGUIComponents(@Value("${" + Constants.MACHINE_NAME + "}") String machineName,
			IMachinePageViewer pageViewer) {
		return args -> {
			log.info("Installing machine gui components ");

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
			log.info("Installing machine pages");

			log.debug("Machine Pages {}", pages);
			pages.forEach((p) -> {
				log.debug("Insalling page {} ", p.getName());
				pageViewer.registerPage(machineName, p.getName(), p.getRepresentativeIcon(), p);
			});
		};
	}

	@Bean
	@Order(3)
	ApplicationRunner initializeUserConfig() {
		return args -> {
			
			boolean needCommit = false ; 
			UserConfig config = this.ctx.getBean(UserConfig.class) ; 
			
			if(args.containsOption(Constants.MACHINE_TARGET_HOST)) { 
				String targetHost = args.getOptionValues(Constants.MACHINE_TARGET_HOST).get(0) ; 
				config.setTargetHost(targetHost);
				log.debug("Initial target host {} " , targetHost);
				needCommit = true ; 
			}
			
			
			if(needCommit) { 
				machine.sendEvent(MachineEvent.CONFIG_COMMIT) ;
			}
		};
	}
}
