package org.sokybot.machinegroup.service;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.sokybot.machinegroup.DashboardContainer;
import org.sokybot.machinegroup.PageContainer;
import org.sokybot.machinegroup.navigationtree.INavTree;
import org.sokybot.machinegroup.navigationtree.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.icons.FlatSearchIcon;

@Component
public class MachinePageViewer implements IMachinePageViewer{

	@Autowired
	private INavTree navTree;

	@Autowired
	private PageContainer pageContainer;

	@Autowired
	private DashboardContainer dashboardContainer;

	@Value("${groupName}")
	private String groupName;

	
	@Override
	public void registerPage(String parentNode, String name, Icon icon, JComponent page) {

		String path = groupName + "." + parentNode ;
		navTree.putLeafNode(path, TreeNode.makeTreeNode(name, new FlatSearchIcon()));
		this.pageContainer.addPage(path + "." + name, page);
		
	}
	@Override
	public void registerPage(String name , Icon icon , JComponent comp) { 
	 
		navTree.putNode(groupName, TreeNode.makeTreeNode(name, icon));
		this.pageContainer.add(groupName + "." + name , comp) ; 
	}
	

}
