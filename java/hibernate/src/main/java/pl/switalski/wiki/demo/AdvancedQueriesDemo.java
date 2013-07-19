package pl.switalski.wiki.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
	
	private Date getDate(int periodsBack) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, 5 * periodsBack);
		calendar.set(Calendar.SECOND, new Random().nextInt(60));
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
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
		
		// FIXME: load data from file

		Observatory observatory = new Observatory(1, "Breslau");
		for (int i = 0; i <= 12 * 24; i++) {
			// FIXME: add invalid measurements
			observatory.addMeasurement(new Measurement(i + 1, getDate(12 * 24 - i), getRandomValue()));
		}

		persistenceService.save(observatory);
	}
	
	@Test
	public void getDifferenceBetweenMeasurements() {
		
		// given
		insertMeasurements();
		
		String query = "SELECT current.value - previous.value FROM Measurement current, Measurement previous WHERE current.id = previous.id + 1) ORDER BY current.date";
		
		// when
		List<?> result = persistenceService.getResult(query);
		
		// then
		assertEquals(288, result.size());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getDifferenceBetweenHourlyMeasurementsUsingModulo() {
		
		// given
		insertMeasurements();
		
		String allMeasurementsQuery = "SELECT m FROM Measurement m ORDER BY m.id";
		
		String hourlyDifferencesQuery = "SELECT current.value - previous.value from Measurement current, Measurement previous "
				+ "WHERE MOD(current.id, 12) = 0 AND previous.id = (SELECT MAX(maxM.id) from Measurement maxM WHERE maxM.id < current.id AND MOD(maxM.id, 12) = 0) "
				+ "ORDER BY current.id ASC";
		
		// when
		List<Measurement> measurements = (List<Measurement>) persistenceService.getResult(allMeasurementsQuery);
		List<?> hourlyDifferences = persistenceService.getResult(hourlyDifferencesQuery);
		
		// then
		assertEquals(289, measurements.size());
		
		for (int i = 1; i < measurements.size() / 12; i++) {
			int current = i * 12 + 11;
			int previous = i * 12 - 1;
			BigDecimal difference = normalize(measurements.get(current).getValue()).subtract(normalize(measurements.get(previous).getValue()));
			System.out.println("Difference between measurement " + current + " and measurement " + previous + " is " + difference);
			BigDecimal dbDiff = normalize((Double) hourlyDifferences.get(i - 1));
			System.out.println("-> from DB: " + dbDiff);
			assertEquals(difference, dbDiff);
		}
	}

	@Test
	/**
	 * Shows date of current temperature measurement which if the first one after full hour and a temperature difference when compared to the measurement an hour before.
	 */
	public void getDifferenceBetweenHourlyMeasurementsUsingMinuteExtraction() {
		
		// given
		insertMeasurements();
		
		String query = "SELECT current.date, current.value - previous.value from Measurement current, Measurement previous "
				+ "WHERE MINUTE(current.date) BETWEEN 0 AND 4 AND HOUR(current.date) != HOUR(previous.date) AND " // skips redundant measurements
				+ "previous.id = (SELECT MAX(maxM.id) from Measurement maxM WHERE maxM.id < current.id AND MINUTE(maxM.date) BETWEEN 0 AND 4) "
				+ "ORDER BY current.id ASC";
		
		// when
		List<?> result = persistenceService.getResult(query);
		
		// then
		assertTrue(result.size() >= 23);
		assertTrue(result.size() <= 24);
		
		// TODO: more assertions
	}

	private BigDecimal normalize(double value) {
		long intValue = Math.round(value * 10);
		BigDecimal bdValue = new BigDecimal(intValue).divide(new BigDecimal(10)).setScale(1);
		return bdValue;
	}
}
