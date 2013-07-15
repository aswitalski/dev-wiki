package pl.switalski.wiki.demo;

import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.switalski.wiki.java.spring.scopes.Prototype;
import pl.switalski.wiki.java.spring.scopes.Singleton;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/context/scope.xml")
public class SpringScopesDemo {
	
	@Autowired
	private Singleton singleton;
	
	@Test
	public void testPrototypes() {
		
		Prototype p1 = singleton.getPrototype();
		Prototype p2 = singleton.getPrototype();
		
		assertNotSame(p1, p2);
	}

}
