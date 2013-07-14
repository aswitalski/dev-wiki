package pl.switalski.wiki.java.hibernate.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pl.switalski.wiki.java.hibernate.beans.PersistenceService;
import pl.switalski.wiki.java.hibernate.model.PhoneNumber;
import pl.switalski.wiki.java.hibernate.model.TelecommunicationObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/context.xml")
@Transactional(propagation = Propagation.REQUIRED)
@TransactionConfiguration(defaultRollback = true)
public class HibernateQueries {
	
	@Autowired
	private PersistenceService persistenceService;

	/**
	 * Finds all telecommunication objects which have no numbers or all numbers are greater that 3000.
	 */
	@Test
	public void queryValidTelecommunicationObjects() {
		
		// given
		TelecommunicationObject objectWithNoNumbers = new TelecommunicationObject(1, "no numbers");
		persistenceService.save(objectWithNoNumbers);
		
		TelecommunicationObject objectWithAllValidNumbers = new TelecommunicationObject(2, "all valid numbers");
		objectWithAllValidNumbers.addNumber(new PhoneNumber(1, 4001));
		objectWithAllValidNumbers.addNumber(new PhoneNumber(2, 5001));
		objectWithAllValidNumbers.addNumber(new PhoneNumber(3, 6001));
		objectWithAllValidNumbers.addNumber(new PhoneNumber(4, 7001));
		persistenceService.save(objectWithAllValidNumbers);
		
		TelecommunicationObject objectWithSomeValidNumbers = new TelecommunicationObject(3, "some valid numbers");
		objectWithSomeValidNumbers.addNumber(new PhoneNumber(5, 2002));
		objectWithSomeValidNumbers.addNumber(new PhoneNumber(6, 3002));
		objectWithSomeValidNumbers.addNumber(new PhoneNumber(7, 4002));
		objectWithSomeValidNumbers.addNumber(new PhoneNumber(8, 5002));
		persistenceService.save(objectWithSomeValidNumbers);
		
		TelecommunicationObject objectWithNoValidNumbers = new TelecommunicationObject(4, "no valid numbers");
		objectWithNoValidNumbers.addNumber(new PhoneNumber(9, 1003));
		objectWithNoValidNumbers.addNumber(new PhoneNumber(10, 2003));
		persistenceService.save(objectWithNoValidNumbers);


		String query = "FROM TelecommunicationObject to WHERE 3000 < ALL(SELECT value from PhoneNumber num where num.object = to)";
		
		// when
		List<?> objects = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(2, objects.size());
	}
	
	@Test
	public void querySingleTelecommunicationObject() {
		
		// given
		TelecommunicationObject objectWithNoNumbers = new TelecommunicationObject(1, "no numbers");
		persistenceService.save(objectWithNoNumbers);
		
		String query = "FROM TelecommunicationObject to";
		
		// when
		List<?> objects = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(1, objects.size());
	}
	
	@Test
	@Ignore
	public void queryTelecommunicationObject() {
		
		// given
		int objectId = 1;
		
		// when
		TelecommunicationObject object = persistenceService.getTelecommunicationObject(objectId);
		
		// then
		assertNotNull(object);
	}
	
	@Test
	@Ignore
	public void queryNumber() {
		
		// given
		int numberId = 1;
		
		// when
		PhoneNumber number = persistenceService.getNumber(numberId);
		
		// then
		assertNotNull(number);
	}
}
