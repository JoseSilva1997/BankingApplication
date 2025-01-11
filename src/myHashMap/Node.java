/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myHashMap;

import java.io.Serializable;

public class Node<K, V> implements Serializable {

		private K key;
		private V value;
		public Node<K, V> next;

		public Node(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		public K getKey() {
			return this.key;
		}

		public V getValue() {
			return this.value;
		}

		public void setValue(V value) {
			this.value = value;
		}
		
	}
