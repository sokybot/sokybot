package org.sokybot.machinegroup.service;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.filters.FluentFilter;
import org.sokybot.app.Constants;
import org.sokybot.exception.NameUniquenessConstraintViolationException;
import org.sokybot.machine.MachineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

// This is just initial implementation , it is currently not suitable for production 
@Service
public class MachineService implements IMachineService {

	private ConfigurableApplicationContext groupCtx;

	private NitriteCollection machineRegister;

	private Map<String, ConfigurableApplicationContext> machines = new ConcurrentHashMap<>();

	@Value("${" + Constants.GROUP_NAME_KEY + "}")
	private String groupName;

	@Autowired
	public MachineService(ConfigurableApplicationContext groupCtx, Nitrite db) {

		this.groupCtx = groupCtx;
		this.machineRegister = db.getCollection(Constants.MACHINE_REGISTER_NAME);

	}

	@PostConstruct
	private void load() {

		this.machineRegister.find(FluentFilter.where(Constants.GROUP_NAME_KEY).eq(groupName)).forEach((doc) -> {
			createBotMachine(doc.get(Constants.MACHINE_NAME_KEY, String.class));
		});
	}

	private void check(String name) {
		Objects.requireNonNull(name, "Machine name required ");

		if (name.isBlank()) {
			throw new IllegalArgumentException("Invalid machine name ");
		}

		if (this.machines.containsKey(name)) {
			throw new NameUniquenessConstraintViolationException("Machine name must be unique", name);
		}
	}

	@Override
	public void createBotMachine(String name) {

		check(name);

		new SpringApplicationBuilder(MachineConfig.class)
		      .properties(Map.of("machineName", name , "spring.config.location" , "classpath:machine.properties"))
				.parent(groupCtx)
				.build()
				.run();

		Document machineDoc = this.machineRegister
				.find(FluentFilter.where(Constants.GROUP_NAME_KEY).eq(this.groupName)
				  .and(FluentFilter.where(Constants.MACHINE_NAME_KEY).eq(name)))
				.firstOrNull();
		if (machineDoc == null) {
			this.machineRegister.insert(Document.createDocument()
					.put(Constants.GROUP_NAME_KEY, this.groupName)
					.put(Constants.MACHINE_NAME_KEY, name)) ;
			
		}

	}

}
