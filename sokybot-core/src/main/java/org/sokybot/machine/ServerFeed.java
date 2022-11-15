package org.sokybot.machine;

public enum ServerFeed implements IMachineEvent {

	SETUP ,
	CHALLENGE , 
	INCOMPATIBLE , 
	COMPATIBLE , 
    GATEWAY_CONNECTED ,
	AGENT_CONNECTED , 
	AGENT_lISTED , 
	LOGIN_SUCCESS , 
	AUTHENTICATED , 
	LISTED , 
	JOINED 
}
