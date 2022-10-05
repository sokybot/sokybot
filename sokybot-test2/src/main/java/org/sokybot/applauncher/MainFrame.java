package org.sokybot.applauncher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainFrame extends JFrame {

	@Autowired
	private ANSITextPane ansiTextPane ; 
	
	
	
	
	@PostConstruct
	private void init() { 
		
		JPanel container = (JPanel) getContentPane() ; 
		
		container.setLayout(new BorderLayout()); 
		container.add(this.ansiTextPane , BorderLayout.CENTER) ; 
		
		
		ansiTextPane.setBackground(Color.black);
		setPreferredSize(new Dimension(400 , 400));
		pack() ; 
	}
	
	
}
