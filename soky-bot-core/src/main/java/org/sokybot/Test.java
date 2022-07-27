package org.sokybot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sokybot.Driver;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

public class Test {
	
	
	public static void main(String args[]) throws Exception { 
		
		Driver d = new Driver() ; 
		Map<Object , Object> config = new HashMap<>() ; 
		List<BundleActivator> activator = new ArrayList<>() ; 
		activator.add(d) ; 
		
		config.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, activator) ; 
		
		
		Felix felix = new Felix(config) ;
		felix.init();
 
		
		deploy(felix);
		
		felix.start();
		
	}

	private static void deploy(Felix felix) throws IOException { 

		Files.list(Paths.get("E:\\CS\\java-projects\\soky-bot-1.1\\product\\system")).forEach((p)->{
		String bundleName = p.toFile().getName() ; 
		
		if(!bundleName.contains("core")) { 
			System.out.println( "Instaling " + p.toFile().getName()) ;
			try {
				felix.getBundleContext()
				.installBundle("file:" +p.toFile().getCanonicalPath()).start() ;
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		});
		
	}
	private static void deployViaAutoProcessor(Felix felix) { 
		
		
		
		AutoProcessor.process(Map.of(AutoProcessor.AUTO_DEPLOY_DIR_PROPERTY ,
						"E:\\CS\\java-projects\\soky-bot-1.1\\product\\system" , 
						"felix.log.level" , 1 ),
				felix.getBundleContext());
		

	}
	private static FrameworkFactory getFrameworkFactory() throws Exception {
		String resource = "META-INF/services/org.osgi.framework.launch.FrameworkFactory";
		java.net.URL url = Test.class.getClassLoader().getResource(resource);
		if (url != null) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
				for (String s = br.readLine(); s != null; s = br.readLine()) {
					s = s.trim();
					// Try to load first non-empty, non-commented line.
					if (s.length() > 0 && s.charAt(0) != '#') {
						return (FrameworkFactory) Class.forName(s).newInstance();
					}
				}
			}
		}
		throw new Exception("Could not find framework factory.");
	}
}
