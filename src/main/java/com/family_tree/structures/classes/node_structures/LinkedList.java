package com.family_tree.structures.classes.node_structures;

import java.lang.IndexOutOfBoundsException;
import java.util.Iterator;

public class LinkedList<T> implements Iterable<T> {
	public static class ListIterator<T> implements Iterator<T> {
		Node<T> curr_node;
		
		public ListIterator(LinkedList<T> list) {
			curr_node = list.get_head();
		}

		@Override
		public T next() {
			Node<T> tmp_node = curr_node;
			curr_node = curr_node.get_next();

			return tmp_node.get_value();
		}

		@Override
		public boolean hasNext() {
			return curr_node != null;
		}
	}
	
	public static class Node<T> {
		private T _value;
		private Node<T> _next;	

		public Node(T value) {
			_value = value;
		}

		public void set_next(Node<T> next) {
			_next = next;
		}

		public boolean has_next() {
			return _next != null;
		}

		public Node<T> get_next() {
			return _next;
		}

		public T get_value() {
			return _value;
		}
	}

	private Node<T> _head;
	private Node<T> _tail;

	private int _size = 0;

	public LinkedList(T[] elems) {
		for (T elem : elems) {
			append(elem);
		}
	}

	public LinkedList() {};

	public void prepend(T value) {
		Node<T> new_head = new Node<>(value);
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
		Node<T> new_tail = new Node<>(value);
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
		Node<T> new_node = new Node<T>(value);
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

		Node<T> iter_node = _head;

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

		Node<T> iter_node = _head.get_next();

		for (int iter_index=1; iter_node.has_next(); iter_index++) {

			if (iter_index >= index-1) {
				break;
			}

			iter_node = iter_node.get_next();
		}

		Node<T> next_node = iter_node.get_next();
		iter_node.set_next(next_node.get_next());

		_size--;
	}

	public void remove(T obj) {
		if (_size == 0) { return;}

		if (obj == _head.get_value()) {
			_head = _head.get_next();
		}


		Node<T> curr_node = _head.get_next();

		for (int i=1; curr_node.has_next(); i++) {
			Node<T> next_node = curr_node.get_next();
			
			if (next_node.get_value() == obj) {
				curr_node.set_next(next_node.get_next());
				break;
			}

			curr_node = next_node;
		}
	}

	public T remove_head() {
		Node<T> ret_node = _head;
		_head = _head.get_next();
		_size--;


		return ret_node.get_value();
	}

	public boolean is_empty() {
		return _size == 0;	
	}

	public Node<T> get_head() {
		return _head;
	}

	public Node<T> get_tail() {
		return _tail;
	}

	public int get_size() {
		return _size;
	}

	@Override 
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (_size == 0) {
			return "[EMPTY LIST]";
		}
		
		Node<T> iter_node = _head;

		for (int i=0; i<_size; i++) {
			T value = iter_node.get_value();		

			builder.append(value + ",");
			iter_node = iter_node.get_next();
		}

		builder.setCharAt(builder.length()-1, ']');

		return "[" + builder;
	}

	@Override
	public ListIterator<T> iterator() {
		return new ListIterator<>(this);
	}
}
