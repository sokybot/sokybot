package org.sokybot.machinegroup;

import java.awt.CardLayout;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.sokybot.machinegroup.navigationtree.NavTreeSelectionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DashboardContainer extends JPanel {

	
	private CardLayout cardLayout ; 
	
	
	
	@PostConstruct
	void init() { 
		
		this.cardLayout = new CardLayout() ; 
		this.setLayout(this.cardLayout);
		setBorder(BorderFactory.createEtchedBorder());
		
		
	}
	
	
	
	@EventListener
	public void showDashboard(NavTreeSelectionEvent navTreeSelectionEvent) { 
		
		String path = navTreeSelectionEvent.getSelectedPath() ; 
		
		System.out.println("DashboardContainer Recive event :  " + path) ; 
		
	}
	
	
}
