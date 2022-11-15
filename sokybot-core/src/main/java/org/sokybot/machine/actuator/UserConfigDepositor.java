package org.sokybot.machine.actuator;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.Invocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.filters.FluentFilter;
import org.slf4j.Logger;
import org.sokybot.app.Constants;
import org.sokybot.machine.IMachineEvent;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.Transition;
import org.sokybot.machine.UserAction;
import org.sokybot.machine.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.WithStateMachine;

@Aspect
@WithStateMachine
public class UserConfigDepositor {

	@Autowired
	private Logger log;

	@Value("${" + Constants.GROUP_NAME + "}")
	private String groupName;

	@Value("${" + Constants.MACHINE_NAME + "}")
	private String machineName;

	private List<ProceedingJoinPoint> invocations = new ArrayList<>();

	@Autowired
	private StateMachine<MachineState, IMachineEvent> machine;

	private NitriteCollection machineRegister;

	@Autowired
	public UserConfigDepositor(Nitrite db) {

		this.machineRegister = db.getCollection(Constants.MACHINE_REGISTER);

	}

	@Around("execution(* org.sokybot.machine.UserConfig.set*(..))")
	public void defer(ProceedingJoinPoint joinPoint) {

		if (joinPoint.getSignature().getName().startsWith("get")) {
			try {
				joinPoint.proceed();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		} else {
			this.invocations.add(joinPoint);
			this.machine.sendEvent(UserAction.CONFIG_MODIFIED);
		}
	}

	@Transition(source = MachineState.CONFIG_UNCOMMITTED, target = MachineState.CONFIG_COMMITTED)
	public void commit() {

		this.invocations.forEach((joinPoint) -> {
			try {
				joinPoint.proceed();
				
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});
		this.invocations.clear();

		Document machineDoc = this.machineRegister.find(FluentFilter.where(Constants.GROUP_NAME)
				.eq(this.groupName)
				.and(FluentFilter.where(Constants.MACHINE_NAME).eq(this.machineName))).firstOrNull();

		if (machineDoc == null)
			throw new IllegalStateException("Machine configuration is missing");

		this.machineRegister.update(machineDoc);

		log.info("User configuration updated");
	}

}
