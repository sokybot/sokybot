package org.sokybot.gameloader;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import sokybot.bot.IBotContext;
import sokybot.bot.model.BotModel;
import sokybot.logger.ILogger;
import sokybot.preferences.GeneralPreferencesDAO;
import sokybot.silkroadgroups.ISilkroadGroupContext;
import utilities.Helper;

@Component
public class GameLoader implements IGameLoader {

	
	@Autowired
	private ApplicationContext ctx ; 
	
	private IProcessLoader processLoader;
	
	public GameLoader() { 
	 
		this.processLoader = ProcessLoader.createInstance() ; 
	}

	@Override
	public void launch(IBotContext botCtx) {

		BotModel model = botCtx.getModel();
		ILogger logger = botCtx.getLogger();
		
		ISilkroadGroupContext group = this.ctx.getBean(model.getParrentGroup() , ISilkroadGroupContext.class) ; 
		

		if (group == null) {
			logger.log("Could not find silkroad info for bot ' " + model.getBotName() + " ' ");
			return ; 
		}

		String silkroadPath = group.getGamePath();

		if (silkroadPath == null || silkroadPath.isBlank() || !Helper.isSilkraodDirectory(new File(silkroadPath))) {
			logger.log("Invalid silkroad path");
			return;
		}
		silkroadPath+="\\sro_client.exe" ; 
		/*
		
		JMXFile file = new JMXFile(silkroadPath);

		if (!file.exists()) {

			logger.log("Silkroad path ' " + silkroadPath + " ' is not exists");
			return;
		}
       */
		
		GeneralPreferencesDAO generalPreferencesDAO = this.ctx.getBean(GeneralPreferencesDAO.class) ; 
		String dllPath = generalPreferencesDAO.getPatchPath(null);

		if (dllPath == null || dllPath.isBlank()) {
			logger.log("Invalid patch path");
			return;
		}

		File file = new File(dllPath);

		if (!file.exists()) {
			logger.log("Game Loader patch is not exists");
			return;
		}

		String shellPath = generalPreferencesDAO.getShellPath();

		if (shellPath == null || shellPath.isBlank()) {
			logger.log("Invalid shell path");
			return;
		}

		file = new File(shellPath);

		if (!file.exists()) {
			logger.log("Could not find shell code file");
			return;
		}

		String command = "0 /" + group.getGameModel().getSilkroadData().getLocal() + " 0 0";

		long phandle = this.processLoader.loadProcessImage(silkroadPath, command, dllPath, shellPath);
 
		model.setClientHandle(phandle);
		

	}
}
