package pl.switalski.wiki.java.core.concurrency.utils;

import java.util.concurrent.TimeUnit;

public class ThreadUtils {
	
	/**
	 * Sleeps for given time.
	 * 
	 * @param milliseconds
	 *            Time in milliseconds
	 */
	public static void nap(int milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
}
