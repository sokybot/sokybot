package org.sokybot.applauncher;
import java.util.Enumeration;

import javax.annotation.PostConstruct;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleLogListener implements LogListener, ServiceListener {

	@Autowired
	private Bundle sysBundle;

	@PostConstruct
	private void listen() {
		try {
			this.sysBundle.getBundleContext().addServiceListener(this,
					"(objectClass=" + LogReaderService.class.getName() + ")");
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void logged(LogEntry arg0) {

	}

	@Override
	public void serviceChanged(ServiceEvent event) {

		ServiceReference<?> ref = event.getServiceReference();

		BundleContext bundleCtx = this.sysBundle.getBundleContext();

		Object obj = bundleCtx.getService(ref);

		if (obj instanceof LogReaderService) {

			if (event.getType() == ServiceEvent.REGISTERED) {

				LogReaderService service = (LogReaderService) obj;
				Enumeration<LogEntry> logs = service.getLog() ; 
				
				while(logs.hasMoreElements()) 
					logged(logs.nextElement()) ;
			
				service.addLogListener(this);
			} 

		}

	}

}
