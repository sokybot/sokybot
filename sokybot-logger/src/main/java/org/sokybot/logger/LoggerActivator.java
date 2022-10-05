package org.sokybot.logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LoggerFactory;


public class LoggerActivator implements BundleActivator , LogListener  , ServiceListener{

	private ANSITextPane ansiTextPane = new ANSITextPane() ; 
	private LogReaderService reader ; 
	private BundleContext ctx ; 
	private JFrame frame = new JFrame() ;
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		
		this.ctx = context ; 
		
		ServiceReference<?> ref =   this.ctx.getServiceReference(LogReaderService.class.getName());
		
		this.reader = (LogReaderService) this.ctx.getService(ref) ; 
		 
		if(this.reader != null) { 
			process();
			this.reader.addLogListener(this);
		}
		
		context.addServiceListener(this, "(objectClass="+LogReaderService.class.getName()+")");
		 
		JPanel panel = (JPanel) this.frame.getContentPane() ; 
		panel.setLayout(new BorderLayout()); 
		panel.add(new JScrollPane(this.ansiTextPane) , BorderLayout.CENTER);
		this.ansiTextPane.setBackground(Color.black);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setPreferredSize(new Dimension(500 , 500));
		frame.setLocationRelativeTo(null);
		frame.pack(); 
		frame.setVisible(true);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	 
		if(this.reader != null) { 
			this.reader.removeLogListener(this);
		}
		if(this.ctx != null) { 
			this.ctx.removeServiceListener(this);
		}
		this.reader = null ;
		this.ctx = null ; 
		this.frame.setVisible(false);;
	}

	@Override
	public void logged(LogEntry log) {
		this.ansiTextPane.appendAnsi("[" + log.getLogLevel().toString() + "]  " + log.getMessage()+"\n");
		
	}

	private void process() { 
		Enumeration<LogEntry> logs = this.reader.getLog() ; 
		  
		while(logs.hasMoreElements()) { 
			LogEntry log = logs.nextElement() ; 
			this.ansiTextPane.appendAnsi(log.getMessage() + "\n");
		}

	}
	@Override
	public void serviceChanged(ServiceEvent event) {
		 System.out.println("On Service Changed") ; 
		 Object obj = this.ctx.getService(event.getServiceReference()) ; 
		 
		if(obj instanceof LogReaderService) { 
			
		 if(event.getType() == ServiceEvent.REGISTERED) { 
				System.out.println("LogReaderService has been registered") ; 
				this.reader = (LogReaderService) obj ; 
				process() ; 
				this.reader.addLogListener(this);
				 
			}
		}else if(event.getType() == ServiceEvent.UNREGISTERING) { 
		  
			this.reader.removeLogListener(this);
			this.reader = null ; 
		  
		}
	}
	
	}

	
	
	

