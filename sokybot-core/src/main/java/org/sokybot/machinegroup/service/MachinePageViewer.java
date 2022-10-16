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

	public void registerPage(String parent, String name, Icon icon, JComponent page) {

		String path = groupName + "." + parent ;
		navTree.putLeafNode(path, TreeNode.makeTreeNode(name, icon));
		this.pageContainer.addPage(path + "." + name, page);
		
	}
	

}
