package com.family_tree.structures.classes.generic;

import java.util.ArrayList;

public class Stack<T> {
	private ArrayList<T> _elem_list = new ArrayList<>();

	public void push(T value) {
		_elem_list.add(value);
	}

	public T pop() {
		if (!_elem_list.isEmpty()) {
			return _elem_list.removeLast();
		}

		return null;
	}

	public T top() {
		return _elem_list.getLast();
	}
	
	public int get_count() {
		return _elem_list.size();
	}

	public boolean is_empty() {
		return _elem_list.isEmpty();
	}
	
}

