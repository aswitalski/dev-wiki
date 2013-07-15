package pl.switalski.wiki.java.hibernate.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
		
		persistenceService.save(new PhoneNumber(11, 999));
	}
	
	/**
	 * Show telecommunication objects which have no numbers or all numbers are greater than 3000.
	 */
	@Test
	public void getTelecommunicationObjectsWithAllNumbersAbove3000() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "FROM TelecommunicationObject to WHERE 3000 < ALL(SELECT num.value FROM PhoneNumber num WHERE num.object = to)";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(2, result.size());
	}
	
	/**
	 * Show telecommunication objects which have any numbers greater than 3000.
	 */
	@Test
	public void getTelecommunicationObjectsWithAnyNumbersAbove3000() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "FROM TelecommunicationObject to WHERE 3000 < ANY(SELECT value FROM PhoneNumber num WHERE num.object = to)";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(2, result.size());
	}
	
	/**
	 * Show all telecommunication objects which have half of numbers greater than 4000.
	 */
	@Test
	public void getTelecommunicationObjectsWithHalfOfNumbersAbove4000() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name FROM TelecommunicationObject to WHERE (SELECT 2 * COUNT(num) FROM to.numbers num "
				+ "WHERE num.value > 4000) = (SELECT COUNT(num) FROM to.numbers num) ORDER by to.id";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(2, result.size());
		assertEquals("no numbers", result.get(0));
		assertEquals("some numbers above 3000", result.get(1));
	}
	
	/**
	 * Show telecommunication objects which have numbers not between 2000 and 6000 - using EXISTS.
	 */
	@Test
	public void getTelecommunicationObjectsWithAllNumbersNotBetween2000And6000() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to FROM TelecommunicationObject to "
				+ "WHERE EXISTS (SELECT value from PhoneNumber num WHERE num.object = to AND value NOT BETWEEN 2000 AND 6000)";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(2, result.size());
	}
	
	/**
	 * Show telecommunication objects which have any numbers less than 2000.
	 */
	@Test
	public void getTelecommunicationObjectsWithAnyNumbersBelow2000() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "FROM TelecommunicationObject to WHERE 2000 > SOME(SELECT value FROM PhoneNumber num WHERE num.object = to)";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(1, result.size());
	}
	
	/**
	 * Show telecommunication objects with greatest phone number, don't show objects with no numbers.
	 */
	@Test
	public void getTelecommunicationObjectsWithGreatestNumber() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name, num.value FROM TelecommunicationObject to, PhoneNumber num "
				+ "WHERE num.value = (SELECT MAX(maxNum.value) FROM PhoneNumber maxNum WHERE maxNum.object = to) ORDER by to.id";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(3, result.size());
		
		assertEquals(7001, get(result, 0, 1));
		assertEquals(5002, get(result, 1, 1));
		assertEquals(2003, get(result, 2, 1));
	}
	
	/**
	 * Show all telecommunication objects having all numbers ending with digit 3, skip objects with no numbers.
	 */
	@Test
	public void getTelecommunicationWithAllNumbersEndingWith3() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to FROM TelecommunicationObject to "
				+ "WHERE (SELECT COUNT(num) FROM to.numbers num) = (SELECT COUNT(num3) FROM to.numbers num3 WHERE MOD(num3.value, 10) = 3) "
				+ "AND (SELECT COUNT(num) FROM to.numbers num) > 0"; // to skip telecommunication objects with no number
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(1, result.size());
	}
	
	/**
	 * Show all telecommunication objects ordered by quantity of numbers attached and id.
	 */
	@Test
	public void getTelecommunicationOrderedByQuantityOfNumbersAttached() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name, COUNT(num), SUM(num.value) FROM TelecommunicationObject to LEFT OUTER JOIN to.numbers num "
				+ "GROUP BY to.name ORDER BY COUNT(num) DESC, SUM(num.value) ASC";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(4, result.size());
		assertEquals("some numbers above 3000", get(result, 0, 0));
		assertEquals(4l, get(result, 0, 1));
		assertEquals(4l, get(result, 1, 1));
		assertEquals(2l, get(result, 2, 1));
		assertEquals(0l, get(result, 3, 1));
	}
	
	/**
	 * Show all telecommunication objects having numbers - with EXISTS.
	 */
	@Test
	public void getTelecommunicationObjectsHavingNumbersWithExists() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name FROM TelecommunicationObject to WHERE EXISTS (SELECT num FROM PhoneNumber num where num.object = to)";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(3, result.size());
	}
	
	/**
	 * Show all telecommunication objects having 'some' in name - using LIKE.
	 */
	@Test
	public void getTelecommunicationWithSomeInName() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to FROM TelecommunicationObject to WHERE to.name LIKE '%some%'";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(1, result.size());
	}
	
	/**
	 * Show all telecommunication objects and quantity of attached numbers - with implicit INNER JOIN.
	 */
	@Test
	public void getTelecommunicationObjectsAndQuantityOfNumbersWithInnerJoin() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name, to.id, COUNT(num) FROM TelecommunicationObject to, PhoneNumber num WHERE num.object = to GROUP BY to.name, to.id ORDER BY to.id";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(3, result.size());
		assertEquals(4l, get(result, 0, 2));
		assertEquals(4l, get(result, 1, 2));
		assertEquals(2l, get(result, 2, 2));
	}
	
	/**
	 * Show all telecommunication objects and quantity of attached numbers - with LEFT OUTER JOIN.
	 */
	@Test
	public void getTelecommunicationObjectsAndQuantityOfNumbersWithLeftOuterJoin() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name, to.id, COUNT(num) FROM TelecommunicationObject to LEFT OUTER JOIN to.numbers num GROUP BY to.name, to.id ORDER BY to.id";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(4, result.size());
		assertEquals(0l, get(result, 0, 2));
	}
	
	/**
	 * Groups and counts numbers by telecommunication object with RIGHT OUTER JOIN. Shows unassigned numbers, because right join includes null
	 * objects. Would be the only way to do such query if PhoneNumber did not have a reference to its TelecommunicationObject.
	 */
	@Test
	public void groupAndCountNumbersWithRightOuterJoin() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT COUNT(num), to.name FROM TelecommunicationObject to RIGHT OUTER JOIN to.numbers num GROUP BY to.name ORDER BY COUNT(num) DESC";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(4, result.size());
		assertNull(get(result, 3, 1)); // last row defines one unassigned number
	}
	
	/**
	 * Show all telecommunication objects having at least 4 numbers.
	 */
	@Test
	public void getTelecommunicationObjectsHaving4Numbers() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT to.name, COUNT(num) FROM TelecommunicationObject to, PhoneNumber num WHERE num.object = to GROUP BY to.name HAVING COUNT(num) = 4";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(2, result.size());
	}
	
	/**
	 * Show difference between subsequent phone numbers.
	 */
	@Test
	public void getDifferenceBetweenNumbers() {
		
		// given
		insertTelecommunicationObjects();
		
		String query = "SELECT number.object.name, number.id, number.value, number.value - prevNumber.value from PhoneNumber number, PhoneNumber prevNumber "
				+ "WHERE prevNumber.id = (SELECT MAX(maxNum.id) from PhoneNumber maxNum WHERE maxNum.id < number.id AND maxNum.object = number.object)";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(7, result.size());
		for (int i = 0; i < result.size(); i++) {
			// all numbers differ by 1000
			assertEquals(1000, get(result, i, 3));
		}
	}
	
	/**
	 * Selects all objects without using SELECT keyword.
	 */
	@Test
	public void getSingleTelecommunicationObject() {
		
		// given
		TelecommunicationObject objectWithNoNumbers = new TelecommunicationObject(1, "no numbers");
		persistenceService.save(objectWithNoNumbers);
		
		String query = "FROM TelecommunicationObject to";
		
		// when
		List<?> result = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(1, result.size());
	}
	
	/**
	 * Returns element from query result.
	 * 
	 * @param result
	 *            List of returned object arrays
	 * @param index
	 *            Index
	 * @param column
	 *            Column
	 */
	public Object get(List<?> result, int index, int column) {
		return ((Object[]) result.get(index))[column];
	}

}
