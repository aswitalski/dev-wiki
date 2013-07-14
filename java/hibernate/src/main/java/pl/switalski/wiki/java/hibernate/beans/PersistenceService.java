package pl.switalski.wiki.java.hibernate.beans;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pl.switalski.wiki.java.hibernate.model.PhoneNumber;
import pl.switalski.wiki.java.hibernate.model.TelecommunicationObject;

/**
 * 
 * All methods may throw runtime exception, such as: "Exception in ... org.springframework.transaction.CannotCreateTransactionException: Could not
 * open JDBC Connection for transaction; nested exception is org.apache.commons.dbcp.SQLNestedException: Cannot get a connection, pool error Timeout
 * waiting for idle object".
 * 
 * 
 */
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class PersistenceService {
	
	@Autowired
	protected HibernateTemplate hibernateTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	/**
	 * Inserts telecommunication objects and numbers into tables. Valid number has value greater than 3000.
	 */
	public void insertData() {
		
		{
			TelecommunicationObject objectWithNoNumbers = new TelecommunicationObject(1, "no numbers");
			hibernateTemplate.save(objectWithNoNumbers);
		}
		
		{
			TelecommunicationObject objectWithAllValidNumbers = new TelecommunicationObject(2, "all valid numbers");
			objectWithAllValidNumbers.addNumber(new PhoneNumber(1, 4001));
			objectWithAllValidNumbers.addNumber(new PhoneNumber(2, 5001));
			objectWithAllValidNumbers.addNumber(new PhoneNumber(3, 6001));
			objectWithAllValidNumbers.addNumber(new PhoneNumber(4, 7001));
			hibernateTemplate.save(objectWithAllValidNumbers);
		}
		
		{
			TelecommunicationObject objectWithSomeValidNumbers = new TelecommunicationObject(3, "some valid numbers");
			objectWithSomeValidNumbers.addNumber(new PhoneNumber(5, 2002));
			objectWithSomeValidNumbers.addNumber(new PhoneNumber(6, 3002));
			objectWithSomeValidNumbers.addNumber(new PhoneNumber(7, 4002));
			objectWithSomeValidNumbers.addNumber(new PhoneNumber(8, 5002));
			hibernateTemplate.save(objectWithSomeValidNumbers);
		}
		
		{
			TelecommunicationObject objectWithNoValidNumbers = new TelecommunicationObject(4, "no valid numbers");
			objectWithNoValidNumbers.addNumber(new PhoneNumber(9, 1003));
			objectWithNoValidNumbers.addNumber(new PhoneNumber(10, 2003));
			hibernateTemplate.save(objectWithNoValidNumbers);
		}
		
	}

	public TelecommunicationObject getTelecommunicationObject(int id) {
		TelecommunicationObject object = (TelecommunicationObject) hibernateTemplate.get(TelecommunicationObject.class, id);
		return object;
	}

	public PhoneNumber getNumber(int id) {
		PhoneNumber number = (PhoneNumber) hibernateTemplate.get(PhoneNumber.class, id);
		return number;
	}
	
	public List<TelecommunicationObject> getTelecommunicationObjects(String query) {
		@SuppressWarnings("unchecked")
		List<TelecommunicationObject> objects = (List<TelecommunicationObject>) hibernateTemplate.find(query);
		return objects;
	}

}
