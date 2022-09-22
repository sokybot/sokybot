package org.sokybot.machine;

import javax.swing.ImageIcon;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


@Component
public class BotPageInstaller implements BeanPostProcessor  {

	
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	
		if(bean instanceof IBotPage) { 
			IBotPage page = (IBotPage) bean ; 
			String pageName  = page.getPageName() ; 
			ImageIcon pageIcon = page.getPageIcon() ; 
			
		}
		
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	
	}
}
