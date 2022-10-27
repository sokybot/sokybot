package org.sokybot.app.service;

import java.util.List;
import java.util.Optional;

public interface IPluginService {

	
	
	public <T> List<T> findService(Class<T> serviceType) ; 
}
