package pl.switalski.wiki.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pl.switalski.wiki.java.hibernate.beans.PropagationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/context/hibernate.xml")
@Transactional(propagation = Propagation.REQUIRED)
public class TransactionPropagationDemo {
	
	@Autowired
	private PropagationService propagationService;
	
	@Test
	public void testMandatory() {
		propagationService.mandatory();
	}
	
	@Test
	public void testMandatoryCallingNeverDirectly() {
		propagationService.mandatoryCallingNeverDirectly();
	}
	
	@Test(expected = IllegalTransactionStateException.class)
	public void testNever() {
		propagationService.never();
	}
}
