package org.sokybot.app.plugin;

import org.sokybot.app.service.IPluginService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class OSGIServiceDeployer implements BeanPostProcessor {

	
	
	@Autowired
	IPluginService pluginService ;
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	
		if(bean.getClass().isAnnotationPresent(OSGIService.class)) { 
			System.out.println("Bean " + beanName  + " is osgi service ") ; 
		}
		return bean ;
	}
}
