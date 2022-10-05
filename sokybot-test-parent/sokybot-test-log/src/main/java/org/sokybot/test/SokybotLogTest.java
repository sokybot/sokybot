package org.sokybot.test;

import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SokybotLogTest implements BundleActivator, ServiceListener {
	ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
	volatile  ScheduledFuture<?> s = null;
	BundleContext ctx;
	volatile  Logger logger;

	@Override
	public void start(BundleContext context) throws Exception {
		// System.setProperty("logback.configurationFile",
		// "./../sokybot-static-resources/logback.xml") ;

		log.info("Starting log test bundle");
		this.ctx = context;

		ServiceReference<?> ref = this.ctx.getServiceReference(LoggerFactory.class.getName()); 
		if(ref != null) {
		LoggerFactory lf = (LoggerFactory) this.ctx.getService(ref) ; 
		 if(lf != null) { 
			 this.logger = lf.getLogger(Logger.ROOT_LOGGER_NAME) ; 
			 startLogging();
		 }
		}
		 this.ctx.addServiceListener(this, "(objectClass=" + LoggerFactory.class.getName() + ")");
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
    
		stopLogging();
		this.logger = null ;

	}

	private void startLogging() {
		s = service.scheduleAtFixedRate(() -> {

			if (logger != null) {
				logger.info("log by osgi logger");
				logger.debug("log by osgi logger");
				logger.error("log by osgi logger");
				logger.warn("log by osgi logger");
				logger.audit("log by osgi logger");
				log.info("Slf4J Log") ; 
			} else {
				System.out.println("logger is still null");
			}

		}, 0, 5, TimeUnit.SECONDS);

	}

	private void stopLogging() {
		if (s != null) {
			s.cancel(true);
			s = null;

		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {

		Object obj = this.ctx.getService(event.getServiceReference());
		if (obj instanceof LoggerFactory) {
			if (event.getType() == ServiceEvent.REGISTERED) {
				System.out.println("LoggerFactory Service Registered");
				LoggerFactory loggerFactory = (LoggerFactory) obj;

				this.logger = loggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
				startLogging();
			}else if(event.getType() == ServiceEvent.UNREGISTERING) { 
				
				stopLogging();
				this.logger = null ;
						
			}
		}

	}

}
