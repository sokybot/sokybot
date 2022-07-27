package org.sokybot.app;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXFrame;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.sokybot.gamegroupbuilder.GameConfigInputDialog;
import org.sokybot.mainframe.WindowPreparedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class AppConfig {

	
	@Autowired
	private ApplicationContext ctx ; 
	
	@EventListener
	void registerCreateGameGroupBtn(WindowPreparedEvent event) { 
		
		System.out.println("On Register Create Game") ; 
		
		JButton btn = new JButton("Create Group") ; 
		event.getToolBar().add(btn) ;
		
		btn.addActionListener((ev)->{
			
			SwingUtilities.invokeLater(()->{
			GameConfigInputDialog dialog = 	ctx.getBean(GameConfigInputDialog.class) ; 
			
			
				dialog.setVisible(true) ; 
				
			});
		});
		
	}
	
    @Bean
    JXFrame  mainFrame(@Value("${spring.application.name}") String appName) {
        JXFrame frame = new JXFrame(appName , true) ;
        return frame ;
    }

    @Bean
    ToolWindowManager toolWindowManager() {
        ToolWindowManager toolWindowManager = new MyDoggyToolWindowManager()  ;
        // TODO: 7/19/2022 configure it

        return toolWindowManager;

    }

}
