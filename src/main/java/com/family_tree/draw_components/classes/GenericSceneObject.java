package com.family_tree.draw_components.classes;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.structures.classes.node_structures.LinkedList;
import com.family_tree.draw_components.interfaces.*;
import com.family_tree.Listener.Listener;

import java.awt.*;
import javax.swing.*;
import java.awt.image.ImageObserver;



public class GenericSceneObject implements IDrawObject {
	private Transform _transform = new Transform();
	private int _top_left_block = 0;
	private int _bottom_right_block = 0;

	public enum Signal {
		TransformChanged;
	}

	public record SignalTransformChanged (
		Rect old_rect,
		Transform tranfsorm
	) {}
	
	private Listener<GenericSceneObject, Signal> _listener = new Listener<>(this, Signal.class); 
	

	private void fire_transform_update_signal(Rect old_rect) {
		_listener.fire_signal(Signal.TransformChanged, new Listener.Signal<GenericSceneObject>(
			this,
			new SignalTransformChanged(old_rect, _transform)
		));
	}
	
	public void set_position(Vector2 position) {
		Rect old_rect = _transform.get_scale_rect();
		
		_transform.rect.x = position.x;
		_transform.rect.y = position.y;

		fire_transform_update_signal(old_rect);
	}
	
	public void set_x(float x) {
		Rect old_rect = _transform.get_scale_rect();
		
		_transform.rect.x = x;
		fire_transform_update_signal(old_rect);
	}

	public void set_rect(float x, float y, float width, float height) {
		Rect old_rect = _transform.get_scale_rect();
		
		_transform.rect.x = x;
		_transform.rect.y = y;
		_transform.rect.w = width;
		_transform.rect.h = height;

		fire_transform_update_signal(old_rect);
	}

	public void set_rect(Rect rect) {
		Rect old_rect = _transform.get_scale_rect();
		
		
		_transform.rect = rect.copy();
		fire_transform_update_signal(old_rect);
	}

	public void set_scale(Vector2 scale) {
		Rect old_rect = _transform.get_scale_rect();
		
		_transform.scale = scale.copy();
		fire_transform_update_signal(old_rect);
	}

	public void set_scale(float x, float y) {
		Rect old_rect = _transform.get_scale_rect();
		_transform.scale.x = x;
		_transform.scale.y = y;
		fire_transform_update_signal(old_rect);
	}

	public void set_scale(float xy) {
		Rect old_rect = _transform.get_scale_rect();
		_transform.scale.x = xy;
		_transform.scale.y = xy;
		fire_transform_update_signal(old_rect);
	}

	public void set_y(float y) {
		Rect old_rect = _transform.get_scale_rect();
		_transform.rect.y = y;

		fire_transform_update_signal(old_rect);
	}

	public float get_x() {
		return _transform.rect.x;
	}

	public float get_y() {
		return _transform.rect.y;
	}

	

	public Vector2 get_position() {
		return new Vector2(_transform.rect.x, _transform.rect.y);
	}

	public void set_block_bounds(int top_left, int bottom_right) {
		_top_left_block = top_left;
		_bottom_right_block = bottom_right;
}

	public Listener<GenericSceneObject, Signal> get_generic_object_listener() {
		return _listener;
	}

	@Override
	public Transform get_transform() {
		return _transform;	
	}

	@Override
	public void draw(Transform draw_transform, IDrawObject.DrawParameters params) {}
}
