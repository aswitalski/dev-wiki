package pl.switalski.wiki.java.core.structures;

import static java.lang.System.out;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class MeasureListsPerformance {
	
	@Test
	public void testAddOperation() {
		
		long timeTakenForLinkedList = benchmarkAddFor(new LinkedList<Integer>());
		long timeTakenForArrayList = benchmarkAddFor(new ArrayList<Integer>());
		
		/* ArrayList is slightly faster */
		assertTrue(timeTakenForArrayList < timeTakenForLinkedList);
	}
	
	/**
	 * Calculates time taken to add one million items to the specified list.
	 * 
	 * @param list
	 *            List to be filled
	 * @return
	 */
	private long benchmarkAddFor(List<Integer> list) {
		Date before = new Date();
		addOneMillionItems(list);
		Date after = new Date();
		return timeTaken(list, before, after);
	}
	
	/**
	 * Adds one million elements to specified list.
	 * 
	 * @param list
	 *            List to be filled
	 */
	private void addOneMillionItems(List<Integer> list) {
		for (int i = 0; i < 1_000_000; i++) {
			list.add(i);
		}
	}
	
	@Test
	public void testRemoveAtTheBeginningOperation() {
		
		long timeTakenForLinkedList = benchmarkRemoveAtTheBeginningFor(new LinkedList<Integer>());
		long timeTakenForArrayList = benchmarkRemoveAtTheBeginningFor(new ArrayList<Integer>());
		
		/* LinkedList is incredibly faster for removing elements at the beginning */
		assertTrue(timeTakenForArrayList > timeTakenForLinkedList);
	}
	
	/**
	 * Calculates time taken to remove 10,000 items at index 0 from given list.
	 * 
	 * @param list
	 *            List to be amended
	 * 
	 * @return Time taken in milliseconds
	 */
	private long benchmarkRemoveAtTheBeginningFor(List<Integer> list) {
		addOneMillionItems(list);
		Date before = new Date();
		removeTenThousandFirstItems(list);
		Date after = new Date();
		return timeTaken(list, before, after);
	}
	
	/**
	 * Removes 10,000 items at index 0 from given list.
	 * 
	 * @param list
	 *            List
	 */
	private void removeTenThousandFirstItems(List<Integer> list) {
		for (int i = 0; i < 10_000; i++) {
			list.remove(0);
		}
	}
	
	/**
	 * Calculates and prints time taken for given implementation of a {@link java.util.List List}.
	 * 
	 * @param list
	 *            List
	 * @param before
	 *            Date before
	 * @param after
	 *            Date after
	 * 
	 * @return Time taken in milliseconds
	 */
	private long timeTaken(List<?> list, Date before, Date after) {
		long timeTaken = after.getTime() - before.getTime();
		out.println("Time taken for " + list.getClass().getSimpleName() + ": " + timeTaken + " ms");
		return timeTaken;
	}
}
