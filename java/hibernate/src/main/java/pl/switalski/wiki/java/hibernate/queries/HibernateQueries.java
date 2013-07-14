package pl.switalski.wiki.java.hibernate.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pl.switalski.wiki.java.hibernate.beans.ApplicationContext;
import pl.switalski.wiki.java.hibernate.beans.PersistenceService;
import pl.switalski.wiki.java.hibernate.model.PhoneNumber;
import pl.switalski.wiki.java.hibernate.model.TelecommunicationObject;

public class HibernateQueries {
	
	private ApplicationContext context;
	
	private PersistenceService persistenceService;

	@Before
	public void setUp() {
		this.context = new ApplicationContext();
		this.context.toString();
		this.persistenceService = context.getPersistenceService();
	}

	@Test
	public void queryTelecommunicationObject() {
		
		// given
		persistenceService.insertData();
		int objectId = 1;
		
		// when
		TelecommunicationObject object = persistenceService.getTelecommunicationObject(objectId);
		
		// then
		assertNotNull(object);
	}
	
	@Test
	public void queryNumber() {
		
		// given
		int numberId = 1;
		
		// when
		PhoneNumber number = persistenceService.getNumber(numberId);
		
		// then
		assertNotNull(number);
	}
	
	@Test
	public void queryValidTelecommunicationObjects() {
		
		// given
		String query = "FROM TelecommunicationObject to WHERE 10000 < ALL(SELECT value from PhoneNumber num where num.object = to)";
		
		// when
		List<?> objects = persistenceService.getTelecommunicationObjects(query);
		
		// then
		assertEquals(2, objects.size());
	}
}
