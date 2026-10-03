package com.family_tree.structures.interfaces;

public class NodeInterfaces {
	public interface INodeIterator {
		int get_stack_height();
	}

	public interface ITreeNode<T> {
		T get_value();
		int get_child_count();
		boolean has_children();
		Iterable<? extends ITreeNode<T>> get_all_children();
		Iterable<? extends ITreeNode<T>> get_imeediate_children();
	}
}
