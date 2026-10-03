package com.family_tree.draw_objects;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.draw_components.interfaces.*;
import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.structures.interfaces.NodeInterfaces.*;

import com.family_tree.draw_objects.geometric.*;

import com.family_tree.data_storage.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.awt.geom.Line2D;

public class DrawNode implements IDrawObject {
	public enum NODE_POSITION {Center, Left, Right};
	
	public final static int NODES_PER_POSITION_WIDTH = 3;
	public final static int NODE_GAP_SPACE = 50;
	public final static int NODE_WIDTH = 100;
	public final static int NODE_HEIGHT = 100;
	public final static int TOTAL_NODE_GAP_SPACE_X = NODE_WIDTH + NODE_GAP_SPACE;
	public final static int TOTAL_NODE_GAP_SPACE_Y = NODE_HEIGHT + NODE_GAP_SPACE;

	private ITreeNode<DrawNode> _tree_node;
	private Person _person;
	private boolean _is_root_node = false;
	private boolean _has_been_initally_positioned = false;

	Transform _transform = new Transform();
	int node_space_needed = 1;

	public LinkedList<IDrawObject> anchor_list = new LinkedList<>();
	
	public DrawNode(ITreeNode<DrawNode> node, Person person, float x, float y) {
		_transform.rect.x = x;
		_transform.rect.y = y;
		_transform.rect.w = NODE_WIDTH;
		_transform.rect.h = NODE_HEIGHT;
		_tree_node = node;
		_person = person;
	}
	
	public DrawNode(ITreeNode<DrawNode> node, Person person) {
		_transform.rect.x = 0;
		_transform.rect.y = 0;
		_transform.rect.w = NODE_WIDTH;
		_transform.rect.h = NODE_HEIGHT;
		_tree_node = node;
		_person = person;
	}


	public DrawNode(Person person) {
		_transform.rect.x = 0;
		_transform.rect.y = 0;
		_transform.rect.w = NODE_WIDTH;
		_transform.rect.h = NODE_HEIGHT;
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

	@Override 
	public void draw(Transform draw_transform, IDrawObject.DrawParameters params) {
		Rect scale_rect = draw_transform.get_scale_rect();

		if (_tree_node != null && _tree_node.has_children()) {
			for (ITreeNode<DrawNode> node : _tree_node.get_imeediate_children()) {
				DrawNode draw_node = node.get_value();
				Rect child_pos = draw_node.to_screen_transform(params.cam_pos()).rect;

				//child_transform.rect.add_position(cam_pos);

				//child_transform.rect.scale(draw_transform.scale.x, draw_transform.scale.y);
				
				Line2D draw_line = new Line2D.Double(
					scale_rect.x + scale_rect.w / 2,
					scale_rect.y + scale_rect.h / 2,
					scale_rect.x + child_pos.x,
					scale_rect.y + child_pos.y
				);
				
				//params.painter().draw(draw_line);
			}
		}
		params.painter().draw(new Rectangle2D.Double(scale_rect.x, scale_rect.y, scale_rect.w, scale_rect.h));

	}

	public LineObject add_child_node(DrawNode child) {
		Transform child_transform = child.get_transform();
		
		Vector2 start_pos = new Vector2(
			_transform.rect.x + _transform.rect.w / 2,
			_transform.rect.y + _transform.rect.h
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

	@Override
	public void set_position(Vector2 position) {
		update_anchor_objects(anchor_list, position);	
		
		_transform.rect.x = position.x;
		_transform.rect.y = position.y;

	}

	public void set_initial_position_flag() {
		_has_been_initally_positioned = true;
	}

	public void set_tree_node(ITreeNode<DrawNode> node) {
		_tree_node = node;	
	}

	public boolean get_initial_position_flag() {
		return _has_been_initally_positioned;
	}

	public Vector2 get_position() {
		return new Vector2(_transform.rect.x, _transform.rect.y);
	}

	public Person get_person() {
		return _person;
	}
	
	@Override 
	public Transform get_transform() {
		return _transform;
	}
}
