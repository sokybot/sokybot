package org.sokybot;

import com.formdev.flatlaf.FlatDarkLaf;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import lombok.extern.slf4j.Slf4j;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;
import org.sokybot.app.logger.AppenderWraper;
import org.springframework.boot.Banner;
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
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.osgi.io.OsgiBundleResourceLoader;
import org.springframework.osgi.io.OsgiBundleResourcePatternResolver;

import javax.swing.*;

import java.awt.*;
import java.net.URL;
import java.util.Enumeration;


/*
 * TODO : implement Driver class using OSGI declarative service instead of implement BundleActivator 
 *  
 * 
 * 
 */


@Slf4j
@ComponentScan({"org.sokybot.app"})
@Configuration
//@SpringBootConfiguration
public class Driver implements BundleActivator {


	private  ConfigurableApplicationContext ctx ; 
	
	
	

	
	private static void configurBufferAppender() { 
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory() ;
		Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME) ; 
		logger.setAdditive(false);
		logger.setLevel(Level.INFO);
		Appender appender = new AppenderWraper() ; 
		appender.setName("d");
		appender.setContext(context);
		appender.start();
		
		logger.addAppender(appender);
		
	  Appender p =	logger.getAppender("d") ;
		 if(p == null) { 
			 System.out.println("Appender is not set properly ") ; 
		 }
	}
	
    @Override
    public void start(BundleContext context) throws Exception {
    	
    	

    	 Enumeration<URL> urls =	this.getClass().getClassLoader().getResources("banner.txt") ; 
      	while(urls.hasMoreElements()) { 
      		URL url = urls.nextElement() ; 
      		
      		System.out.println(url.toString()) ; 
      	}
    	//configurBufferAppender() ; 
    	//System.setProperty("logback.configurationFile", "./src/main/resources/logback.xml");
    	Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
 
    	//this.getClass().getClassLoader().getResource(null)
		OsgiBundleResourcePatternResolver bundleResourceLoader = new OsgiBundleResourcePatternResolver(
				context.getBundle());
    	  urls =	this.getClass().getClassLoader().getResources("banner.txt") ; 
     	while(urls.hasMoreElements()) { 
     		URL url = urls.nextElement() ; 
     		
     		System.out.println(url.toString()) ; 
     	}
    	//OsgiBundleResourceLoader bundleResourceLoader = new OsgiBundleResourceLoader(context.getBundle()); 
    	  
    	
		initUIManager();
    		this.ctx = new SpringApplicationBuilder(Driver.class)
        		.resourceLoader(bundleResourceLoader)
                .headless(false)
                .web(WebApplicationType.NONE)
                .properties("spring.application.name:sokybot" ,
                		//"logging.config:classpath:org/sokybot/logback-prod.xml",
                		"logging.level.root:INFO" , 
                		"spring.output.ansi.enabled:always")
                .profiles("dev"   , "init")
                .logStartupInfo(true)
                .initializers((ctx)->{
        			ctx.getBeanFactory().registerSingleton("bundle", context.getBundle()) ;          
        		   System.out.println("In Inialize system container") ;
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



    private static void initUIManager() {

    	log.info("initialize swing enverionment");
    	FlatDarkLaf.setup();

        UIManager.put("toolWindowManager.mainContainer.background" , Color.DARK_GRAY) ;

    }

    
    public static void main(String args[]) {
    //	configurBufferAppender(); 
    	initUIManager();
    	
//    	 LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory() ; 
//
//    	    PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();
//    	    logEncoder.setContext(logCtx);
//    	    logEncoder.setPattern("%-12date{YYYY-MM-dd HH:mm:ss.SSS} %-5level - %msg%n");
//    	    logEncoder.start();
//
//    	    AppenderWraper logConsoleAppender = new AppenderWraper();
//    	    logConsoleAppender.setContext(logCtx);
//    	    logConsoleAppender.setName("t");
//    	    //logConsoleAppender.setEncoder(logEncoder);
//    	    logConsoleAppender.start();
//  
//
//    	    Logger log = logCtx.getLogger("org.sokybot.app");
//    	    log.setAdditive(false);
//    	    log.setLevel(Level.INFO);
//    	    log.addAppender(logConsoleAppender);
//    	    log.info("On Configure Custom logger");
    	    
    	//, "logging.config:src/main/resources/logback-spring.xml"
		 new SpringApplicationBuilder(Driver.class)
                .headless(false)
                .bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.NONE)
                .properties("spring.application.name:sokybot" , 
                 	//	"logging.config:classpath:org/sokybot/logback-prod.xml",
                		"spring.output.ansi.enabled:always")
                .profiles("dev" , "prod" )
                .logStartupInfo(false)
                .run() ; 

    }



}
