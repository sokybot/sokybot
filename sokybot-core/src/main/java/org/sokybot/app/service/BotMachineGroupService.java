package org.sokybot.app.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.osgi.framework.Bundle;
import org.sokybot.exception.InvalidGameReferenceException;
import org.sokybot.exception.NameUniquenessConstraintViolationException;
import org.sokybot.machinegroup.MachineGroupConfig;
import org.sokybot.utils.SilkroadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BotMachineGroupService implements IBotMachineGroupService {

	@Autowired
	private ConfigurableApplicationContext ctx;

	private NitriteCollection machineGroupRegister;

	private Lock lock = new ReentrantLock();

	private Map<String, ConfigurableApplicationContext> ctxs = new ConcurrentHashMap<String, ConfigurableApplicationContext>();

	@Autowired
	public BotMachineGroupService(ConfigurableApplicationContext ctx,
			@Qualifier("machineGroupRegister") NitriteCollection machineGroupRegister) {
		this.ctx = ctx;
		this.machineGroupRegister = machineGroupRegister;
		
	}

	private void saveOrUpdate(String name, String gamePath) {
		Document doc = Document.createDocument("group-name", name).put("game-path", gamePath);

		this.machineGroupRegister.update(doc, true);
	}

	@Override
	public void createNewGroup(String name, String gamePath) {
		log.info("Creating new machine group with name {} at {}" , name , gamePath);
		if (name.isBlank())
			throw new IllegalArgumentException("Group name could not be blank");

		if (gamePath.isBlank())
			throw new IllegalArgumentException("Game path could not be blank");

		if (this.ctxs.containsKey(name))
			throw new NameUniquenessConstraintViolationException(
					"Each machine group is identifed by its name , so the name must be unique ", name);

		if (!SilkroadUtils.isValidSilkroadDirectory(gamePath))
			throw new InvalidGameReferenceException("Invalid game directory " + gamePath, gamePath);
		try {

			lock.lock();
			

			//OsgiBundleResourcePatternResolver bundleResourceLoader = new OsgiBundleResourcePatternResolver(
			//	this.ctx.getBean(Bundle.class));
		
			
			ConfigurableApplicationContext ctx = new SpringApplicationBuilder(MachineGroupConfig.class)
					//.resourceLoader(bundleResourceLoader)
					.parent(this.ctx)
					.bannerMode(Banner.Mode.OFF)
					.properties("groupName:" + name, "gamePath:" + gamePath)
					.logStartupInfo(false)
					.web(WebApplicationType.NONE)
					.run();

			ctx.setId(name);
			saveOrUpdate(name, gamePath);
			ctxs.put(name, ctx);
			
		} finally {
			lock.unlock();
		}
	}

}
