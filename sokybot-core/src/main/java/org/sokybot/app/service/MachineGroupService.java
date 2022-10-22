package org.sokybot.app.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.filters.FluentFilter;
import org.osgi.framework.Bundle;
import org.sokybot.app.Constants;
import org.sokybot.exception.InvalidGameReferenceException;
import org.sokybot.exception.MachineGroupNotFoundException;
import org.sokybot.exception.NameUniquenessConstraintViolationException;
import org.sokybot.machinegroup.MachineGroupConfig;
import org.sokybot.machinegroup.service.IMachineService;
import org.sokybot.service.IGameDAO;
import org.sokybot.utils.SilkroadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MachineGroupService implements IMachineGroupService {

	@Autowired
	private ConfigurableApplicationContext ctx;

	private NitriteCollection machineGroupRegister;

	private Lock lock = new ReentrantLock();

	private Map<String, ConfigurableApplicationContext> ctxs = new ConcurrentHashMap<String, ConfigurableApplicationContext>();

	@Autowired
	public MachineGroupService(ConfigurableApplicationContext ctx,
			Nitrite db ) {
		this.ctx = ctx;
		this.machineGroupRegister = db.getCollection(Constants.MACHINE_GROUP_REGISTER_NAME);

	}

	@PostConstruct
	private void reload() {
		log.info("Reloading groups...");
		this.machineGroupRegister.find().forEach((doc) -> {

			String name = doc.get(Constants.GROUP_NAME_KEY, String.class);
			String gamePath = doc.get(Constants.GAME_PATH_KEY, String.class);

			createMachineGroup(name, gamePath);
			log.info("Group {} has been loaded ", name);
		});
	}

	private void saveOrUpdate(String name, String gamePath) {

		Document doc = this.machineGroupRegister.find(FluentFilter.where(Constants.GROUP_NAME_KEY).eq(name)).firstOrNull();

		if (doc == null) {
			doc = Document.createDocument(Constants.GROUP_NAME_KEY, name).put(Constants.GAME_PATH_KEY, gamePath);
		} else {
			doc.put(Constants.GAME_PATH_KEY, gamePath);
		}
		this.machineGroupRegister.update(doc, true);

	}

	@Override
	public void createMachineGroup(String name, String gamePath) {
		log.info("Creating new machine group with name {} at {}", name, gamePath);
		check(name, gamePath);

		try {

			lock.lock();

			ConfigurableApplicationContext newCtx = createNewContainer(name, gamePath);

			newCtx.setId(name);
			saveOrUpdate(name, gamePath);
			ctxs.put(name, newCtx);

		} finally {
			lock.unlock();
		}
	}

	private ConfigurableApplicationContext createNewContainer(String name, String gamePath) {
		
		return new SpringApplicationBuilder(MachineGroupConfig.class)
				// .resourceLoader(bundleResourceLoader)
				.parent(this.ctx)
				//.bannerMode(Banner.Mode.OFF)
				.properties("groupName:" + name, "gamePath:" + gamePath , "spring.config.location:classpath:machine-group-ctx.properties")
				
				//.logStartupInfo(false)
				//.web(WebApplicationType.NONE)
				
				.run();
	}

	private void check(String name, String gamePath) {
		if (name.isBlank())
			throw new IllegalArgumentException("Group name could not be blank");

		if (gamePath.isBlank())
			throw new IllegalArgumentException("Game path could not be blank");

		if (this.ctxs.containsKey(name))
			throw new NameUniquenessConstraintViolationException(
					"Each machine group is identifed by its name , so the name must be unique ", name);

		if (!SilkroadUtils.isValidSilkroadDirectory(gamePath))
			throw new InvalidGameReferenceException("Invalid game directory " + gamePath, gamePath);

	}

	@Override
	public Set<String> listGroups() {
		return this.ctxs.keySet();
	}

	@Override
	public IGameDAO getGameDAO(String groupName) {


		return getActiveGroupCtx(groupName).getBean(IGameDAO.class);

	}

	@Override
	public void createMachine(String parentGroup, String trainerName) {
 
		ApplicationContext ctx = getActiveGroupCtx(parentGroup) ; 
		
		IMachineService machineService = ctx.getBean(IMachineService.class) ;
		
		machineService.createBotMachine(trainerName);
		
	}
	
	private ConfigurableApplicationContext getActiveGroupCtx(String groupName) { 
		if (!this.ctxs.containsKey(groupName)) {
			throw new MachineGroupNotFoundException("Could not find machine group with name " + groupName, groupName);
		}

		ConfigurableApplicationContext targetCtx = this.ctxs.get(groupName);

		if (!targetCtx.isActive()) {
			throw new IllegalStateException("Could not interact with inactive group context " + groupName);
		}
		
		return targetCtx ; 
				
	}

}
