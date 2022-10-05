package org.sokybot.app;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.common.mapper.JacksonMapperModule;
import org.dizitart.no2.common.module.NitriteModule;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.jdesktop.swingx.JXFrame;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentUI;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.slf4j.LoggerFactory;
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
import org.springframework.context.event.EventListener;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
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
			SwingUtilities.invokeLater(()->{
				ctx.getBean(GameConfigInputDialog.class).setVisible(true);
				
			});

//			SwingUtilities.invokeLater(()->{
//			GameConfigInputDialog dialog = 	 ; 
//			
//			
//				dialog.setVisible(true) ; 

			// });
		});

	}

	@Bean
	CacheManager cacheManager() { 
		return new ConcurrentMapCacheManager("sokybot-cache") ; 
	}
	@Bean
	IEntityExtractorFactory gameEntityExtractorFactory() {
		return new Pk2Extractors();
	}

	@Bean
	JXFrame mainFrame(@Value("${spring.application.name}") String appName) {
	  log.info("creating main frame");
		JXFrame frame = new JXFrame(appName, true);
        //frame.setPreferredSize(new Dimension(1000 , 700));
        //frame.setStartPosition(JXFrame.StartPosition.CenterInScreen);
		
		//frame.setUndecorated(true);
		
		//frame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
       
		return frame;
	}

	@Bean
	ToolWindowManager toolWindowManager() {
		ToolWindowManager toolWindowManager = new MyDoggyToolWindowManager();

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

		return Nitrite.builder().loadModule(mvstoreModule()).loadModule(new JacksonMapperModule())
				.openOrCreate("soky", "soky");

	}

	private NitriteModule mvstoreModule() {
		return MVStoreModule.withConfig().filePath(System.getProperty("user.dir") + "\\Sokybot.data").build();

	}

}
