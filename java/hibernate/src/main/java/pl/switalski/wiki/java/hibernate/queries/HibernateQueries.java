package pl.switalski.wiki.java.hibernate.queries;

import static org.junit.Assert.assertEquals;

import java.util.List;

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
	 * Show all telecommunication objects which have no numbers or all numbers are greater that 3000.
	 */
	@Test
	public void queryValidTelecommunicationObjects() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "FROM TelecommunicationObject to WHERE 3000 < ALL(SELECT value from PhoneNumber num where num.object = to)";
		
		// when
		List<?> objects = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(2, objects.size());
	}
	
	/**
	 * Show all telecommunication objects which have any numbers.
	 */
	@Test
	public void queryTelecommunicationObjectsWithAnyNumbersBelow2000() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "FROM TelecommunicationObject to WHERE 2000 > SOME(SELECT value from PhoneNumber num where num.object = to)";
		
		// when
		List<?> objects = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(1, objects.size());
	}
	
	/**
	 * Show all telecommunication objects with greatest phone number, don't show objects with no numbers.
	 */
	@Test
	public void queryTelecommunicationObjectsWithAllNumbersAbove3000() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name, num.value FROM TelecommunicationObject to, PhoneNumber num "
				+ "WHERE num.value = (SELECT MAX(num.value) from PhoneNumber num where num.object = to) ORDER by to.id";

		// when
		List<?> objects = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(3, objects.size());
		
		assertEquals(7001, ((Object[]) objects.get(0))[1]);
		assertEquals(5002, ((Object[]) objects.get(1))[1]);
		assertEquals(2003, ((Object[]) objects.get(2))[1]);
	}
	
	/**
	 * Show all telecommunication objects having all numbers ending with digit 3.
	 */
	@Test
	public void queryTelecommunicationWithNumbersEndingWith3() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to FROM TelecommunicationObject to "
				+ "WHERE (SELECT COUNT(num) FROM to.numbers num) = (SELECT COUNT(num3) FROM to.numbers num3 WHERE MOD(num3.value, 10) = 3) "
				+ "AND (SELECT COUNT(num) FROM to.numbers num) > 0";
		
		// when
		List<?> objects = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(1, objects.size());
	}
	
	/**
	 * Show all telecommunication objects and quantity of attached numbers - with inner join.
	 */
	@Test
	public void queryTelecommunicationAndQuantityOfNumbersWithInnerJoin() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name, COUNT(num) FROM TelecommunicationObject to, PhoneNumber num WHERE num.object = to GROUP BY to.name";
		
		// when
		List<?> objects = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(3, objects.size());
	}
	
	/**
	 * Show all telecommunication objects and quantity of attached numbers - with outer join.
	 */
	@Test
	public void queryTelecommunicationAndQuantityOfNumbersWithOuterJoin() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name, COUNT(num) FROM TelecommunicationObject to LEFT OUTER JOIN to.numbers num GROUP BY to.name";
		
		// when
		List<?> objects = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(4, objects.size());
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
	
	/**
	 * Inserts data re-used in several tests.
	 */
	private void insertTelecommunicationObjects() {

		TelecommunicationObject objectWithNoNumbers = new TelecommunicationObject(1, "no numbers");
		persistenceService.save(objectWithNoNumbers);
		
		TelecommunicationObject objectWithAllValidNumbers = new TelecommunicationObject(2, "all numbers above 3000");
		objectWithAllValidNumbers.addNumber(new PhoneNumber(1, 4001));
		objectWithAllValidNumbers.addNumber(new PhoneNumber(2, 5001));
		objectWithAllValidNumbers.addNumber(new PhoneNumber(3, 6001));
		objectWithAllValidNumbers.addNumber(new PhoneNumber(4, 7001));
		persistenceService.save(objectWithAllValidNumbers);
		
		TelecommunicationObject objectWithSomeValidNumbers = new TelecommunicationObject(3, "some numbers above 3000");
		objectWithSomeValidNumbers.addNumber(new PhoneNumber(5, 2002));
		objectWithSomeValidNumbers.addNumber(new PhoneNumber(6, 3002));
		objectWithSomeValidNumbers.addNumber(new PhoneNumber(7, 4002));
		objectWithSomeValidNumbers.addNumber(new PhoneNumber(8, 5002));
		persistenceService.save(objectWithSomeValidNumbers);
		
		TelecommunicationObject objectWithNoValidNumbers = new TelecommunicationObject(4, "no numbers above 3000");
		objectWithNoValidNumbers.addNumber(new PhoneNumber(9, 1003));
		objectWithNoValidNumbers.addNumber(new PhoneNumber(10, 2003));
		persistenceService.save(objectWithNoValidNumbers);
	}
}
