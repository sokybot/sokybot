package org.sokybot.machine.logger;

import java.awt.Color;
import java.awt.Font;

import org.slf4j.LoggerFactory;
import org.sokybot.common.ANSITextPane;
import org.sokybot.common.GuiAppender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

@Configuration
public class MachineLogConfig {

	@Bean
	ANSITextPane machineLogTextPane() {
		ANSITextPane atp = new ANSITextPane();
		atp.setBackground(Color.BLACK);
		atp.setFont(new Font("Consolas", Font.PLAIN, 15));
		atp.setEditable(false);

		return atp;

	}
	
	@Bean
	PatternLayout patternLayout() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		PatternLayout pattern = new PatternLayout();
		pattern.setContext(context);

		pattern.setPattern(
				"%cyan([%date{dd MMM ;HH:mm:ss.SSS}]) %highlight(%-5level) %magenta(%logger{15}) - %green(%msg) %n");
		// pattern.setPattern("[%date{dd MMM ;HH:mm:ss.SSS}]%highlight(%logger{10}) :
		// %msg");
		pattern.start();

		return pattern;

	}


	@Bean
	AppenderBase<ILoggingEvent> machineLogAppender() {
		return GuiAppender.builder().pattern(patternLayout()).guiWriter(machineLogTextPane()).build();
	}

}
