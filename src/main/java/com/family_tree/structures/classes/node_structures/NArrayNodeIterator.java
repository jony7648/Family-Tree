package com.family_tree.structures.classes.node_structures;

import com.family_tree.structures.interfaces.NodeInterfaces.INodeIterator;
import com.family_tree.structures.classes.generic.*;

import java.util.Iterator;

public class NArrayNodeIterator {
	public static class IterEntry<T> {
		public NArrayNode<T> node;
		public int iter_index = 0;

		public IterEntry(NArrayNode<T> entry_node) {
			node = entry_node;
		}

		public boolean has_children_left() {
			return iter_index < node.child_count();	
		}
	}
	
	public static class Pre<T> implements Iterator<NArrayNode<T>>, INodeIterator {
		private Stack<IterEntry<T>> _iter_stack = new Stack<>();

		public Pre(NArrayNode<T> root_node) {
			IterEntry<T> entry = new IterEntry<>(root_node);
			_iter_stack.push(entry);	
		}

		@Override
		public NArrayNode<T> next() {
			IterEntry<T> top = _iter_stack.top();

			NArrayNode<T> iter_node = top.node.get_child(top.iter_index);
			top.iter_index++;

			if (iter_node.has_children()) {
				IterEntry<T> new_entry = new IterEntry<T>(iter_node);

				_iter_stack.push(new_entry);
				top = new_entry;
			}

			while (!top.has_children_left()) {
				_iter_stack.pop();

				if (!_iter_stack.is_empty()) {
					top =_iter_stack.top();
				}
				else {
					return iter_node;
				}
			}

			return iter_node;
		}

		@Override
		public boolean hasNext() {
			return !_iter_stack.is_empty();	
		}

		@Override
		public int get_stack_height() {
			return _iter_stack.get_count();
		}
	}

	public static class Post<T> implements Iterator<NArrayNode<T>>, INodeIterator{
		private Stack<IterEntry<T>> _iter_stack = new Stack<>();

		public Post(NArrayNode<T> root_node) {
			IterEntry<T> entry = new IterEntry<>(root_node);
			_iter_stack.push(entry);	
		}

		@Override 
		public NArrayNode<T> next() {
			IterEntry<T> top = _iter_stack.top();			 		

			while (!top.has_children_left()) {
				top = _iter_stack.pop();

				return top.node;
			}


			NArrayNode<T> iter_node = top.node.get_child(top.iter_index);
			top.iter_index++;
			

			while (iter_node.has_children()) {
				System.out.println(iter_node.has_children());
				System.out.println(iter_node.get_value());
				
				IterEntry<T> new_entry = new IterEntry<T>(iter_node);

				_iter_stack.push(new_entry);
				top = new_entry;
				iter_node = top.node.get_child(top.iter_index);
				top.iter_index++;
			}
		
			return iter_node;
			
		}

		@Override 
		public boolean hasNext() {
			return !_iter_stack.is_empty();
		}

		@Override
		public int get_stack_height() {
			return _iter_stack.get_count();
		}
	}

	public static class BFS<T> implements Iterator<NArrayNode<T>>{
		private Queue<NArrayNode<T>> node_queue = new Queue<>();
		
		public BFS(NArrayNode<T> root_node) {
			for (NArrayNode<T> node : root_node.get_child_arr()) {
				node_queue.add(node);
			}
		}

		public NArrayNode<T> next() {
			NArrayNode<T> queue_node = node_queue.front();
			
			for (NArrayNode<T> node : queue_node.get_child_arr()) {
				node_queue.add(node);
			}

			node_queue.remove_front();

			return queue_node;
		}

		public boolean hasNext() {
			return !node_queue.is_empty();
		}
	}
}
