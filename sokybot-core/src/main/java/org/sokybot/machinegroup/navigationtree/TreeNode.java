package org.sokybot.machinegroup.navigationtree;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.sokybot.utils.Helper;

import com.formdev.flatlaf.extras.FlatSVGIcon;


public class TreeNode  {

	 private String nodeName ; 
	 private Icon nodeIcon ;
	 
	 
	private TreeNode(String nodeName , Icon nodeIcon ) {
		this.nodeIcon = nodeIcon ; 
		this.nodeName = nodeName ; 
		
	}
	 
	public String getNodeName() {
		return nodeName;
	}
	
	public Icon getNodeIcon() {
		return nodeIcon;
	}
	
	 
	
	public static TreeNode makeTreeNode(String name , Icon icon ) { 
	  
	   if(icon == null) return  new TreeNode(name , null) ; 
	//	ImageIcon imageIcon =   new ImageIcon(Helper.fitimage(icon, 40, 40)) ; 
		//Icon i = new FlatSVGIcon() ; 
	   return new TreeNode(name , icon) ; 
	}
	

}
