package org.sokybot.machinegroup.navigationtree;

import java.awt.Image;

import javax.swing.ImageIcon;

import org.sokybot.utils.Helper;


public class TreeNode  {

	 private String nodeName ; 
	 private ImageIcon nodeIcon ;
	 
	 
	private TreeNode(String nodeName , ImageIcon nodeIcon ) {
		this.nodeIcon = nodeIcon ; 
		this.nodeName = nodeName ; 
		
	}
	 
	public String getNodeName() {
		return nodeName;
	}
	
	public ImageIcon getNodeIcon() {
		return nodeIcon;
	}
	
	 
	
	public static TreeNode makeTreeNode(String name , Image icon ) { 
	  
	   if(icon == null) return  new TreeNode(name , null) ; 
		ImageIcon imageIcon =   new ImageIcon(Helper.fitimage(icon, 40, 40)) ; 
		return new TreeNode(name , imageIcon) ; 
	}
	

}
