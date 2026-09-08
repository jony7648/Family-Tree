package com.family_tree.draw_objects;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.draw_components.interfaces.*;
import com.family_tree.structures.classes.node_structures.*;

import com.family_tree.data_storage.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class DrawNode implements IDrawObject {
	public final static int NODE_GAP_SPACE = 50;
	public final static int NODE_WIDTH = 100;
	public final static int NODE_HEIGHT = 100;
	public final static int TOTAL_NODE_GAP_SPACE_X = NODE_WIDTH + NODE_GAP_SPACE;
	public final static int TOTAL_NODE_GAP_SPACE_Y = NODE_HEIGHT + NODE_GAP_SPACE;

	private Person _person;
	private boolean _is_root_node = false;
	

	public enum NODE_POSITION {Center, Left, Right};
	
	Transform transform = new Transform();
	int node_space_needed = 1;
	
	public DrawNode(Person person, float x, float y) {
		transform.rect.x = x;
		transform.rect.y = y;
		transform.rect.w = NODE_WIDTH;
		transform.rect.h = NODE_HEIGHT;

		this._person = person;
		
	}
	
	public DrawNode(Person person) {
		transform.rect.x = 0;
		transform.rect.y = 0;
		transform.rect.w = NODE_WIDTH;
		transform.rect.h = NODE_HEIGHT;

		this._person = person;
	}

	
	public DrawNode() {};
	
	public void assign_to_root() {
		_is_root_node = true;
	}

	public boolean is_root() {
		return _is_root_node;
	}

	public Person get_person() {
		return _person;
	}


	@Override 
	public void draw(Graphics2D painter, Transform draw_transform) {
		if (_is_root_node) {
			return;
		}
		
		Rect scale_rect = draw_transform.get_scale_rect();
		
		painter.draw(new Rectangle2D.Double(scale_rect.x, scale_rect.y, scale_rect.w, scale_rect.h));
	}

	public void set_position(Vector2 position) {
		transform.rect.x = position.x;
		transform.rect.y = position.y;
	}

	public void set_position(float x, float y) {
		transform.rect.x = x;
		transform.rect.y = y;
	}

	public Vector2 get_position() {
		return new Vector2(transform.rect.x, transform.rect.y);
	}
	
	@Override 
	public Transform get_transform() {
		return transform;	
	}

}
