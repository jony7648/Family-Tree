package com.family_tree.structures.classes.generic;

import java.util.ArrayList;
import com.family_tree.structures.classes.node_structures.LinkedList;

public class Queue<T> {
	private LinkedList<T> _elem_list = new LinkedList<>();

	public void add(T value) {
		_elem_list.append(value);
	}

	public T remove_front() {
		if (!_elem_list.is_empty()) {
			return _elem_list.remove_head();
		}

		throw new IndexOutOfBoundsException("Queue is empty, cannot remove element!!");
		
	}

	public T front() {
		if (_elem_list.is_empty()) {
			throw new IndexOutOfBoundsException("Queue is empty");
		}
		
		return _elem_list.get_first();
	}

	public int get_count() {
		return _elem_list.get_size();
	}

	public boolean is_empty() {
		return _elem_list.is_empty();
	}
}
