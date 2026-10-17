package com.family_tree.structures.classes.node_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedList<T> implements Iterable<T> {
	Node _head;
	Node _tail;
	int _size = 0;

	public class ListIterator implements Iterator<T> {
		Node node = _head;

		public T next() {
			Node old_node = node;
			node = node.get_next();

			return old_node.get_value();
		}

		public boolean hasNext() {
			return node != null;
		}
	}
	
	public class Node {
		private T value;
		private Node prev;
		private Node next;

		public Node(Node prev, T value, Node next) {
			this.value = value;
			this.prev = prev;
			this.next = next;
		}

		public T get_value() {
			return value;
		}
		
		public Node get_prev() {
			return prev;
		}
		public Node get_next() {
			return next;
		}
		public void set_value(T value) {
			this.value = value;
		}
		public void set_next(Node next) {
			this.next = next;
		}

		public void set_prev(Node prev) {
			this.prev = prev;
		}
		public boolean has_prev() {
			return prev != null;
		}

		public boolean has_next() {
			return next != null;
		}
	}

	public DoublyLinkedList() {}

	public DoublyLinkedList(T[] items) {
		for (T item : items) {
			append(item);
		}
	}

	public void append(T value) {
		_size++;
		
		if (_size == 1) {
			_tail = new Node(null, value, null);
			_head = _tail;
			return;
		}

		
		_tail.set_next(new Node(_tail, value, null));
		_tail = _tail.get_next();
		
	}

	public void prepend(T value) {
		_size++;

		if (_size == 1) {
			_tail = new Node(null, value, null);
			_head = _tail;
			return;
		}

		_head.set_prev(new Node(null, value, _head));
		_head = _head.get_prev();
	}

	public void add_between(Node prev, T value) {
		Node next = prev.get_next();	
		
		Node new_node = new Node(prev, value, next);	
		prev.set_next(new_node);
		
		//incase we are at the last position
		if (next != null) {
			next.set_prev(new_node);
		}
	}

	public void insert(int index, T value) throws IndexOutOfBoundsException {
		if (_size < index || index < 0) {
			throw new IndexOutOfBoundsException("Inserting past linked list bounds!");
		}

		_size++;

		Node curr_node = _head;

		for (int i=0; i<index-1; i++) {
			curr_node = curr_node.get_next();
		}

		add_between(curr_node, value);
	}

	public T remove_tail() throws NoSuchElementException {
		if (_size == 0) {
			throw new NoSuchElementException("Linked List is empty!");
		}

		_size--;

		
		Node old_tail = _tail;

		if (_size == 0) {
			_head = null;
			_tail = null;
			return old_tail.get_value();
		}


		_tail = _tail.get_prev();
			
		return old_tail.get_value();
	}

	public T get_head() {
		return _head.get_value();
	}

	public T get_tail() {
		return _tail.get_value();
	}

	public T set(int index, T value) throws NoSuchElementException {
		if (_size <= index || index < 0) {
			throw new NoSuchElementException("Index is not valid");
		}

		Node curr_node = _head;

		for (int i=0; i<index; i++) {
			curr_node = curr_node.get_next();	
		}

		T old_value = curr_node.value;
		curr_node.value = value;

		return old_value;
	}

	public T get(int index) throws NoSuchElementException {
		if (_size <= index || index < 0) {
			throw new NoSuchElementException("Index is not valid");
		}

		Node curr_node = _head;

		for (int i=0; i<index; i++) {
			curr_node = curr_node.get_next();	
		}

		return curr_node.get_value();
	}

	public Node get_node(int index) throws NoSuchElementException {
		if (_size <= index || index < 0) {
			throw new NoSuchElementException("Index is not valid");
		}

		Node curr_node = _head;

		for (int i=0; i<index; i++) {
			curr_node = curr_node.get_next();	
		}

		return curr_node;
	}

	public int get_size() {
		return _size;
	}

	public boolean is_empty() {
		return _size == 0;
	}

	public Iterator<T> iterator() {
		return new ListIterator();
	}
}
