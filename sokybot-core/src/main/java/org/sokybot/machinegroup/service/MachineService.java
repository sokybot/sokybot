package org.sokybot.machinegroup.service;

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
	// private ObjectRepository<UserConfig> userConfigRepo;

	private Map<String, ConfigurableApplicationContext> machines = new ConcurrentHashMap<>();

	@Value("${" + Constants.GROUP_NAME + "}")
	private String groupName;

	@Autowired
	public MachineService(ConfigurableApplicationContext groupCtx, Nitrite db) {

		this.groupCtx = groupCtx;
		this.machineRegister = db.getCollection(Constants.MACHINE_REGISTER);
		// this.userConfigRepo = db.getRepository(UserConfig.class);
	}

	@PostConstruct
	private void load() {

		this.machineRegister.find(FluentFilter.where(Constants.GROUP_NAME).eq(groupName)).forEach((doc) -> {
			createBotMachine(doc.get(Constants.MACHINE_NAME, String.class));
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
	public void createBotMachine(String name, String... options) {
		check(name);

		 getMachineDoc(name);

		new SpringApplicationBuilder(MachineConfig.class)
				.properties(Map.of(Constants.MACHINE_NAME, name, Constants.GROUP_NAME, this.groupName,
						"spring.config.location", "classpath:machine.properties"))
				.parent(groupCtx)
				.build()
				.run(options);

	}

	@Override
	public void createBotMachine(String name) {
		createBotMachine(name , new String[] {}); // here we can pass some initial options to the container 
	}

	private Document getMachineDoc(String name) {
		Document machineDoc = this.machineRegister.find(FluentFilter.where(Constants.GROUP_NAME)
				.eq(this.groupName)
				.and(FluentFilter.where(Constants.MACHINE_NAME).eq(name))).firstOrNull();
		if (machineDoc == null) {

			machineDoc = Document.createDocument()
					.put(Constants.GROUP_NAME, this.groupName)
					.put(Constants.MACHINE_NAME, name);
			// .put(Constants.MACHINE_USER_CONFIG, userConfig);

			this.machineRegister.update(machineDoc , true);

		}

		return machineDoc;

	}

}
