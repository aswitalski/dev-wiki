package pl.switalski.wiki.java.core.concurrency;

import static pl.switalski.wiki.java.core.concurrency.utils.ThreadUtils.nap;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ThreadLocalRace {
	
	private class NumberAssigner extends ThreadLocal<Integer> {
		
		private final AtomicInteger number = new AtomicInteger(0);
		
		protected Integer initialValue() {
			return number.incrementAndGet();
		}
	}

	/**
	 * Stores numbers assigned to racers.
	 */
	private final ThreadLocal<Integer> numberHolder = new NumberAssigner();
	
	/**
	 * Stores final positions of racers.
	 */
	private final ThreadLocal<Integer> positionHolder = new NumberAssigner();

	/**
	 * Racer thread.
	 */
	private class Racer extends Thread {
		
		@Override
		public void run() {

			// assigning value to the thread
			System.out.println("Racer number " + numberHolder.get() + " starts the race!");

			// race time
			nap(500 + new Random().nextInt(100));
			
			// retrieving number that was previously assigned and getting position
			System.out.println("Racer number " + numberHolder.get() + " finished " + positionHolder.get());
		}
	}

	@Test
	public void startRace() throws Exception {
		for (int i = 0; i < 8; i++) {
			new Racer().start();
		}
		
		TimeUnit.SECONDS.sleep(1);
	}
}
