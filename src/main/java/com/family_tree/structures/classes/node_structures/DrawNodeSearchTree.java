package com.family_tree.structures.classes.node_structures;

import com.family_tree.draw_objects.DrawNode;
import com.family_tree.data_storage.*;
import com.family_tree.math_classes.Geometry.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import com.family_tree.util.*;

/*
public class DrawNodeSearchTree { 
	ArrayList<OrderedList> name_list = new ArrayList<>();
	OrderedList mid_group;
	int mid_group_index = 0;


	public class NodeSearchResult {
		enum Dir {
			Left,
			Right,
			Exact
		}

		public Dir dir_to_prev = Dir.Left;
		public int index = 0;
		public OrderedList list;
	}

	
	private class OrderedList {
		private LinkedList<DrawNode> list = new LinkedList<>();
		private long order = 0;

		public OrderedList(DrawNode node, long order) {
			list.append(node);
			this.order = order;	
		}
	}

	

	private void add_name_group(DrawNode node) {
		OrderedList list = new OrderedList(node, node.calculate_order());

		if (name_list.isEmpty()) {
			mid_group = list;
			mid_group_index = 0;
		}
		
		name_list.add(list);
	}

	private void add_name_group(DrawNode node, OrderedList list) {
		name_list.add(list);
	}

	

	private void add_to_name_group(DrawNode node, NodeSearchResult result) {

		int result_index = result.index;
		
		switch (result.dir_to_prev) {
			case Exact:
				result.list.list.append(node);
			break;
			
			case Left:
				//if node is to the right we need to place it to the right in the list
				result_index++;
			default:
				OrderedList list = new OrderedList(node, node.calculate_order());
				name_list.add(result_index, list);
				list.list.append(node);
				break;
		}
	}

	public void add(DrawNode node) {
		if (name_list.isEmpty()) {
			add_name_group(node);
			return;
		}

		OrderedList curr_group = mid_group;
		long node_order = node.calculate_order();
		
		NodeSearchResult result = search_through_ordered_lists(node_order);	

		add_to_name_group(node, result);	

		for (OrderedList list : name_list) {
			System.out.println(list.order);

		}

		System.out.println("\n\nDone\n\n");
	}

	public NodeSearchResult search_through_ordered_lists(long node_order) {
		int left_bord = 0;
		int right_bord = name_list.size();

		OrderedList last_mid = null;

		NodeSearchResult ret_result = new NodeSearchResult();
		
		do {
			int mid_pos = (right_bord + left_bord) / 2;
			OrderedList mid_list = name_list.get(mid_pos);
			last_mid = mid_list;
			ret_result.index = mid_pos;


			if (node_order == mid_list.order) {
				ret_result.dir_to_prev = NodeSearchResult.Dir.Exact;	
				break;
			}
			else if (node_order < mid_list.order) {
				right_bord = mid_pos;
				ret_result.dir_to_prev = NodeSearchResult.Dir.Right;	
			}
			else {
				left_bord = mid_pos+1;
				ret_result.dir_to_prev = NodeSearchResult.Dir.Left;	
			}
		} while (left_bord < right_bord);

		ret_result.list = last_mid;	

		return ret_result;
	}

	public DrawNode search_jump(String query) throws NoSuchElementException, IllegalStateException {
		int seperator_index = query.indexOf(' ');

		if (seperator_index == -1 && seperator_index != query.length()-1) {
			throw new IllegalStateException("Not a valid Name: " + query);
		}
		
		String first_name = query.substring(0, seperator_index);
		String last_name = query.substring(seperator_index);
		
		long order = DrawNode.calculate_order(first_name, last_name);

		NodeSearchResult result = search_through_ordered_lists(order);

		if (result.dir_to_prev != NodeSearchResult.Dir.Exact) {
			throw new NoSuchElementException("No node found in the tree with the name: " + query);
		}

		for (DrawNode node : result.list.list) {
			Person person = node.get_person();
			if (person.get_first_name().equals(first_name)) && person.get_last_name().equals(last_name)) {
			}
		}
		
	}
}
*/

public class DrawNodeSearchTree {
	private HashMap<String, LinkedList<DrawNode>> _namemap = new HashMap<>();

	public void add(DrawNode node) {
		Person person = node.get_person();
		String query = person.get_first_name() + ' ' + person.get_last_name();
		_namemap.get(query);

		if (_namemap.containsKey(query)) {
			LinkedList<DrawNode> list = _namemap.get(query);
			list.append(node);
		}
		else {
			LinkedList<DrawNode> list = new LinkedList<>();
			list.append(node);
			_namemap.put(query, list);
		}
	}

	public LinkedList<DrawNode> get(String query) {
		query = Util.title_string(query);
		
		if (_namemap.containsKey(query)) {
			LinkedList<DrawNode> list = _namemap.get(query);

			return list;
		}
		else {
			return new LinkedList<>();
		}
	}

	public LinkedList<DrawNode> get(String first_name, String last_name) {
		return get(first_name.strip() + " " + last_name.strip());			
	}
}
