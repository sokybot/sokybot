package org.sokybot.app;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.felix.atomos.Atomos;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.common.mapper.JacksonMapperModule;
import org.dizitart.no2.common.module.NitriteModule;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.jdesktop.swingx.JXFrame;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentManagerUI;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.connect.ConnectFrameworkFactory;
import org.osgi.framework.launch.Framework;
import org.sokybot.app.gamegroupbuilder.GameConfigInputDialog;
import org.sokybot.app.mainframe.WindowPreparedEvent;
import org.sokybot.pk2extractor.Pk2Extractors;
import org.sokybot.pk2extractor.IEntityExtractorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
public class AppConfig {

	@Autowired
	private ApplicationContext ctx;

	@EventListener
	void registerCreateGameGroupBtn(WindowPreparedEvent event) {

		log.info("Registering tool btn ");

		JButton btn = new JButton("Create Group");

		event.getToolBar().add(btn);

		btn.addActionListener((ev) -> {
			ctx.getBean(GameConfigInputDialog.class).setVisible(true);
		});

	}

	@Bean
	CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("sokybot-cache");
	}

	@Bean
	IEntityExtractorFactory gameEntityExtractorFactory() {
		return new Pk2Extractors();
	}

	@Bean
	@Profile("prod")
	JXFrame mainFrame(@Value("${spring.application.name}") String appName) {
		log.info("creating main frame");
		JXFrame frame = new JXFrame(appName, true);

		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		return frame;
	}

	@Bean
	@Profile("dev")
	JXFrame mainFrameForDev(@Value("${spring.application.name}") String appName) {
		log.info("creating main frame");
		JXFrame frame = new JXFrame(appName, true);
		frame.setPreferredSize(new Dimension(1000, 700));
		frame.setStartPosition(JXFrame.StartPosition.CenterInScreen);

		frame.setExtendedState(JXFrame.MAXIMIZED_BOTH);

		return frame;
	}

	@Bean
	ToolWindowManager toolWindowManager() {
		ToolWindowManager toolWindowManager = new MyDoggyToolWindowManager();

		ToolWindowManagerDescriptor toolWindowManagerDescriptor = toolWindowManager.getToolWindowManagerDescriptor();
		toolWindowManagerDescriptor.setNumberingEnabled(false);
		// toolWindowManagerDescriptor.setCornerComponent(Corner.NORD_EAST, new
		// JLabel("Hello World!!!"));
		ContentManagerUI<?> contentManagerUI = toolWindowManager.getContentManager().getContentManagerUI();

		contentManagerUI.setCloseable(false);
		contentManagerUI.setDetachable(false);
		contentManagerUI.setMinimizable(false);
		contentManagerUI.setMaximizable(false);

		if (contentManagerUI instanceof TabbedContentManagerUI) {
			TabbedContentManagerUI<?> tabbedContentManagerUI = (TabbedContentManagerUI<?>) contentManagerUI;
			tabbedContentManagerUI.setShowAlwaysTab(true);
		}

		return toolWindowManager;

	}

	@Bean
	Nitrite db() {

		return Nitrite.builder()
				.loadModule(mvstoreModule())
				.loadModule(new JacksonMapperModule())
				.openOrCreate("soky", "soky");

	}

	private NitriteModule mvstoreModule() {
		return MVStoreModule.withConfig().filePath(System.getProperty("user.dir") + "\\sokybot.data").build();

	}

	@Bean
	Bundle systemBundle(@Value("classpath:osgi.properties") Resource configFile) throws IOException {

		Properties prop = new Properties();
		prop.load(configFile.getInputStream());

		Map<String, String> config = new HashMap<>();
		prop.forEach((k, v) -> {
			config.put(String.valueOf(k), String.valueOf(v));
		});

		ServiceLoader<ConnectFrameworkFactory> loader = ServiceLoader.load(ConnectFrameworkFactory.class);
		ConnectFrameworkFactory factory = loader.findFirst().get();

		Framework framework = factory.newFramework(config, Atomos.newAtomos().getModuleConnector());

		try {
			framework.init();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return framework;

	}

}
