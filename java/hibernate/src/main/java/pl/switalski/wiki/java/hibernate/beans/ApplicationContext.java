package pl.switalski.wiki.java.hibernate.beans;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContext extends ClassPathXmlApplicationContext {
	
	public static final String DEFAULT_CONTEXT_CONFIG_FILE = "/context.xml";
	
	public ApplicationContext() {
		super(DEFAULT_CONTEXT_CONFIG_FILE);
	}

	public PersistenceService getPersistenceService() {
		return getBean(PersistenceService.class);
	}

}
