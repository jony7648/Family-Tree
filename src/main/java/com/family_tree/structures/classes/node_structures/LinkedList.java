package com.family_tree.structures.classes.node_structures;

import java.lang.IndexOutOfBoundsException;

public class LinkedList {
	public static class Head<T> {
		private Node<T> _head;
		private Node<T> _tail;

		private int _size = 0;

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

		public void remove(int index) {
			if (_size == 0) { return; };

			Node<T> iter_node = _head;
			Node<T> target_node = null;

			int iter_index = 0;

			if (index == 0) {
				_head = _head.get_next();
				_size--;
			}

			for (; iter_node.has_next(); iter_index++) {
				iter_node = iter_node.get_next();

				if (iter_index >= index-1) {
					target_node = iter_node;
					break;
				}
			}

			if (target_node == null) {
				throw new IndexOutOfBoundsException();
			}

			Node<T> next_node = target_node.get_next();
			target_node.set_next(next_node.get_next());

			_size--;
		}

		public void remove_head() {
			_head = _head.get_next();
			_size--;
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
}
