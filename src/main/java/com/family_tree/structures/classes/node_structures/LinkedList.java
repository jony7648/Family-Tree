package com.family_tree.structures.classes.node_structures;

import java.lang.IndexOutOfBoundsException;
import java.util.Iterator;
import java.util.Collection;

public class LinkedList<T> implements Iterable<T> {
	public class ListIterator implements Iterator<T> {
		Node curr_node;
		
		public ListIterator() {
			curr_node = get_head();
		}

		@Override
		public T next() {
			Node tmp_node = curr_node;
			curr_node = curr_node.get_next();

			return tmp_node.get_value();
		}

		@Override
		public boolean hasNext() {
			return curr_node != null;
		}
	}


	private class Node {
		private T _value;
		private Node _next;	

		public Node(T value) {
			_value = value;
		}

		public void set_next(Node next) {
			_next = next;
		}

		public boolean has_next() {
			return _next != null;
		}

		public Node get_next() {
			return _next;
		}

		public T get_value() {
			return _value;
		}
	}
	
	

	private Node _head;
	private Node _tail;

	private int _size = 0;

	public LinkedList(T[] elems) {
		for (T elem : elems) {
			append(elem);
		}
	}

	public LinkedList() {};

	public void prepend(T value) {
		Node new_head = new Node(value);
		_size++;

		if (_size == 1) {
			_head = new_head;
			_tail = new_head;
			return;
		}

		new_head.set_next(_head);
		_head = new_head;

	}

	public void append(T value) {
		Node new_tail = new Node(value);
		_size++;

		if (_size == 1) {
			_head = new_tail;
			_tail = new_tail;
			return;
		}

		_tail.set_next(new_tail);	
		_tail = new_tail;
	}

	public void insert(int index, T value) {
		Node new_node = new Node(value);
		_size++;

		if (_size == 1) { 
			_head = new_node;
			_tail = new_node;
			return; 
		};


		if (index == 0) {
			new_node.set_next(_head);
			_head = new_node;
			return;
		}

		Node iter_node = _head;

		for (int i=0; iter_node.has_next() && i<index; i++) {
			iter_node = iter_node.get_next();	
		}

		new_node.set_next(iter_node.get_next());
		iter_node.set_next(new_node);

	}

	public void remove(int index) throws IndexOutOfBoundsException {
		if (index >= _size || index < 0) {
			throw new IndexOutOfBoundsException();
		}

		if (index == 0) {
			_head = _head.get_next();
			_size--;
			return;
		}

		Node iter_node = _head.get_next();

		for (int iter_index=1; iter_node.has_next(); iter_index++) {

			if (iter_index >= index-1) {
				break;
			}

			iter_node = iter_node.get_next();
		}

		Node next_node = iter_node.get_next();
		iter_node.set_next(next_node.get_next());

		_size--;
	}

	public T set(int index, T value) throws IndexOutOfBoundsException {
		Node curr_node = _head;

		if (index >= _size) {
			throw new IndexOutOfBoundsException("Setting a value past the LinkedList bounds!");
		}
		
		for (int i=0; i<index; i++) {
			curr_node = curr_node._next;	
		}

		T old_value = curr_node._value;		

		curr_node._value = value;
		
		return old_value;
	}

	public T get(int index) throws IndexOutOfBoundsException {
		if (index >= _size || index < 0) {
			throw new IndexOutOfBoundsException("Trying to access past list bounds!");
		}
		
		Node curr = _head;

		for (int i=0; i<index; i++) {
			System.out.println("Value: " + curr._value);
			curr = curr._next;
		}

		return curr._value;
	}

	public boolean remove(Object obj) {
		if (_size == 0) { return false;}

		if (obj == _head.get_value()) {
			_head = _head.get_next();
		}

		Node curr_node = _head.get_next();

		for (int i=1; curr_node.has_next(); i++) {
			Node next_node = curr_node.get_next();
			
			if (next_node.get_value() == obj) {
				curr_node.set_next(next_node.get_next());
				break;
			}

			curr_node = next_node;

		}
		return true;
	}

	

	private void remove_next_node(Node prev_node) {
		_size--;	
		
		if (prev_node != null) {
			if (prev_node._next == _tail) {
				_tail = prev_node;
				return;
			}

			prev_node._next = prev_node._next._next;
		}
		else { //if the node being removed is the head
			_head = _head._next;
		}
	}

	

	public boolean removeAll(Collection<?> collection) {
		boolean ret_state = false;
		
		for (Object elem : collection) {
			//we need the previous node to avoid an O(mn^2)
			Node prev_node = null;
			Node curr_node = _head._next;

			while (curr_node != null) {
				if (curr_node._value == elem) {
					ret_state = true;
					remove_next_node(prev_node);
				}
				
				prev_node = curr_node;
				curr_node = curr_node._next;
			}
		}
		
		return ret_state;
	}

	public boolean retainAll(Collection<?> collection) {
		boolean ret_state = false;

		//we need the previous node to avoid an O(mn^2)
		Node prev_node = null;
		Node curr_node = _head._next;
		
		while (curr_node != null) {
			boolean match_found = false;
			
			for (Object elem : collection) {
				if (curr_node._value == elem) {
					match_found = true;
					ret_state = true;
					break;
				}
			}


			if (!match_found) {
				remove_next_node(prev_node);
			}

			prev_node = curr_node;
			curr_node = curr_node._next;

		}
		
		
		return ret_state;
	}

	public T remove_head() {
		Node ret_node = _head;
		_head = _head.get_next();
		_size--;


		return ret_node.get_value();
	}

	public void clear() {
		_size = 0;
		_head = null;
		_tail = null;
	}

	public boolean contains(Object object) {
		for (Object value : this) {
			if (value == object) {
				return true;
			}
		}

		return false;
	}

	public Object[] toArray() {
		Object[] ret_array = new Object[_size];

		Node curr_node = _head;
		
		for (int i=0; i<_size; i++) {
			ret_array[i] = curr_node.get_value();
			curr_node = curr_node._next;
		}

		return ret_array;
	}

	@SuppressWarnings("unchecked")
	public <E> E[] toArray(E[] arr) throws IndexOutOfBoundsException {
		if (arr.length < _size) {
			throw new ArrayStoreException("Array size is too small!!");
		}
		
		Node curr_node = _head;
		
		for (int i=0; i<_size; i++) {
			arr[i] = (E)curr_node.get_value();
			curr_node = curr_node._next;
		}

		return arr;
	}

	public T get_first() {
		if (is_empty()) { 
			return null;
		}
		
		return _head._value;
	}

	public T get_last() {
		if (!is_empty()) { 
			return null;
		}
		
		return _tail._value;
	}

	public boolean is_empty() {
		return _size == 0;	
	}

	public int get_size() {
		return _size;
	}

	private Node get_head() {
		return _head;
	}

	public Node get_tail() {
		return _tail;
	}

	@Override 
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (_size == 0) {
			return "[EMPTY LIST]";
		}
		
		Node iter_node = _head;

		for (int i=0; i<_size; i++) {
			T value = iter_node.get_value();		

			builder.append(value + ",");
			iter_node = iter_node.get_next();
		}

		builder.setCharAt(builder.length()-1, ']');

		return "[" + builder;
	}

	@Override
	public ListIterator iterator() {
		return new ListIterator();
	}
}
