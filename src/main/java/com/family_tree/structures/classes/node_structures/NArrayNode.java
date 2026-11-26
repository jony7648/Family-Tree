package com.family_tree.structures.classes.node_structures;

import com.family_tree.algorithms.Search;
import com.family_tree.structures.interfaces.NodeInterfaces.*;
import com.family_tree.util.Iterators;

import java.util.ArrayList;
import java.util.Iterator;

public class NArrayNode<T> implements Iterable<NArrayNode<T>>, Search.ISearchableObject {
	private PositionalList<NArrayNode<T>> _child_list = new PositionalList<>();
	private NArrayNode<T> _parent;
	private T _value;
	private PositionalList<NArrayNode<T>>.Node _pos_key;

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

	public NArrayNode<T> get_next_child(NArrayNode<T> child_node) {
		return _child_list.get_next_value(child_node.pos_key());
	}

	public NArrayNode<T> get_prev_child(NArrayNode<T> child_node) {
		return _child_list.get_prev_value(child_node.pos_key());
	}

	public void add_child(T value) {
		NArrayNode<T> new_node = new NArrayNode<>(value);

		add_child(new_node);
	}

	public void add_child(NArrayNode<T> node) {
		node.set_parent(this);
		node._pos_key = _child_list.append(node);
	}

	public void remove(T child) {
		for (int i=0; i<_child_list.size(); i++) {
			if (_child_list.get(i)._value == child) {
				_child_list.remove(i);
				break;
			}
		}
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
	
	public boolean is_leaf() {
		return _child_list.size() == 0;	
	}

	public PositionalList<NArrayNode<T>>.Node pos_key() {
		return _pos_key;
	}

	public PositionalList<NArrayNode<T>> get_child_list() {
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

	public Iterable<NArrayNode<T>> from_right_bfs() {
		return () -> new NArrayNodeIterator.FromRightBFS<>(this);	
	}

	public NArrayNode<T> get_first() {
		return _child_list.get_head();
	}

	public NArrayNode<T> get_last() {
		return _child_list.get_tail();
	}

	public boolean has_children() {
		return !_child_list.is_empty();
	}

	public int get_child_count() {
		return _child_list.size();
	}

	public Iterable<NArrayNode<T>> get_children() {
		return _child_list;
	}

	public Iterable<NArrayNode<T>> get_children(NArrayNode<T> start_node) {
		if (start_node == null) {
			return () -> Iterators.empty_iterate();
		}
		
		return () -> _child_list.iterator(start_node._pos_key);
	}

	public Iterable<NArrayNode<T>> get_reversed_children() {
		return () -> _child_list.reversed();
	}

	public Iterable<NArrayNode<T>> get_reversed_children(NArrayNode<T> end_node) {
		return () -> _child_list.reversed(end_node._pos_key);
	}

    public Iterable<NArrayNode<T>> get_all_children() {
        return this; 
    }
}
