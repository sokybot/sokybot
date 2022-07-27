package org.sokybot.app;


import com.formdev.flatlaf.icons.FlatSearchIcon;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXRootPane;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.sokybot.mainframe.WindowPreparedEvent;
import org.sokybot.service.IMainFrameConfigurator;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@Configuration
public class AppStartup {


	
	



//
//    ApplicationRunner loadPerspective(ToolWindowManager toolWindowManager) {
//        return args -> {
//
//            toolWindowManager.getPersistenceDelegate() ;
//            if(Files.exists(Paths.get(""))) {
//                toolWindowManager.getPersistenceDelegate().
//            }
//        } ;
//    }



    //@Profile({"dev" , "test"})
    @Bean
    ApplicationRunner addSomeToolWindows(IMainFrameConfigurator configurator) {
      return args -> {
          JPanel panel = new JPanel(new BorderLayout()) ;
          panel.add(new JScrollPane(new JTextArea()) , BorderLayout.CENTER) ;

          configurator.addExtraWindow("Console" , "Sokybot console" , new FlatSearchIcon(), panel );

          panel = new JPanel(new BorderLayout()) ;
          panel.add(new JScrollPane(new JTextArea()) , BorderLayout.CENTER) ;

          configurator.addExtraWindow("Log" , "Sokybot log" , new FlatSearchIcon() , panel);
          System.out.println("Console and log Windows has been added ");
      } ;
    }



    @Bean
    @Profile({"osgi" , "prod"})
    ApplicationRunner deployServices(ApplicationContext ctx) {
        return args -> {

        	
        	Bundle bundle = ctx.getBean(Bundle.class) ; 
        	
        	BundleContext bndCtx = bundle.getBundleContext() ; 
   
        	
        	
        	JMenuBar menuBar = ctx.getBean(JMenuBar.class) ;
        	//bndCtx.registerService(JMenuBar.class.toString(), menuBar, null) ;
        	bndCtx.registerService(JMenuBar.class, menuBar, null); 
        	
        	
        };
    }

   // very last operation
    @Bean
    ApplicationRunner displayFrame(ApplicationContext ctx) {

    	
        return (args)->{

            System.out.println("On Display Frame ") ;

            JXFrame mainFrame = ctx.getBean(JXFrame.class) ;

            JXRootPane rootPane = mainFrame.getRootPaneExt() ;
            JToolBar toolBar = ctx.getBean(JToolBar.class) ;
            rootPane.setToolBar(toolBar);

            JMenuBar menuBar  = getMenuBar(ctx) ;


            rootPane.setJMenuBar(menuBar);

            ToolWindowManager toolWindowManager = ctx.getBean(ToolWindowManager.class) ;


            if(toolWindowManager instanceof  Component) {
              //  rootPane.getCont((Container) toolWindowManager );
            	  rootPane.getContentPane().add((Component)toolWindowManager) ; 
            }else {
                System.exit(0);
            }


            mainFrame.setPreferredSize(new Dimension(1000 , 700));
            mainFrame.setStartPosition(JXFrame.StartPosition.CenterInScreen);
            mainFrame.pack();
            ctx.publishEvent(new WindowPreparedEvent(this , mainFrame , toolBar , menuBar ));

            mainFrame.setVisible(true);




        };
    }



    private JMenuBar getMenuBar(ApplicationContext ctx) {
        JMenuBar menuBar =  ctx.getBean(JMenuBar.class) ;
        menuBar.setHelpMenu(ctx.getBean("helpMenu" , JMenu.class));

        return menuBar ;
    }

    //
//    @Bean
//    ApplicationRunner deploy(){
//        return (arguments)->{
//            JOptionPane.showMessageDialog(null , "Hello World!!");
//        };
//
//    }


}
