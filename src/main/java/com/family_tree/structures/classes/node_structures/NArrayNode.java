package com.family_tree.structures.classes.node_structures;

import com.family_tree.algorithms.Search;

import java.util.ArrayList;
import java.util.Iterator;

public class NArrayNode<T> implements Iterable<NArrayNode<T>>, Search.ISearchableObject {
	private ArrayList<NArrayNode<T>> _child_list = new ArrayList<>();
	private NArrayNode<T> _parent;
	private T _value;
	private int _iter_index = 0;

	public NArrayNode(T value) {
		_value = value;	
	}

	public void set_value(T value) {
		_value = value;
	}

	public T get_value() {
		return _value;
	}

	public NArrayNode<T> get_child(int index) {
		return _child_list.get(index);	
	}

	public void add_child(T value) {
		NArrayNode<T> new_node = new NArrayNode<>(value);
		
		_child_list.add(new_node);	
	}

	public void add_child(NArrayNode<T> node) {
		node.set_parent(this);
		_child_list.add(node);
	}

	public void set_parent(NArrayNode<T> node) {
		_parent = node;
	}

	public NArrayNode<T> get_parent() {
		return _parent;
	}

	public boolean has_parent() {
		return _parent != null;
	}

	public int child_count() {
		return _child_list.size();
	}
	
	public boolean has_children() {
		return _child_list.size() != 0;	
	}

	public ArrayList<NArrayNode<T>> get_child_arr() {
		return _child_list;
	}

	@Override
	public String get_search_string(int option) {
		if (_value != null && _value instanceof Search.ISearchableObject) {
			Search.ISearchableObject searchable = (Search.ISearchableObject)_value;
			return searchable.get_search_string(option);
		}
		return "";
	}

	public NArrayNodeIterator.Pre<T> iterator() {
		return new NArrayNodeIterator.Pre<T>(this);	
	}

	public Iterable<NArrayNode<T>> post_iter() {
		return () -> new NArrayNodeIterator.Post<>(NArrayNode.this);	
	}

	public Iterable<NArrayNode<T>> bfs_iter() {
		return () -> new NArrayNodeIterator.BFS<>(this);	
	}
}
