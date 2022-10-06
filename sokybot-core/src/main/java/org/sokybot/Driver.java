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
import org.sokybot.app.logger.AppenderWrapper;
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



@Slf4j
@ComponentScan({"org.sokybot.app"})
@Configuration
public class Driver {

	


    private static void initUIManager() {

    	log.info("initialize swing enverionment");
    	FlatDarkLaf.setup();

        UIManager.put("toolWindowManager.mainContainer.background" , Color.DARK_GRAY) ;

    }

    
    public static void main(String args[]) {
    	initUIManager();
 
    	SpringApplication.run(Driver.class, args ) ; 
    }



}
