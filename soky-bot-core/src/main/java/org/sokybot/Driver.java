package org.sokybot;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.formdev.flatlaf.FlatDarkLaf;

import picocli.CommandLine.ExitCode;

import org.jdesktop.swingx.JXFrame;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.osgi.io.OsgiBundleResourceLoader;
import javax.swing.*;
import java.awt.*;


@ComponentScan(basePackages = {"org.sokybot.app" ,
								"org.sokybot.mainframe"  , 
								"org.sokybot.gamegroupbuilder"} )
@Configuration
public class Driver implements BundleActivator {


	private  ConfigurableApplicationContext ctx ; 
	
	
	

    @Override
    public void start(BundleContext context) throws Exception {
    	

    	Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

//		OsgiBundleResourcePatternResolver bundleResourceLoader = new OsgiBundleResourcePatternResolver(
//				context.getBundle());
//	
    	OsgiBundleResourceLoader bundleResourceLoader = new OsgiBundleResourceLoader(context.getBundle()); 
    	
		initUIManager();
		
    		this.ctx = new SpringApplicationBuilder(Driver.class)
        		.resourceLoader(bundleResourceLoader)
        		
                .headless(false)
                .web(WebApplicationType.NONE)
                .properties("spring.application.name:sokybot" , "logging.level.root:INFO")
                .profiles("dev" , "test" , "osgi")
                .logStartupInfo(false)
                .initializers((ctx)->{
        			ctx.getBeanFactory().registerSingleton("bundle", context.getBundle()) ;                 	
                }).run() ; 

		
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    	System.out.println("Bundle Stop Event ") ; 
    	SpringApplication.exit(ctx , ()->0 ) ;
    }
    
    
    @EventListener
    void onExit(ExitCodeEvent exitCode ) { 
     
    	JFrame frame = this.ctx.getBean(JFrame.class) ; 
     
    	if(frame != null) { 
    		frame.setVisible(false);
    		frame.dispose();
    	}else { 
    		System.out.println("On Exit with code " + exitCode.getExitCode()) ; 
    		System.out.println("Frame is null") ; 
    	}
    }


	
	public static void main(String args[]) { 
        initUIManager();
        new SpringApplicationBuilder(Driver.class)        	
                .headless(false)
                .web(WebApplicationType.NONE)
                .properties("spring.application.name:sokybot")
                .profiles("dev" , "test")
                .logStartupInfo(false)
                .run(args);
	}

    private static void initUIManager() {
        FlatDarkLaf.setup();

        UIManager.put("toolWindowManager.mainContainer.background" , Color.DARK_GRAY) ;

    }


 


}
