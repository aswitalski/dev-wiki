package pl.switalski.wiki.demo;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Date;
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
import pl.switalski.wiki.java.hibernate.model.Measurement;
import pl.switalski.wiki.java.hibernate.model.Observatory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/context/hibernate.xml")
@Transactional(propagation = Propagation.REQUIRED)
@TransactionConfiguration(defaultRollback = true)
public class AdvancedQueriesDemo {
	
	@Autowired
	private PersistenceService persistenceService;
	
	private Date getDate(int hoursBack) {
		return new Date();
	}
	
	private double getRandomValue() {
		double value = 18 + Math.random() * 5;
		BigDecimal bd = new BigDecimal(value).setScale(1, BigDecimal.ROUND_DOWN);
		return bd.doubleValue();
	}

	/**
	 * Inserts data re-used in several tests.
	 */
	private void insertMeasurements() {
		
		Observatory observatory = new Observatory(1, "Breslau");
		for (int i = 0; i <= 12 * 24; i++) {
			observatory.addMeasurement(new Measurement(i + 1, getDate(12 * 24 - i), getRandomValue()));
		}

		persistenceService.save(observatory);
	}
	
	/**
	 * Show telecommunication objects which have no numbers or all numbers are greater than 3000.
	 */
	@Test
	public void getDifferenceBetweenMeasurements() {
		
		// given
		insertMeasurements();
		
		String query = "SELECT current.value - previous.value FROM Measurement current, Measurement previous WHERE current.id = previous.id + 1)";
		
		// when
		List<?> result = persistenceService.getResult(query);
		
		// then
		assertEquals(2, result.size());
	}
	
}
