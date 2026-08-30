package com.family_tree.draw_components.classes;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.draw_components.interfaces.IDrawObject;
import com.family_tree.draw_objects.*;

import java.awt.*;
import javax.swing.*;
import java.lang.Math;

public class Camera {
	public final float MAX_ZOOM = 2.0f;
	public final float MIN_ZOOM = 0.1f;
	public final float ZOOM_FACTOR = 0.1f;
	
	private Vector2 _position = new Vector2(1);
	private float _scale = 1;
	private DefaultListModel<IDrawObject> draw_list = new DefaultListModel<>();

	final static int BASE_MOVEMENT_SPEED = 50;

	public void draw(Graphics2D painter) {
		for (int i=0; i<draw_list.size(); i++) {
			IDrawObject object = draw_list.get(i);
			
			Transform object_transform = object.get_transform().copy();

			object_transform.rect.subtract_position(_position);
			object_transform.scale = new Vector2(_scale, _scale);
			
			object.draw(painter, object_transform);
			
		}
	}

	public void move(Vector2 move_vec) {
		float movement_speed = BASE_MOVEMENT_SPEED * 2 * (MAX_ZOOM - _scale);
		
		_position = _position.add(move_vec.scale(movement_speed));
	}

	public void set_scale(float ammount) {
		_scale = Math.clamp(ammount, MIN_ZOOM, MAX_ZOOM);
	}

	public void zoom_in() {
		set_scale(_scale + ZOOM_FACTOR);
		System.out.println("moving");
	}

	public void zoom_out() {
		set_scale(_scale - ZOOM_FACTOR);
	}

	public void add_draw_object(IDrawObject draw_object) {
		draw_list.addElement(draw_object);
	}
}
