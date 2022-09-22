package org.sokybot.machinegroup;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;
import org.sokybot.app.service.IGameDistributionMaintainer;
import org.sokybot.machinegroup.navigationtree.INavTree;
import org.sokybot.service.IMainFrameConfigurator;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Configuration
public class MachineGroupLifecycle {

	

	private JComponent createMultiSplitMainContainer(ApplicationContext ctx) {

		JXMultiSplitPane jxMultiSplitPane = new JXMultiSplitPane();

		String layoutDef = "(ROW (LEAF name =left weight=0.2)(LEAF name=center weight=0.6)(LEAF name =right weight=0.2)";
		jxMultiSplitPane.getMultiSplitLayout().setModel(MultiSplitLayout.parseModel(layoutDef));

		jxMultiSplitPane.add(ctx.getBean(INavTree.class), "left");
		jxMultiSplitPane.add(ctx.getBean(PageContainer.class), "center");
		jxMultiSplitPane.add(ctx.getBean(DashboardContainer.class), "right");

		return jxMultiSplitPane;
	}

	private JComponent createDefaultMainContainer(ApplicationContext ctx) {

		JPanel mainCont = new JPanel(new BorderLayout());

		DashboardContainer dashboardContainer = ctx.getBean(DashboardContainer.class) ; 	
	    dashboardContainer.setPreferredSize(new Dimension(230, 0));
		
			
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ctx.getBean(INavTree.class),
				ctx.getBean(PageContainer.class));
		split.setDividerLocation(250);
		split.setDividerSize(2);
		mainCont.add(split, BorderLayout.CENTER);
		mainCont.add(dashboardContainer , BorderLayout.EAST) ; 
		
		
		return mainCont ; 

	}

	
	@Bean
	ApplicationRunner checkGameVersion(IGameDistributionMaintainer gameVersionRegistryManager) { 
		
		return (args)->{
			//	if(gameVersionRegistryManager.checkVersion(null)) { 
				    // we must then extract data from media pk2 
					// and update our db repositories and then update gameVersionRegistry
					// extraction process involves the following operations  
					// 1 - extract all items data from media pk2 and insert them into ItemEntityRepo 
					// 2 - 
					// finally extract current game version and use it to update game version registry 
			//	}
		};
		
	}
	
	@Bean
	@Order(1)
	ApplicationRunner installGUI(ApplicationContext ctx,
			// @Value("${groupName}") String groupName ,
			Icon gameIcon) {
		return (args) -> {
				String groupName = ctx.getEnvironment().getProperty("groupName") ; 
				
			ctx.getBean(IMainFrameConfigurator.class).addPage(groupName, gameIcon, 
					groupName, createDefaultMainContainer(ctx));

		};

	}

	/*
	 * Last operation , the aim of this operation is to save group information so
	 * let us retrieve and re-create this group at system startup stage
	 * 
	 */
	@Bean
	@Order(100)
	ApplicationRunner save() {
		return (arg) -> {
			
			System.out.println("Saving group configuration.....");
		};
	}

}
