package org.sokybot.app;

import javax.swing.JButton;
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
import org.sokybot.gamegroupbuilder.GameConfigInputDialog;
import org.sokybot.mainframe.WindowPreparedEvent;
import org.sokybot.pk2extractor.EntityExtractorFactory;
import org.sokybot.pk2extractor.IEntityExtractorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class AppConfig {

	@Autowired
	private ApplicationContext ctx;

	@EventListener
	void registerCreateGameGroupBtn(WindowPreparedEvent event) {

		System.out.println("On Register Create Game");

		JButton btn = new JButton("Create Group");

		event.getToolBar().add(btn);

		btn.addActionListener((ev) -> {
			ctx.getBean(GameConfigInputDialog.class).setVisible(true);

//			SwingUtilities.invokeLater(()->{
//			GameConfigInputDialog dialog = 	 ; 
//			
//			
//				dialog.setVisible(true) ; 

			// });
		});

	}

	@Bean
	IEntityExtractorFactory gameEntityExtractorFactory() {
		return new EntityExtractorFactory();
	}

	@Bean
	JXFrame mainFrame(@Value("${spring.application.name}") String appName) {
		JXFrame frame = new JXFrame(appName, true);

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
