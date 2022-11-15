package org.sokybot.machine;

public enum UserAction  implements IMachineEvent{

	CONFIG_MODIFIED ,
	CONFIG_COMMIT , 
	DISCONNECT , 
	CONNECT , 
	KILL_CLIENT  ,
	CLIENT_ATTACHED,
	
}
