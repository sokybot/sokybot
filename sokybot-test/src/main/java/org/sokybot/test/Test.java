package org.sokybot.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileStore;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.felix.fileinstall.internal.FileInstall;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.connect.ConnectModule;
import org.osgi.framework.connect.ModuleConnector;
import org.osgi.framework.launch.Framework;

public class Test {

	public static void main(String args[]) throws Exception {

		//System.setProperty("logback.configurationFile", "./../sokybot-static-resources/logback.xml");

		Properties prop = new Properties();
		prop.load(new FileInputStream(Paths.get("src", "main", "resources", "config.properties").toFile()));
		prop.forEach((k, v) -> System.out.println(k + " : " + v));

		BundleActivator activator = new FileInstall();
		List<BundleActivator> activators = new ArrayList<>();
		activators.add(activator);

		prop.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, activators);
		
		FrameworkFactory ff = getFrameworkFactory();
		Framework f = ff.newFramework(prop);
		
		f.start();

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
