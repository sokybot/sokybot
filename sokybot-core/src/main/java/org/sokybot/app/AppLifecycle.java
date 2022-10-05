package org.sokybot.app;


import com.formdev.flatlaf.icons.FlatSearchIcon;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import lombok.extern.slf4j.Slf4j;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.NitriteCollection; 
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXRootPane;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;
import org.sokybot.app.logger.AppenderWraper;
import org.sokybot.app.logger.GuiAppender;
import org.sokybot.app.mainframe.WindowPreparedEvent;
import org.sokybot.app.service.IBotMachineGroupService;
import org.sokybot.common.ANSITextPane;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.service.IMainFrameConfigurator;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class AppLifecycle {


	
	

	


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


	@Bean
	ApplicationRunner configurLogger(ApplicationContext ctx) { 
		return args-> { 
           LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory() ; 
			Logger root = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME); 
			
			Appender<ILoggingEvent> appender = ctx.getBean(GuiAppender.class) ; 
			appender.setContext(loggerContext);
			
			
			root.addAppender(appender);
			root.setLevel(Level.INFO);
			root.setAdditive(false);
			
			
			
		};
	}
	//@Bean
	ApplicationRunner configLogger2(ApplicationContext ctx) { 
		return args -> { 
			log.info("Configuring root logger");
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory() ; 
			
			Logger root = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME); 
			AppenderWraper wrapper = (AppenderWraper) root.getAppender("d") ; 
		
			if(wrapper == null) { 
				System.out.println("Could not find wraper appender") ; 
			}
			GuiAppender guiAppender = ctx.getBean(GuiAppender.class) ; 
			if(guiAppender == null) { 
				System.out.println("Could not create gui appender") ; 
			}
			if(!guiAppender.isStarted()) { 
				System.out.println("Appender is not running") ; 
			}
			wrapper.origin(ctx.getBean(GuiAppender.class));
			System.out.println("After Configuer Logger"); 
			
		};
	}

    //@Profile({"dev" , "test"})
    @Bean
    ApplicationRunner addSomeToolWindows(IMainFrameConfigurator configurator , ApplicationContext ctx) {
      return args -> {
    	  log.info("adding console and log tool windows");
          JPanel panel = new JPanel(new BorderLayout()) ;
          panel.add(new JScrollPane(new JTextArea()) , BorderLayout.CENTER) ;

          configurator.addExtraWindow("Console" , "Sokybot console" , new FlatSearchIcon(), panel );

          panel = new JPanel(new BorderLayout()) ;
          panel.add(new JScrollPane(ctx.getBean(ANSITextPane.class)) , BorderLayout.CENTER) ;

          configurator.addExtraWindow("Log" , "Sokybot log" , new FlatSearchIcon() , panel);
          //System.out.println("Console and log Windows has been added ");
      } ;
    }



    //@Bean
    //@Profile({"osgi" , "prod"})
    ApplicationRunner deployServices(ApplicationContext ctx) {
        return args -> {

        	log.info("deploying service to osgi container");
        	Bundle bundle = ctx.getBean(Bundle.class) ; 
        	
        	BundleContext bndCtx = bundle.getBundleContext() ; 
   
        	
        	
        	JMenuBar menuBar = ctx.getBean(JMenuBar.class) ;
        	//bndCtx.registerService(JMenuBar.class.toString(), menuBar, null) ;
        	bndCtx.registerService(JMenuBar.class, menuBar, null); 
        	
        	IMainFrameConfigurator mainFrameConfigurator = ctx.getBean(IMainFrameConfigurator.class) ; 
        	bndCtx.registerService(IMainFrameConfigurator.class.getName(), mainFrameConfigurator, null); 
        	
        	
        };
    }


    @Bean
    @Profile({"init" })
    ApplicationRunner clearDB(@Qualifier("machineGroupRegister")  NitriteCollection machineGroup , Nitrite db) {
        return args -> {
        	log.info("inialize db");
        	machineGroup.find().forEach((doc)->{
        	
        		db.destroyRepository(ItemEntity.class , doc.get("game-path" , String.class));
        		db.destroyRepository(SkillEntity.class , doc.get("game-path" , String.class));
            		
        	});
        	machineGroup.clear();
        	
        	
        };
    }

    /**
     * load all previously registered machine groups 
     * 
     * 
     * @param botMachineGroupService
     * @param machineGroup
     * @return
     */
    
    @Bean
    ApplicationRunner loadGroups(IBotMachineGroupService botMachineGroupService , 
    		@Qualifier("machineGroupRegister")  NitriteCollection machineGroup) { 
    	
    	return (args)->{
    		log.info("loading groups");
    	  
    		// iterate over groups doc and for each group try to check if game path need update 
    		// if true then update our repositories related with this path 
    		// wither true of false then create new group using IBotMachineGroupService
    		machineGroup.find().forEach((doc)->{
    		
    			  botMachineGroupService.createNewGroup(doc.get("group-name",String.class),
    					  doc.get("game-path", String.class)) ; 
    		});
    		
    		
    	};
    }
    
   // very last operation
    @Bean
    ApplicationRunner displayFrame(ApplicationContext ctx) {

    	
        return (args)->{

        	log.info("displaying main frame");
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


            mainFrame.pack();
            ctx.publishEvent(new WindowPreparedEvent(this , mainFrame , toolBar , menuBar ));

            GraphicsEnvironment.getLocalGraphicsEnvironment()
    		.getScreenDevices()[0].setFullScreenWindow(mainFrame);
    		
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
