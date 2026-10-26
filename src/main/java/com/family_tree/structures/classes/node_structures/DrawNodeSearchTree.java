package com.family_tree.structures.classes.node_structures;

import com.family_tree.draw_objects.DrawNode;
import com.family_tree.data_storage.*;
import com.family_tree.math_classes.Geometry.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import com.family_tree.util.*;
import java.util.Iterator;

import com.family_tree.algorithms.Sort;
import com.family_tree.algorithms.Search;

public class DrawNodeSearchTree {
	private HashMap<String, PositionalList<DrawNode>> _namemap = new HashMap<>();

	public void add(DrawNode node) {
		Person person = node.get_person();
		String query = person.get_first_name() + ' ' + person.get_last_name();
		_namemap.get(query);

		if (_namemap.containsKey(query)) {
			PositionalList<DrawNode> list = _namemap.get(query);
			list.append(node);
		}
		else {
			PositionalList<DrawNode> list = new PositionalList<>();
			list.append(node);
			_namemap.put(query, list);
		}
	}

	public PositionalList<DrawNode> get(String query) {
		query = Util.title_string(query);
		
		if (_namemap.containsKey(query)) {
			PositionalList<DrawNode> list = _namemap.get(query);

			return list;
		}

		return new PositionalList<>();
	}

	public PositionalList<DrawNode> fuzzy_get(String first_name, String last_name) {
		final int SEARCH_PROPERTY = 0;

		String query = first_name + " " + last_name;
		
		query = Util.title_string(query);

		PositionalList<DrawNode> ret_list = new PositionalList<DrawNode>();
		Iterator<HashMap.Entry<String, PositionalList<DrawNode>>> iterator = _namemap.entrySet().iterator();

		while (iterator.hasNext()) {
			HashMap.Entry<String, PositionalList<DrawNode>> entry = iterator.next();
			PositionalList<DrawNode> name_list = entry.getValue();

			for (DrawNode node : name_list) {
				ret_list.append(node);
			}
		}

		PositionalList<DrawNode> search_list = Search.fuzzy(ret_list, SEARCH_PROPERTY, query);
		
		return search_list;
	}

	public PositionalList<DrawNode> get(String first_name, String last_name) {
		return get(first_name.strip() + " " + last_name.strip());			
	}
}
