package pl.switalski.wiki.java.core.concurrency;

import static org.junit.Assert.assertEquals;
import static pl.switalski.wiki.java.core.concurrency.utils.ThreadUtils.nap;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;

/**
 * <p>
 * Demonstrates how to synchronise efficiently a single Writer with many Readers. Writer starts writing letters 5 seconds ahead of readers and keeps
 * going as long as all readers are happy and receive new letters. When any Reader sends a notification (through
 * {@link java.util.concurrent.atomic.AtomicBoolean atomic flag} that nothing could be read what makes him unhappy - the Writer gives up.
 * </p>
 * 
 * <p>
 * Readers hold the {@link java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock read-lock} simultaneously, but the read operation is blocked for
 * the time of writing new letter by the Writer.
 * </p>
 * 
 * <p>
 * In order to force the Readers to wait for the output, blocking queues must be used, which are slower but can utilise
 * {@link java.util.concurrent.locks.Condition conditions} and their signals.
 * </p>
 * 
 * @author sensei
 */
public class ReadWriteLocks {
	
	/** Indicates if all readers are happy. */
	private static AtomicBoolean happy = new AtomicBoolean(true);

	/** Magical book, where letters disappear when read. */
	private class Book {
		
		private Queue<Character> letters = new LinkedList<>();
		
		private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);
		
		private Lock write = readWriteLock.writeLock();
		
		private Lock read = readWriteLock.readLock();
		
		public void write(Character ch) {
			write.lock();
			try {
				// exclusive write lock
				wPrint("Locked for write");
				letters.offer(ch);
				wPrint("Letter '" + ch + "' written");
			} finally {
				wPrint("Releasing lock for write");
				write.unlock();
			}
		}
		
		public Character read() {
			read.lock();
			try {
				// read lock can be held by many threads at the same time
				rPrint("Locked for read");
				Character ch = letters.poll();
				if (ch == null) {
					rPrint("NULL letter read");
					return null;
				} else {
					rPrint("Letter '" + ch + "' read");
					return ch;
				}
			} finally {
				rPrint("Releasing lock for read");
				read.unlock();
			}
		}
		
	}
	
	private final Book book = new Book();
	
	/**
	 * Writer thread writing letters to the book.
	 * 
	 * @author sensei
	 */
	class Writer extends Thread {
		
		private static final int MAX_LETTERS = 1000;

		private int lettersWritten = 0;

		private Random random = new Random();
		
		/**
		 * Generates a random lower-case letter in English alphabet.
		 */
		private Character getRandomLetter() {
			int range = 'z' - 'a' + 1;
			int index = random.nextInt(range);
			char ch = Character.toChars('a' + index)[0];
			return Character.valueOf(ch);
		}
		
		@Override
		public void run() {
			for (int i = 1; i <= MAX_LETTERS; i++) {
				if (happy.get() == false) {
					// if any reader got null value, writer gives up and stops writing
					break;
				}
				nap(10);
				Character randomLetter = getRandomLetter();
				print("==> Writer writes '" + randomLetter + "' (" + i + ")");
				book.write(randomLetter);
				lettersWritten = i;
			}
		}
		
		/**
		 * Returns the number of letters written.
		 * 
		 * @return Number of letters written
		 */
		public int getLettersWritten() {
			return lettersWritten;
		}
	}
	
	/**
	 * Reader thread, reading letters from the book.
	 * 
	 * @author sensei
	 */
	class Reader extends Thread {
		
		private final int number;
		
		private int count = 0;

		Reader(int number) {
			this.number = number;
		}
		
		public int getCount() {
			return count;
		}

		@Override
		public void run() {
			// does not use happy indicator, because it would not allow other threads to read all the remaining letters
			while (true) {
				Character letter = book.read();
				if (letter != null) {
					print("<== Reader " + number + " got: " + letter + " (total: " + ++count + ")");
					nap(10);
				} else {
					happy.set(false);
					print("NULL letter read, stopping Reader " + number + "...");
					// just exists the loop when there is no data
					break;
				}
			}
		}
	}

	@Test
	public void testOneProducerWithManyConsumers() throws Exception {
		
		Writer writer = new Writer();
		writer.start();
		
		TimeUnit.SECONDS.sleep(5);
		
		final Reader[] readers = new Reader[10];

		for (int i = 0; i < 10; i++) {
			Reader reader = new Reader(i);
			readers[i] = reader;
			reader.start();
		}
		
		// gets notified by readers getting null values
		while (happy.get()) {
			TimeUnit.MILLISECONDS.sleep(100);
		}

		print("================================");
		
		// give more than enough time for all threads to complete
		TimeUnit.MILLISECONDS.sleep(100);

		print("Writer has written " + writer.getLettersWritten() + " letters");

		int totalRead = 0;
		for (int i = 0; i < 10; i++) {
			int read = readers[i].getCount();
			totalRead += read;
			print("Reader " + i + " got " + read + " letters");
		}
		
		print("Readers have read " + totalRead + " letters");
		
		assertEquals(writer.getLettersWritten(), totalRead);
	}
	
	private static void print(String msg) {
		System.out.println(msg);
	}

	private static void rPrint(String msg) {
		System.out.println("- " + msg);
	}
	
	private static void wPrint(String msg) {
		System.out.println("+ " + msg);
	}
}
