package org.sokybot.machine.logger;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JScrollPane;

import org.sokybot.common.ANSITextPane;
import org.sokybot.machine.page.IMachinePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogPage extends IMachinePage {

	
	@Autowired
	private ANSITextPane machineLogTextPane ; 
	
	
	
	@PostConstruct
	void init() {
  
		
		
		setLayout(new BorderLayout());
		add(new JScrollPane(this.machineLogTextPane) , BorderLayout.CENTER);
	}

	@Override
	public String getName() {

		return super.LOG_NAME;
	}

	@Override
	public String getIcon() {

		return "classpath:icons/feed.svg";
	}
	
	
	
}
