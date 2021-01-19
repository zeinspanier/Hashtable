import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/*
 * Name: Zachary Einspanier
 * Email: zeinspanier@wisc.edu
 * Lecture: Lecture 2
 * Class Description: This class is the implementation of a hash table
 */

/**
 * HashTable implementation that uses:
 * 
 * Collision Resolution Scheme: Open addressing using linear probing Probing
 * Sequence: When there is a collision, find next open position Hashing
 * Algorithm: Takes the index of a string character and raises 31 to that power.
 * Then multiplies that value by the character at that index. Adds that to
 * current hashCode. Takes hashCode and % it by the capacity
 * 
 * @param <K> unique comparable identifier for each <K,V> pair, may not be null
 * @param <V> associated value with a key, value may be null
 */
public class BookHashTable implements HashTableADT<String, Book> {

	/**
	 * This is an inner class that combines a key and a value together
	 * 
	 * @author Zachary Einspanier
	 */
	private class HashItem {
		private String key;
		private Book value;

		/**
		 * Constructor that sets values for key and value
		 * 
		 * @param key - a book key
		 * @param value - a book
		 */
		private HashItem(String key, Book value) {
			this.key = key;
			this.value = value;
		}
	}

	/** The initial capacity that is used if none is specifed user */
	static final int DEFAULT_CAPACITY = 101;

	/** The load factor that is used if none is specified by user */
	static final double DEFAULT_LOAD_FACTOR_THRESHOLD = 0.75;

	private HashItem[] bookTable; // The hashTable that holds books
	private int capacity; // The max amount of objects the table can hold
	private int numKeys; // The number of keys in the table
	private double loadFactor; // The number of keys divided by the capacity
	private double loadFactorThreshold; // The number that causes the table to
										// be resized

	/**
	 * REQUIRED default no-arg constructor Uses default capacity and sets load
	 * factor threshold for the newly created hash table.
	 */
	public BookHashTable() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR_THRESHOLD);
	}

	/**
	 * Creates an empty hash table with the specified capacity and load factor.
	 * 
	 * @param initialCapacity number of elements table should hold at start.
	 * @param loadFactorThreshold the ratio of items/capacity that causes table
	 * to resize and rehash
	 */
	public BookHashTable(int initialCapacity, double loadFactorThreshold) {
		this.capacity = initialCapacity;
		this.loadFactorThreshold = loadFactorThreshold;
		this.bookTable = new HashItem[capacity];
		this.numKeys = 0;
		this.loadFactor = numKeys / capacity;
	}

	/**
	 * If there is a load factor, return it otherwise return default load factor
	 * threshold
	 */
	@Override
	public double getLoadFactorThreshold() {
		if (loadFactorThreshold == 0) {
			return DEFAULT_LOAD_FACTOR_THRESHOLD;
		} else {
			return loadFactorThreshold;
		}
	}

	/**
	 * If there is a capacity, return it otherwise return default capacity
	 */
	@Override
	public int getCapacity() {
		if (capacity == 0) {
			return DEFAULT_CAPACITY;
		} else {
			return capacity;
		}
	}

	/**
	 * Find the collision resolution strategy return the corresponding int
	 */
	@Override
	public int getCollisionResolutionScheme() {
		return 1; // 1 OPEN ADDRESSING: linear probe
	}

	/**
	 * Add the key,value pair to the data structure and increase the number of
	 * keys. If key is null, throw IllegalNullKeyException; If key is already in
	 * data structure, throw DuplicateKeyException();
	 */
	@Override
	public void insert(String key, Book value) throws IllegalNullKeyException, DuplicateKeyException {
		// Key should not be null
		if (key == null)
			throw new IllegalNullKeyException();

		try {
			// Gets Duplicate, if there is one
			this.get(key);
			throw new DuplicateKeyException();
		} catch (KeyNotFoundException e) {
		}

		int hashCode;
		HashItem item = new HashItem(key, value);

		// Gets hash code
		hashCode = hashFunction(key);

		// If the index has a value, use open addressing to find
		// place to add
		if (bookTable[hashCode] != null) {
			hashCode = openAddressInst(hashCode, bookTable);
		}

		bookTable[hashCode] = item;
		++numKeys;

		// After adding, check if table needs to be resized
		loadFactor = numKeys / capacity;
		if (loadFactor >= loadFactorThreshold) {
			resize();
		}
	}

	/**
	 * If key is found, remove the key,value pair from the data structure
	 * decrease number of keys Return true If key is null, throw
	 * IllegalNullKeyException If key is not found, return false
	 */
	@Override
	public boolean remove(String key) throws IllegalNullKeyException {
		// Key should not be null
		if (key == null)
			throw new IllegalNullKeyException();

		// Finds the hashCode for the given key
		int hashCode = hashFunction(key);
		// Keeps track of first value
		int start = hashCode;

		// The object at the hashCode
		HashItem item = bookTable[hashCode];

		// If the keys match, then remove book
		if (item != null && item.key.equals(key)) {
			bookTable[hashCode] = null;
			--numKeys;
			return true;
		} else {
			// Finds next non null value
			while (item == null) {
				// wrap around if at end of table
				if (hashCode == capacity - 1) {
					hashCode = 0;
				}
				// Otherwise go to next value
				else {
					item = bookTable[hashCode + 1];
				}
				// If correct key is found, end loop
				if (item != null && item.key.equals(key)) {
					break;
				}
				// If every index is checked, value is not in table
				if (hashCode == start) {
					return false;
				}
			}

			bookTable[hashCode] = null;
			--numKeys;
			return true;
		}
	}

	/**
	 * Returns the value associated with the specified key Does not remove key
	 * or decrease number of keys If key is null, throw IllegalNullKeyException
	 * If key is not found, throw KeyNotFoundException()
	 */
	@Override
	public Book get(String key) throws IllegalNullKeyException, KeyNotFoundException {
		// Key should not be null
		if (key == null) {
			throw new IllegalNullKeyException();
		}
		// finds hashCode for given key
		int hashCode = hashFunction(key);
		// Keeps track of start index
		int start = hashCode;

		HashItem temp = bookTable[hashCode];

		do {
			// Return the value if keys match
			if (temp != null && temp.key.equals(key)) {
				return temp.value;
			}
			// wrap around if at end of table
			if (hashCode == capacity - 1) {
				hashCode = 0;
			} else {
				// Otherwise go to next index
				hashCode = hashCode + 1;
			}

			temp = bookTable[hashCode];

		} while (hashCode != start); // Exit loop if entire table searched

		throw new KeyNotFoundException();
	}

	/**
	 * Returns the number of key,value pairs in the data structure
	 */
	@Override
	public int numKeys() {
		return this.numKeys;
	}

	/**
	 * This method creates a hashCode for a given key using the capacity of the
	 * table
	 * 
	 * @param key - the key for a book that needs to be stored
	 * @return - the hashCode
	 */
	private int hashFunction(String key) {
		// Initial condition
		double hashCode = 0;
		// Go through string and add characters up
		for (int i = 0; i < key.length(); ++i) {
			// find power
			double power = Math.pow(31, i);
			// multiply by char value
			hashCode = (key.charAt(i) * power) + hashCode;

		}

		// Modular hashCode
		hashCode = hashCode % capacity;

		return (int) hashCode;
	}

	/**
	 * This method resizes a hashTable. Capacity is increased by: (capacity * 2)
	 * + 1
	 */
	private void resize() {
		// Holds array that is being resized
		HashItem[] temp = bookTable;
		// Double size of table and add 1
		capacity = (capacity * 2) + 1;

		bookTable = new HashItem[capacity];

		// Re insert values/keys
		for (int i = 0; i < temp.length; ++i) {
			// Get hashCode
			int hashCode = hashFunction(temp[i].key);
			// find correct place for item and insert it
			if (bookTable[hashCode] != null) {
				hashCode = openAddressInst(hashCode, bookTable);
				bookTable[hashCode] = temp[i];
			} else {
				bookTable[hashCode] = temp[i];
			}
		}
	}

	/**
	 * This method uses open addressing to find a place to insert a book
	 * 
	 * @param hashCode - current hashCode
	 * @param table - current hash table
	 * @return - insert location
	 */
	private int openAddressInst(int hashCode, HashItem[] table) {
		while (table[hashCode] != null) {
			// If at end of array, wrap around
			if (hashCode == table.length - 1) {
				hashCode = 0;
			}
			// otherwise look in next spot
			else {
				hashCode = hashCode + 1;
			}
		}
		// Return open position in table
		return hashCode;
	}
}