package com.family_tree.draw_objects;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.draw_components.interfaces.*;
import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.structures.interfaces.NodeInterfaces.*;

import com.family_tree.draw_objects.geometric.*;
import com.family_tree.draw_components.classes.GenericSceneObject;

import com.family_tree.data_storage.*;
import com.family_tree.singletons.ImageLoader;

import com.family_tree.algorithms.Sort;
import com.family_tree.algorithms.Search;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.awt.geom.Line2D;
import java.nio.file.Path;

public class DrawNode extends GenericSceneObject implements IDrawObject, Sort.ISortable, Search.ISearchableObject {
	public enum NODE_POSITION {Center, Left, Right};
	
	public final static int NODES_PER_POSITION_WIDTH = 3;
	public final static int NODE_GAP_SPACE = 50;
	public final static int NODE_WIDTH = 100;
	public final static int NODE_HEIGHT = 100;
	public final static int TOTAL_NODE_GAP_SPACE_X = NODE_WIDTH + NODE_GAP_SPACE;
	public final static int TOTAL_NODE_GAP_SPACE_Y = NODE_HEIGHT + NODE_GAP_SPACE + 30;

	private NArrayNode<DrawNode> _narray_node;
	private Person _person;
	private boolean _is_root_node = false;
	private boolean _has_been_initally_positioned = false;

	public LinkedList<IDrawObject> anchor_list = new LinkedList<>();

	public long _order_value = 0;

	private ImageLoader.ImageReference _image_refrence = new ImageLoader.ImageReference();
	
	public DrawNode(NArrayNode<DrawNode> node, Person person, float x, float y) {
		this(node, person);
		
		set_x(x);
		set_y(y);
	}
	
	public DrawNode(NArrayNode<DrawNode> node, Person person) {
		this(person);
		_narray_node = node;
	}


	public DrawNode(Person person) {
		set_rect(0, 0, NODE_WIDTH, NODE_HEIGHT);
		_person = person;
	};
	
	public DrawNode() {
		_person = new Person();
	}
	
	public void assign_to_root() {
		_is_root_node = true;
	}

	public boolean is_root() {
		return _is_root_node;
	}

	public void load_image(Path path) {
		_image_refrence = ImageLoader.request_image(path);
	}

	public Image get_image (Path path) {
		return _image_refrence.get_image();
	}

	@Override 
	public void draw(Transform draw_transform, IDrawObject.DrawParameters params) {
		Rect scale_rect = draw_transform.get_scale_rect();

		Graphics2D g = params.painter();

		g.drawImage(_image_refrence.get_image(), (int)scale_rect.x, (int)scale_rect.y, (int)scale_rect.w, (int)scale_rect.h, params.holder());
	}

	public LineObject add_child_node(DrawNode child) {
		Transform child_transform = child.get_transform();

		Transform transform = get_transform();
		
		Vector2 start_pos = new Vector2(
			transform.rect.x + transform.rect.w / 2,
			transform.rect.y + transform.rect.h
		);


		Vector2 end_pos = new Vector2(
			child_transform.rect.x + child_transform.rect.w / 2,
			child_transform.rect.y
		);
		
		System.out.println(start_pos);
		System.out.println(end_pos);
		
		LineObject line = new LineObject(start_pos, end_pos, new Vector2(1));

		anchor_list.append(line);

		return line;
	}

	public String get_search_string(int _option) {
		return _person.get_first_name() + " " + _person.get_last_name();
	}

		
	public static long get_order(String first_name, String last_name) {
		long order = 0;
		
		for (int i=0; i<first_name.length(); i++) {
			Character let = first_name.charAt(i);
			int order_val = let.charValue();

			order += order_val;
		}

		for (int i=0; i<last_name.length(); i++) {
			Character let = last_name.charAt(i);
			int order_val = let.charValue();

			order += order_val;
		}

		return order;
	}

	public long get_order() {
		Person person = get_person();
		
		return get_order(person.get_first_name(), person.get_last_name());
	}

	@Override
	public void set_position(Vector2 position) {
		super.set_position(position);
		update_anchor_objects(anchor_list, position);	
	}

	public void set_tree_node(NArrayNode<DrawNode> node) {
		_narray_node = node;	
	}

	public NArrayNode<DrawNode> get_narray_node() {
		return _narray_node;
	}

	public boolean get_initial_position_flag() {
		return _has_been_initally_positioned;
	}

	public Person get_person() {
		return _person;
	}

	public void set_search_order_value(long order) {
		_order_value = order;
	}

	public long get_search_order_value() {
		return _order_value;
	}
}
