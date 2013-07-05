package pl.switalski.wiki.java.core.concurrency;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LocksAndConditions {
	
	private static class Queue {
		
		private LinkedList<Double> elements = new LinkedList<>();
		
		final Lock lock = new ReentrantLock();
		
		final Condition notFull = lock.newCondition();
		
		final Condition notEmpty = lock.newCondition();
		
		public void put(Double value) {
			lock.lock();
			print("Locked for put");
			try {
				while (elements.size() == 1000) {
					print("Queue full, waiting");
					notFull.await(); // releases the lock and waits for a signal, then checks the size again
				}
				print("Queue not full, adding element");
				elements.add(value);
				print("Signalling not empty, queue size: " + elements.size());
				notEmpty.signal(); // signals that condition is met, but does not release the lock
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
		}
		
		public Object take() {
			lock.lock();
			print("Locked for take");
			try {
				while (elements.size() == 0) {
					print("Queue empty, waiting");
					notEmpty.await(); // releases the lock and waits for a signal
				}
				print("Queue not empty, taking element");
				Double value = elements.removeFirst();
				print("Signalling not full, queue size: " + elements.size());
				notFull.signal(); // signals that condition is met, but does not release the lock
				return value;
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
		}
	}
	
	public static void main(String... args) throws Exception {
		
		final Queue queue = new Queue();

		Thread producer = new Thread() {
			
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					nap(i < 500 ? 10 : 20);
					queue.put(Math.random());
				}
			}
		};
		
		Thread consumer = new Thread() {
			
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					System.out.println("Got: " + queue.take());
					nap(i < 500 ? 20 : 10);
				}
			}
		};
		
		consumer.start();
		producer.start();

		TimeUnit.SECONDS.sleep(10);
	}
	
	private static void nap(int milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static void print(String msg) {
		System.out.println(msg);
	}
}
