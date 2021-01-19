/**
 * Filename:   TestHashTableDeb.java
 * Project:    p3
 * Authors:    Debra Deppeler (deppeler@cs.wisc.edu)
 * 
 * Semester:   Fall 2018
 * Course:     CS400
 * 
 * Due Date:   before 10pm on 10/29
 * Version:    1.0
 * 
 * Credits:    None so far
 * 
 * Bugs:       Tests 1 and 2 have an index out of bounds exception that I could not fix
 */

/*
 * Name: Zachary Einspanier
 * Email: zeinspanier@wisc.edu
 * Lecture: Lecture 2
 * Class Description: This class is tests BookHashTable class
 */

import org.junit.After;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class is tests BookHashTable class
 */
public class BookHashTableTest {

	// Default name of books data file
	public static final String BOOKS = "books.csv";

	// Empty hash tables that can be used by tests
	static BookHashTable bookObject;
	static ArrayList<Book> bookTable;

	static final int INIT_CAPACITY = 2;
	static final double LOAD_FACTOR_THRESHOLD = 0.49;

	static Random RNG = new Random(0); // seeded to make results repeatable
										// (deterministic)

	/** Create a large array of keys and matching values for use in any test */
	@BeforeAll
	public static void beforeClass() throws Exception {
		bookTable = BookParser.parse(BOOKS);
	}

	/** Initialize empty hash table to be used in each test */
	@BeforeEach
	public void setUp() throws Exception {
		// TODO: change HashTable for final solution
		bookObject = new BookHashTable(INIT_CAPACITY, LOAD_FACTOR_THRESHOLD);
	}

	/** Not much to do, just make sure that variables are reset */
	@AfterEach
	public void tearDown() throws Exception {
		bookObject = null;
	}

	private void insertMany(ArrayList<Book> bookTable) throws IllegalNullKeyException, DuplicateKeyException {
		for (int i = 0; i < bookTable.size(); i++) {
			bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
		}
	}

	/**
	 * IMPLEMENTED AS EXAMPLE FOR YOU Tests that a HashTable is empty upon
	 * initialization
	 */
	@Test
	public void test000_collision_scheme() {
		if (bookObject == null)
			fail("Gg");
		int scheme = bookObject.getCollisionResolutionScheme();
		if (scheme < 1 || scheme > 9)
			fail("collision resolution must be indicated with 1-9");
	}

	/**
	 * IMPLEMENTED AS EXAMPLE FOR YOU Tests that a HashTable is empty upon
	 * initialization
	 */
	@Test
	public void test000_IsEmpty() {
		// "size with 0 entries:"
		assertEquals(0, bookObject.numKeys());
	}

	/**
	 * IMPLEMENTED AS EXAMPLE FOR YOU Tests that a HashTable is not empty after
	 * adding one (key,book) pair
	 * 
	 * @throws DuplicateKeyException
	 * @throws IllegalNullKeyException
	 */
	@Test
	public void test001_IsNotEmpty() throws IllegalNullKeyException, DuplicateKeyException {
		bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
		String expected = "" + 1;
		// "size with one entry:"
		assertEquals(expected, "" + bookObject.numKeys());
	}

	/**
	 * IMPLEMENTED AS EXAMPLE FOR YOU Test if the hash table will be resized
	 * after adding two (key,book) pairs given the load factor is 0.49 and
	 * initial capacity to be 2.
	 */

	@Test
	public void test002_Resize() throws IllegalNullKeyException, DuplicateKeyException {
		bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
		int cap1 = bookObject.getCapacity();
		bookObject.insert(bookTable.get(1).getKey(), bookTable.get(1));
		int cap2 = bookObject.getCapacity();

		// "size with one entry:"
		assertTrue(cap2 > cap1 & cap1 == 2);
	}

	/**
	 * This method tests BookHashTable insert
	 * 
	 * @throws IllegalNullKeyException
	 * @throws DuplicateKeyException
	 */
	@Test
	public void test003_Insert() throws IllegalNullKeyException, DuplicateKeyException {
		Book testBook = new Book(null, null, null, null, null, null, null, null);

		bookObject.insert("Book1", testBook);
		bookObject.insert("Book2", testBook);
		// After inserting 2 books, the number of keys should be 2
		// the capacity of the table should be 5
		if (bookObject.numKeys() != 2 || bookObject.getCapacity() != 5) {
			fail("Book Table should have a capacity of 5 and 2 books in it");
		}
		
		bookObject.insert("Book3", testBook);
		bookObject.insert("Book4", testBook);
		bookObject.insert("Book5", testBook);
		bookObject.insert("Book6", testBook);
		// After inserting 6 books, the number of keys should be 6
		// the capacity of the table should be 11
		if (bookObject.numKeys() != 6 || bookObject.getCapacity() != 11) {
			fail("Book Table capacity should be 11 and 6 books in it");
		}
	}

	/**
	 * This makes sure that the table does not except duplicate keys
	 */
	@Test
	public void test004_DupKey() {
		Book testBook = new Book(null, null, null, null, null, null, null, null);

		try {
			// Should throw a duplicate key exception
			bookObject.insert("Book1", testBook);
			bookObject.insert("Book1", testBook);
			fail("Duplicate key excpetion should be thrown");
		} catch (DuplicateKeyException e) {
		} catch (IllegalNullKeyException e) {
			fail("Null key Excpetion should not be thrown");
		}

		try {
			bookObject.insert("Book2", testBook);
			bookObject.insert("Book3", testBook);
			bookObject.insert("Book4", testBook);
			bookObject.insert("Book5", testBook);
			// Should throw a duplicate key exception
			bookObject.insert("Book3", testBook);
			fail("Duplicate key excpetion should be thrown");
		} catch (DuplicateKeyException e) {
		} catch (IllegalNullKeyException e) {
			fail("Null key Excpetion should not be thrown");
		}
	}

	/**
	 * This method adds a number of books, removes them, then adds some back
	 */
	@Test
	public void test005_Insert_Remove() {
		Book testBook = new Book(null, null, null, null, null, null, null, null);

		try {
			// No exceptions should be thrown
			bookObject.insert("Book1", testBook);
			bookObject.insert("Book2", testBook);
			bookObject.insert("Book3", testBook);
			bookObject.insert("Book4", testBook);
			bookObject.remove("Book3");
			bookObject.insert("Book5", testBook);
			bookObject.insert("Book6", testBook);
			bookObject.insert("Book3", testBook);
			bookObject.remove("Book2");
			bookObject.remove("Book4");
			bookObject.insert("Book2", testBook);
		} catch (IllegalNullKeyException e) {
			fail("Null key Excpetion should not be thrown");
		} catch (DuplicateKeyException e) {
			fail("Duplicate key Excpetion should not be thrown");
		}
		try {
			// Remove key that does not exist
			bookObject.remove("hello");
		} catch (IllegalNullKeyException e) {
			fail("Null key Excpetion should not be thrown");
		}
		// Capacity should be 11 and number of keys should be 5
		if (bookObject.getCapacity() != 11 || bookObject.numKeys() != 5) {
			fail("Table should have a capacity of 11 with 4 books in it");
		}
	}

	/**
	 * This method tests the get method in BookHashTable
	 */
	@Test
	public void test006_get() {
		Book testBook = new Book("book1", null, null, null, null, null, null, null);
		Book testBook2 = new Book("book2", null, null, null, null, null, null, null);
		Book testBook3 = new Book("book3", null, null, null, null, null, null, null);
		Book testBook4 = new Book("book4", null, null, null, null, null, null, null);
		Book testBook5 = new Book("book5", null, null, null, null, null, null, null);
		Book testBook6 = new Book("book6", null, null, null, null, null, null, null);

		try {
			// Add books
			bookObject.insert("Book1", testBook);
			bookObject.insert("Book2", testBook2);
			bookObject.insert("Book3", testBook3);
			bookObject.insert("Book4", testBook4);
			bookObject.insert("Book5", testBook5);
			bookObject.insert("Book6", testBook6);
			bookObject.remove("Book4");
		} catch (IllegalNullKeyException e) {
			fail("Null key Excpetion should not be thrown");
		} catch (DuplicateKeyException e) {
			fail("Duplicate key Excpetion should not be thrown");
		}

		try {
			// All gets should return the correct corresponding book
			if (!bookObject.get("Book1").equals(testBook)) {
				fail("Expected to get Book1 but " + bookObject.get("Book1") +"was gotten instead");
			}
			if (!bookObject.get("Book2").equals(testBook2)) {
				fail("Expected to get Book2 but " + bookObject.get("Book2") +"was gotten instead");

			}
			if (!bookObject.get("Book3").equals(testBook3)) {
				fail("Expected to get Book3 but " + bookObject.get("Book3") +"was gotten instead");

			}
			if (!bookObject.get("Book5").equals(testBook5)) {
				fail("Expected to get Book5 but " + bookObject.get("Book5") +"was gotten instead");

			}
			if (!bookObject.get("Book6").equals(testBook6)) {
				fail("Expected to get Book6 but " + bookObject.get("Book6") +"was gotten instead");

			}
		} catch (IllegalNullKeyException e) {
			fail("Null key Excpetion should not be thrown");
		} catch (KeyNotFoundException e) {
			fail("Key not found excpetion should not be thrown");
		}
		
		try {
			// Get on a non-existent book
			bookObject.get("Book4");
		} catch (IllegalNullKeyException e) {
			fail("Null key Excpetion should not be thrown");
		} catch (KeyNotFoundException e) {
		}

	}
	
	/**
	 * This method tests the method BookHashTable remove
	 */
	@Test
	public void test007_remove() {
		Book testBook = new Book("book1", null, null, null, null, null, null, null);
		
		try {
			// Adds a number of books, then removes all but one
			bookObject.insert("Book1", testBook);
			bookObject.insert("Book2", testBook);
			bookObject.insert("Book3", testBook);
			bookObject.insert("Book4", testBook);
			bookObject.insert("Book5", testBook);
			bookObject.insert("Book6", testBook);
			bookObject.insert("Book7", testBook);
			bookObject.insert("Book8", testBook);
			bookObject.remove("Book3");
			bookObject.remove("Book5");
			bookObject.remove("Book1");
			bookObject.remove("Book7");
			bookObject.remove("Book6");
			bookObject.remove("Book4");
			bookObject.remove("Book2");
			
			if(bookObject.numKeys() != 1) {
				fail("Number of keys in this table should be 1, instead it is: " + bookObject.numKeys());
			}
			// Return false for non-existent book
			if(bookObject.remove("Book"))
				fail("removing a nonexistant book should return false");
			
		} catch (IllegalNullKeyException e) {
			fail("Null key Excpetion should not be thrown");
		} catch (DuplicateKeyException e) {
			fail("Duplicate key Excpetion should not be thrown");
		}
	}
}