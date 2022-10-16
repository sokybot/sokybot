package org.sokybot.machine.page;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.AnnotationUtils;
import org.sokybot.machinegroup.service.IMachinePageViewer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class MachinePageInstaller implements ApplicationRunner  {

	
	@Autowired
	private IMachinePageViewer pageViewer ; 
	
	@Value("${machineName}")
	private String machineName ; 
	
	@Autowired
	List<IMachinePage> pages ; 
	
	
	
	 
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
	 
		  this.pages.forEach((p)-> {
			  pageViewer.registerPage(this.machineName, p.getName(),p.getRepresentativeIcon() , p) ; 
		  });
	}
}
