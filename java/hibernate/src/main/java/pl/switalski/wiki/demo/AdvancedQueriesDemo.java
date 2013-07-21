package pl.switalski.wiki.demo;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
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
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private PersistenceService persistenceService;
	
	/**
	 * Inserts data re-used in several tests.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void insertMeasurements() throws Exception {
		
		Observatory observatory = new Observatory(1, "Breslau");
		
		Pattern pattern = Pattern.compile("(\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2}) \\-\\> (.+)");
		
		List<String> lines;
		lines = IOUtils.readLines(AdvancedQueriesDemo.class.getResourceAsStream("/data/measurements.txt"));
		int i = 0;
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				Date date = formatter.parse(matcher.group(1));
				double value = Double.parseDouble(matcher.group(2));
				observatory.addMeasurement(new Measurement(++i, date, value));
			}
		}

		persistenceService.save(observatory);
	}
	
	@Test
	public void getDifferenceBetweenMeasurements() throws Exception {
		
		// given
		insertMeasurements();
		
		String query = "SELECT current.value - previous.value FROM Measurement current, Measurement previous WHERE current.id = previous.id + 1) ORDER BY current.date";
		
		// when
		List<?> result = persistenceService.getResult(query);
		
		// then
		assertEquals(40, result.size());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	/**
	 * Calculates hourly differences between temperature measurements using indexes.
	 * 
	 * @throws Exception If any exception occurs
	 */
	public void getDifferenceBetweenHourlyMeasurementsUsingModulo() throws Exception {
		
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
		assertEquals(41, measurements.size());
		assertEquals(2, hourlyDifferences.size());
		assertEquals(-3.9, hourlyDifferences.get(0));
		assertEquals(-1.8, hourlyDifferences.get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	/**
	 * Calculates hourly differences between temperature measurements using first measurements in given hour.
	 * 
	 * @throws Exception If any exception occurs
	 */
	public void getDifferenceBetweenHourlyMeasurementsUsingMinuteExtraction() throws Exception {
		
		// given
		insertMeasurements();

		String query = "SELECT previous.date, current.value - previous.value from Measurement current, Measurement previous "
				+ "WHERE MINUTE(current.date) BETWEEN 0 AND 4 AND HOUR(current.date) != HOUR(previous.date) AND " // skips redundant measurements
				+ "previous.id = (SELECT MAX(maxM.id) from Measurement maxM WHERE maxM.id < current.id AND MINUTE(maxM.date) BETWEEN 0 AND 4) "
				+ "ORDER BY current.date ASC";
		
		// when
		List<Object[]> result = (List<Object[]>) persistenceService.getResult(query);
		
		// then
		assertEquals(3, result.size());
		assertEquals(-4.0, result.get(0)[1]);
		assertEquals(-3.8, result.get(1)[1]);
		assertEquals(-2.0, result.get(2)[1]);
	}

}
