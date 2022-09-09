package org.sokybot.app.service;

import org.dizitart.no2.Nitrite;
import org.sokybot.domain.items.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;

public class GameStorageManager implements IGameStorageManager {

	
	@Autowired
	Nitrite db ; 
	
	
	
	@Override
	public void update(String gameDist) {
	  
		
	}

	@Override
	public void destroy(String gameDistribution) {
		// TODO Auto-generated method stub
		
	}

}
