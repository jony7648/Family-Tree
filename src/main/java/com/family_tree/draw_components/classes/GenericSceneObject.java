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
	private LinkedList<GenericSceneObject> _anchor_list = new LinkedList<>();

	public static class Viewer {
		private GenericSceneObject _obj;

		public Viewer() {};

		public Viewer(GenericSceneObject obj) {
			set_object(obj);
		}

		public void set_object(GenericSceneObject obj) {
			if (obj != null) {
				obj.get_generic_object_listener().remove_receiver(this, GenericSceneObject.Signal.ObjectRemoved);
			}
			
			_obj = obj;

			_obj.get_generic_object_listener().add_receiver(this, Signal.ObjectRemoved, on_signal_object_removed());
		}

		public Transform get_transform() throws IllegalStateException {
			verify_obj();	
			
			return _obj.get_transform().copy();
		}

		private void verify_obj() {
			if (_obj == null) {
				throw new IllegalStateException("Object is not attached to the object view!");
			}
		}

		public boolean obj_exists() {
			return _obj != null;
		}

		public Listener.ISignalFire<GenericSceneObject> on_signal_object_removed() {
			return (emitter, signal)-> {
				_obj = null;
			};
		}
	}

	public enum Signal {
		TransformChanged,
		ObjectRemoved,
	}

	public record SignalTransformChanged (
		Rect old_rect,
		Transform tranfsorm
	) {}
	
	private Listener<GenericSceneObject, Signal> _listener = new Listener<>(this, Signal.class); 

	private void fire_transform_update_signal(Rect old_rect) {
		_on_object_moved(old_rect);
		update_anchor_objects(old_rect);
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

	public void adjust_x(float x) {
		set_x(_transform.rect.x + x);		
	}

	public void adjust_y(float y) {
		set_y(_transform.rect.x + y);
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

	public float get_right_x() {
		//does not apply scalling
		return _transform.rect.x + _transform.rect.w;
	}

	public Vector2 get_position() {
		return new Vector2(_transform.rect.x, _transform.rect.y);
	}

	public void set_block_bounds(int top_left, int bottom_right) {
		_top_left_block = top_left;
		_bottom_right_block = bottom_right;
	}


	public void add_to_anchor_list(GenericSceneObject obj) {
		_anchor_list.append(obj);
	}

	public void remove_from_anchor_list(GenericSceneObject obj) {
		_anchor_list.remove(obj);
	}

	private void update_anchor_objects(Rect old_rect) {
		Rect parent_rect = get_transform().rect;
		
		for (GenericSceneObject obj : _anchor_list) { 
			Rect obj_rect = obj.get_transform().rect;
			
			Vector2 pos_difference = new Vector2(
				obj_rect.x - old_rect.x,
				obj_rect.y - old_rect.y
			);
			
			Vector2 new_obj_pos = new Vector2(
				parent_rect.x + pos_difference.x,
				parent_rect.y + pos_difference.y
			);

			
			System.out.println(obj.getClass());
			System.out.println(pos_difference);
			obj.on_parent_object_updated(parent_rect.get_position(), new_obj_pos);
			
		}
	}

	

	public Listener<GenericSceneObject, Signal> get_generic_object_listener() {
		return _listener;
	}

	@Override
	public Transform get_transform() {
		return _transform.copy();	
	}

	public void set_transform(Transform transform) {
		_transform = transform;
	}

	public LinkedList<GenericSceneObject> get_anchor_list_objects() {
		return _anchor_list;
	}

	protected void on_parent_object_updated(Vector2 parent_pos, Vector2 obj_pos) {
		set_position(obj_pos);	
	}

	@Override
	public void draw(Transform draw_transform, IDrawObject.DrawParameters params) {}
	protected void _on_object_moved(Rect old_rect) {};
	public void _on_object_destroyed() {};

	public void remove() {
		_on_object_destroyed();	
		_listener.fire_signal(Signal.ObjectRemoved);		
	}
	
	
	
}
