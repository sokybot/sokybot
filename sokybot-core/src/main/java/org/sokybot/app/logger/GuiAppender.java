package org.sokybot.app.logger;


import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.swing.text.StyledEditorKit.FontSizeAction;

import org.noos.xing.mydoggy.plaf.ui.util.Colors;
import org.sokybot.common.ANSITextPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.osgi.io.OsgiBundleResourceLoader;
import org.springframework.osgi.io.internal.OsgiResourceUtils;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;



@Component
public class GuiAppender extends AppenderBase<ILoggingEvent> implements SmartLifecycle{

	
	@Autowired
	private ANSITextPane textPane ; 
	
	@Autowired
	private PatternLayout pattern ; 
	

	
	@Override
	public boolean isRunning() {
		  
		return super.isStarted();
	}

	@Override
	protected void append(ILoggingEvent logEvent) {
		 
	String message = 	this.pattern.doLayout(logEvent) ;
		this.textPane.appendAnsi(message );
		//System.out.println(logEvent.getFormattedMessage()) ; 
	}

	
	
  
	
	
	
}
