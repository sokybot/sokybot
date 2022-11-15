package org.sokybot.machine.service;

import java.util.Optional;

import org.sokybot.domain.AgentServer;

public interface IAgentServerList {


	// void add(AgentServer agent) ;
	 Optional<AgentServer> findByName(String name) ; 
	 Optional<AgentServer> findById(int id) ;
	 String[] listNames() ; 
	 int count() ; 
	 void discoverAgents() ; 
	 //void clear() ; 
	 
}
