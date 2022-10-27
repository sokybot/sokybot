package org.sokybot.machinegroup.service;

import javax.swing.Icon;
import javax.swing.JComponent;

public interface IMachinePageViewer {

	
	public void registerPage(String parent , String name , Icon icon , JComponent content ) ; 
	public void registerPage(String name , Icon icon , JComponent content ) ; 
	
	public void registerDashborad(String name , JComponent content) ;  

}
