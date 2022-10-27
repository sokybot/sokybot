package org.sokybot.machine.dashboard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.annotation.PostConstruct;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.sokybot.machine.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestPanel  extends JPanel implements ItemListener{

	private JCheckBox test1 ;
	private JCheckBox test2 ; 
	private JCheckBox test3 ; 
	
	
	@Autowired
	private UserConfig userConfig ; 
	
	
	
	
	public TestPanel() {
		this.test1 = new JCheckBox("test1") ; 
		this.test2 = new JCheckBox("test2") ; 
		this.test3 = new JCheckBox("test3");
	}
	
	
	
	@PostConstruct
	void init() { 
		
		this.test1.setSelected(this.userConfig.isTest1());
		this.test2.setSelected(this.userConfig.isTest2());
		this.test3.setSelected(this.userConfig.isTest3());
		
		this.test1.addItemListener(this);
		this.test2.addItemListener(this);
		this.test3.addItemListener(this);
		
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints() ;
		gbc.gridx = 0 ; 
		gbc.gridy = 0 ; 
		gbc.insets = new Insets(5, 5, 5, 5) ; 
		
		this.add(this.test1 , gbc) ; 
		gbc.gridy = 1 ; 
		this.add(this.test2 , gbc) ; 
		gbc.gridy = 2 ; 
		this.add(this.test3 , gbc) ;
		
		
	}
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
	 
		if(e.getSource() == this.test1) { 
			this.userConfig.setTest1(e.getStateChange() == ItemEvent.SELECTED);
		}else if(e.getSource() == this.test2) { 
			this.userConfig.setTest2(e.getStateChange() == ItemEvent.SELECTED);
		}else if(e.getSource() == this.test3) { 
			this.userConfig.setTest3(e.getStateChange() == ItemEvent.SELECTED);
		}
	}
	
}
