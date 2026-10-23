package com.family_tree.structures.classes.node_structures;

import com.family_tree.draw_objects.DrawNode;
import com.family_tree.data_storage.*;
import com.family_tree.math_classes.Geometry.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import com.family_tree.util.*;

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
		else {
			return new PositionalList<>();
		}
	}

	public PositionalList<DrawNode> get(String first_name, String last_name) {
		return get(first_name.strip() + " " + last_name.strip());			
	}
}
