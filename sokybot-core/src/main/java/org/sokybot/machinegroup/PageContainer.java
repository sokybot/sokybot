package org.sokybot.machinegroup;

import java.awt.CardLayout;
import java.awt.Color;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.sokybot.machinegroup.navigationtree.NavTreeSelectionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PageContainer extends JPanel {

	
	private CardLayout cardLayout  ; 
	

	
	@PostConstruct
	void init() { 
		this.cardLayout = new CardLayout() ; 
		
		setLayout(this.cardLayout);
		setBorder(BorderFactory.createEtchedBorder()) ; 
		setBackground(Color.yellow);
		
		
	}
	
	
	public void addPage(String name , java.awt.Component component) { 
		
		this.add(name  , component)  ; 
	
		
	}
	
	
	@EventListener
    public void showPage(NavTreeSelectionEvent navTreeSelectionEvent ) { 
    	
    String path = 	navTreeSelectionEvent.getSelectedPath() ;
    log.info("Main Page Container Recive Path {} " ,   path) ; 
    }
	
}
