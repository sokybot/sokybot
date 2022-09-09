package org.sokybot;

import com.formdev.flatlaf.FlatDarkLaf;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.boot.Banner;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.SpringApplication;
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


/*
 * TODO : implement Driver class using OSGI declarative service instead of implement BundleActivator 
 *  
 * 
 * 
 */

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
                .bannerMode(Banner.Mode.OFF)
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
