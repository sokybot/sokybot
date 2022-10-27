package org.sokybot.machine.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MachineDashboard  extends JPanel{

	
	@Autowired
	private MachineControll controll ; 
	
	
	@Autowired
	private TestPanel testPanel ; 
	
	
	
	@PostConstruct
	private void init() { 
		
		this.controll.setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());
		add(this.controll  , BorderLayout.PAGE_END) ; 
		add(this.testPanel , BorderLayout.CENTER) ; 
	}
	
	
	
	
	
	
	
	
	
}
