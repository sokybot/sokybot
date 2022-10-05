package org.sokybot;

import com.formdev.flatlaf.FlatDarkLaf;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.LoggerFactory;
import org.sokybot.app.logger.AppenderWrapper;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.swing.*;

import java.awt.*;

@Slf4j
@ComponentScan({ "org.sokybot.app" })
@Configuration

public class Driver {

	private static void configurBufferAppender() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.setAdditive(false);
		logger.setLevel(Level.INFO);
		Appender appender = new AppenderWrapper();
		appender.setName("d");
		appender.setContext(context);
		appender.start();

		logger.addAppender(appender);

		Appender p = logger.getAppender("d");
		if (p == null) {
			System.out.println("Appender is not set properly ");
		}
	}

	private static void initUIManager() {
		FlatDarkLaf.setup();

		UIManager.put("toolWindowManager.mainContainer.background", Color.DARK_GRAY);

	}

	public static void main(String args[]) {
		initUIManager();

		SpringApplication.run(Driver.class, args);

	}

}
