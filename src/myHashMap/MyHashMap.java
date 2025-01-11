/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package myHashMap;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

/**
 * Custom implementation of a HashMap.
 */
public class MyHashMap<K, V> implements Serializable {

	private final float LOAD_FACTOR = 0.75f;  // Default load factor for resizing.
	private int capacity = 16; // Default initial size
	private int size = 0; // Tracks the number of elements in the map

	private Node<K, V> table[];

	/**
	 * Constructs an empty MyHashMap with default capacity.
	 */
	public MyHashMap() {
		this.table = new Node[capacity];
	}

	/**
	 * Returns the amount of elements in the table
	 */
	public int size() {
		return size;
	}

	/**
	 * Empty the HashMap
	 */
	public void clear() {
		table = new Node[capacity];
		size = 0;
	}

	/**
	 * Associates the specified value with the specified key and adds them to this map. 
	 * If the map previously contained a mapping for the key, the old value is replaced
	 */
	public void put(K key, V value) {

		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null");
		}

		int hash = Math.abs(key.hashCode() % capacity); // Calculate bucket index
		Node<K, V> n = table[hash];

		if (n == null) {
			// No collision: Add the new key-value pair at the head
			table[hash] = new Node<>(key, value);
			size++; // Update number of elements stored.
		} else {
			// Handle collision using separate chaining
			while (n.next != null) { // Iterate over list of elements at index "hash" to check if the key already exists
				if (n.getKey().equals(key)) {
					// Key already exists: Update the value
					n.setValue(value);
					return;
				}
				n = n.next;
			}
			if (n.getKey().equals(key)) {
				// Check last node for a matching key
				n.setValue(value);
				return;
			}
			// Key does not exist: Add new node to the end of the chain
			n.next = new Node<>(key, value);
			size++; // Update number of elements stored
		}

		//Check LOAD_FACTOR and resize if necessary
		if (size >= capacity * LOAD_FACTOR) {
			resize();
		}
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public V get(K key) {

		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null");
		}

		int hash = Math.abs(key.hashCode() % capacity); // Calculate bucket index.
		Node<K, V> n = table[hash];

		// Iterate over the chain to find the key.
		while (n != null) {
			if (n.getKey().equals(key)) {
				return n.getValue();
			}
			n = n.next;
		}
		return null; // Key not found
	}
	
	/**
	 * Removes the mapping for the specified key from this map if present
	 */
	public Node<K, V> remove(K key) {
		// Calculate the hash and the index at which this key is meant to be stored
		int hash = Math.abs(key.hashCode() % capacity);
		// Retrieve the element at the calculated position
		Node<K, V> n = table[hash];

		if (n == null) { // If there is no element at the specified index
			return null;
		}
		// Check the head of the chain
		if (n.getKey().equals(key)) {
			table[hash] = n.next; // Remove the head node
			n.next = null;
			size--;
			return n;
		}

		// Traverse the chain to find and remove the key
		Node<K, V> previous = n;
		n = n.next;
		while (n != null) {
			if (n.getKey().equals(key)) {
				previous.next = n.next;
				n.next = null;
				size--;
				return n;
			}
			// Advance one element
			previous = n;
			n = n.next;

		}

		return null; // Key not found
	}

	/**
	 * Checks whether the map contains a mapping for the specified key.
	 */
	public boolean containsKey(K key) {
		int hash = Math.abs(key.hashCode() % capacity); // Calculate bucket index.
		Node<K, V> e = table[hash];

		// Traverse the chain to find the key
		while (e != null) {
			if (e.getKey().equals(key)) {
				return true;
			}
			e = e.next;
		}
		return false; // Key not found
	}

	/**
	 * Checks whether the map contains the specified value.
	 */
	public boolean containsValue(V value) {
		for (Node<K, V> n : table) {
			while (n != null) {
				if (value == null ? n.getValue() == null : value.equals(n.getValue())) {
					return true; // Value found
				}
				n = n.next;
			}
		}
		return false; // Value not found
	}

	/**
	 * Returns a set of all keys in the map.
	 */
	public Set<K> keySet() {

		Set<K> keys = new HashSet<>();

		for (Node<K, V> bucket : table) {
			Node<K, V> current = bucket;
			while (current != null) {
				keys.add(current.getKey());
				current = current.next;
			}
		}
		return keys;
	}

	/**
	 * Returns a collection of all values in the map.
	 */
	public Collection<V> values() {

		Collection<V> values = new ArrayList<>();

		for (Node<K, V> bucket : table) {
			Node<K, V> current = bucket;
			while (current != null) {
				values.add(current.getValue());
				current = current.next;
			}
		}

		return values;
	}

	/*
	 * Checks whether the map is empty.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Resizes the hash table when the load factor threshold is reached.
	 */
	private void resize() {
		int newCapacity = capacity * 2;
		Node<K, V>[] newTable = new Node[newCapacity];

		for (Node<K, V> bucket : table) { // iterate through old bucket
			Node<K, V> current = bucket;
			while (current != null) {
				// Recalculate hash and index for the new table
				int hash = Math.abs(current.getKey().hashCode() % newCapacity);
				Node<K, V> next = current.next;

				// Insert node into new table
				current.next = newTable[hash];
				newTable[hash] = current;

				// Move to the next node in the old bucket
				current = next;
			}
		}
		table = newTable;
		capacity = newCapacity;
	}

	
}
