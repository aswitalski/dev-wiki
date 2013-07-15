package pl.switalski.wiki.java.spring.scopes.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class PropagationService {
	
	@Autowired
	private ApplicationContext context;

	@Transactional(propagation = Propagation.REQUIRED)
	public void required() {
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void requiresNew() {
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void supports() {
	}
	
	@Transactional(propagation = Propagation.MANDATORY)
	public void mandatory() {
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void notSupported() {
	}
	
	@Transactional(propagation = Propagation.NEVER)
	public void never() {
	}
	
	@Transactional(propagation = Propagation.NESTED)
	public void nested() {
	}
	
	@Transactional(propagation = Propagation.MANDATORY)
	public void mandatoryCallingNeverDirectly() {
		never();
	}
	
	@Transactional(propagation = Propagation.MANDATORY)
	public void mandatoryCallingNeverViaProxy() {
		context.getBean(PropagationService.class).never();
	}

}
