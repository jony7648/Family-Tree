package com.family_tree.structures.classes.node_structures;

import java.lang.IndexOutOfBoundsException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PositionalList<T> implements Iterable<T> {
	//positional list for the hashmap
	Node _head = new Node(null, null, null);
	Node _tail = new Node(null, null, null);
	int _size = 0;

	public class ListIterator implements Iterator<T> {
		Node node = _head.next;

		public ListIterator() {}

		public ListIterator(Node start_node) {
			if (start_node.is_valid()) {
				node = start_node;
			}
		}

		public T next() {
			Node old_node = node;
			node = node.get_next();

			return old_node.get_value();
		}

		public boolean hasNext() {
			return node != _tail;
		}
	}

	public class NodeIterator implements Iterator<Node> {
		Node node = _head.next;

		public Node next() {
			Node old_node = node;
			node = node.get_next();

			return old_node;
		}

		public boolean hasNext() {
			return node != _tail;
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
		
		private Node get_prev() {
			return prev;
		}
		private Node get_next() {
			return next;
		}
		private void set_value(T value) {
			this.value = value;
		}
		private void set_next(Node next) {
			this.next = next;
		}

		private void set_prev(Node prev) {
			this.prev = prev;
		}

		private void clear() {
			next = null;
		}

		private boolean is_valid() {
			return next != null;
		}
	}

	public PositionalList() {
		_head.next = _tail;
		_tail.prev = _head;
	}

	public PositionalList(T[] items) {
		this();
		
		for (T item : items) {
			append(item);
		}
	}

	public Node append(T value) {
		_size++;
		
		Node prev = _tail.prev;
		Node new_node = new Node(prev, value, _tail);

		prev.next = new_node;
		_tail.prev = new_node;

		return new_node;
	}

	public Node prepend(T value) {
		_size++;
		
		Node next = _head.next;
		Node new_node = new Node(_head, value, next);

		_head.next = new_node;
		next.prev = new_node;

		return new_node;
	}

	public void add_after(Node prev, T value) throws IllegalStateException {
		if (!prev.is_valid()) {
			throw new IllegalStateException("This node is not valid!");
		}

		_size++;
		
		Node next = prev.get_next();	
		
		Node new_node = new Node(prev, value, next);	
		prev.set_next(new_node);
		next.set_prev(new_node);
	}

	public void insert(int index, T value) throws IndexOutOfBoundsException {
		if (_size < index || index < 0) {
			throw new IndexOutOfBoundsException("Inserting past linked list bounds!");
		}

		Node curr_node = _head;

		for (int i=0; i<index-1; i++) {
			curr_node = curr_node.get_next();
		}

		//size gets incremented in the add_after method
		add_after(curr_node, value);
	}

	public T remove_tail() throws NoSuchElementException {
		if (_size == 0) {
			throw new NoSuchElementException("Linked List is empty!");
		}

		_size--;
		
		Node last_node = _tail.get_prev();
		Node new_last = last_node.prev;

		new_last.next = _tail;
		_tail.prev = new_last;
			
		return last_node.get_value();
	}

	public void remove_all_after(Node node) {
		if (node.is_valid() == false || _size == 0) {
			return;
		}

		Node it_node = node.next;

		while (it_node != _tail) {
			Node tmp = it_node;
			it_node = it_node.next;	

			tmp.clear();
			_size--;
		}
		
		_tail.prev = node;
		node.next = _tail;
		
	}

	public T get_head() {
		return _head.next.get_value();
	}

	public Node get_head_node() {
		if (_size == 0) {
			return null;
		}
		
		return _head.next;
	}

	public T get_tail() {
		return _tail.prev.get_value();
	}

	public Node get_tail_node() {
		if (_size == 0) {
			return null;
		}
		
		return _tail.prev;
	}

	public T set(int index, T value) throws NoSuchElementException {
		if (_size <= index || index < 0) {
			throw new NoSuchElementException("Index is not valid");
		}

		Node curr_node = _head.next;

		for (int i=0; i<index; i++) {
			curr_node = curr_node.get_next();	
		}


		T old_value = curr_node.value;
		curr_node.value = value;

		return old_value;
	}

	public T set(Node node, T value) {
		if (!node.is_valid()) {
			return null;
		}
		
		T old_value = node.value;
		node.value = value;

		node.value = value;

		return old_value;
	}

	public Node get_node(int index) throws IndexOutOfBoundsException {
		if (_size <= index || index < 0) {
			throw new IndexOutOfBoundsException("Trying to access past list bounds!");
		}

 		Node curr_node = _head.next;

		for (int i=0; i<index; i++) {
			curr_node = curr_node.get_next();	
		}

		return curr_node;
	}

	public Node get_node(T obj) throws IndexOutOfBoundsException {
		Node curr_node = _head.next;

		//loop till the size so if we can't find the node the curr_node will be null
		for (int i=0; i<_size; i++) {
			Object value = curr_node.get_value();
			
			if (obj.equals(value)) {
				break;
			}
			curr_node = curr_node.get_next();	
		}

		if (curr_node == null) {
			throw new NoSuchElementException("No node was associated with this object!");
		}

		return curr_node;
	}

	public T get(int index) throws IndexOutOfBoundsException {
		Node node = get_node(index);

		if (node == null) {
			return null;
		}
		
		return node.get_value();
	}

	public T remove(Node remove_node) throws IllegalStateException {
		if (!remove_node.is_valid()) {
			throw new IllegalStateException("This is not a valid list node!");
		}
		
		//if the node is a head or tail, we don't need to do this
		if (remove_node.prev != null && remove_node.next != null) {
			Node prev = remove_node.get_prev();
			Node next = remove_node.get_next();
			
			prev.next = remove_node.next;		
			next.prev = remove_node.prev;
		}
		
		_size--;
		remove_node.clear();
		return remove_node.value;
	}

	public T remove(int index) {
		return remove(get_node(index));	
	}

	public T remove(T obj) {
		Node node = get_node(obj);

		if (node == null) {
			return null;
		}
		
		return remove(get_node(obj));	
	}

	public Node get_next_node(Node node) {
		if (node_has_next(node) == false) {
			return null;
		}

		if (!node.is_valid()) {
			return null;
			
		}
		return node.get_next();	
	}

	public Node get_prev_node(Node node) throws NoSuchElementException {
		if (node_has_prev(node) == false) {
			throw new IndexOutOfBoundsException("Trying to access past list bounds!");
		}
		
		return node.get_prev();	
	}

	public T get_next_value(Node node) throws NoSuchElementException {
		return get_next_node(node).get_value();	
	}

	public T get_prev_value(Node node) throws NoSuchElementException {
		return get_prev_node(node).get_value();	
	}

	public int size() {
		return _size;
	}

	public boolean is_empty() {
		return _size == 0;
	}

	public Iterator<T> iterator() {
		return new ListIterator();
	}

	public Iterator<T> iterator(Node node) {
		return new ListIterator(node);
	}

	

	public boolean node_has_next(Node node) {
		return node.next != _tail;
	}

	public boolean node_has_prev(Node node) {
		return node.prev != _head;
	}
}
