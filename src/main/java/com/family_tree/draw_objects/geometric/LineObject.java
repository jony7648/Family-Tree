package com.family_tree.draw_objects.geometric;

import com.family_tree.draw_components.interfaces.IDrawObject;
import com.family_tree.draw_components.classes.GenericSceneObject;
import com.family_tree.draw_objects.DrawNode;
import com.family_tree.data_storage.Person;
import com.family_tree.Listener.*;
import com.family_tree.draw_components.classes.*;
import com.family_tree.math_classes.Geometry.*;
import java.awt.geom.Line2D;
import java.awt.*;

public class LineObject extends GenericSceneObject implements IDrawObject {
	private boolean _v_flip = false;
	private boolean _h_flip = false;

	private Vector2 _old_current_pos = new Vector2();
	private Vector2 _old_lock_pos = new Vector2();

	private Vector2 _parent_pos_diff = new Vector2();
	private Vector2 _lock_pos_diff = new Vector2();

	private DrawNode parent;

	public LineObject(Vector2 start, Vector2 end, Vector2 scale) {
		set_scale(scale);
		set_points(start, end);
	}

	public LineObject(Vector2 parent_pos, Vector2 line_start_offset, Vector2 end_pos, Vector2 end_pos_offset, Vector2 scale) {
		_parent_pos_diff = line_start_offset;
		_lock_pos_diff = end_pos_offset;
		Vector2 start = parent_pos.copy();
		Vector2 end = end_pos.copy();
		
		start.add(line_start_offset);
		end.add(end_pos_offset);
		set_scale(scale);
		set_points(start, end);

	}

	public LineObject(Vector2 parent_pos, Vector2 line_start_offset, DrawNode scene_object, Vector2 end_pos_offset, Vector2 scale) {
		this(parent_pos, line_start_offset, scene_object.get_position(), end_pos_offset, scale);
		scene_object.get_generic_object_listener().add_receiver(this, GenericSceneObject.Signal.TransformChanged, _on_signal_target_object_moved());
	}


	public void set_points(Vector2 current_pos, Vector2 lock_pos) {
		_old_current_pos = current_pos;
		_old_lock_pos = lock_pos;
		
		_v_flip = false;
		_h_flip = false;

		Vector2 top_p = current_pos;
		Vector2 left_p = current_pos;

		float width = Math.max(Math.abs(current_pos.x - lock_pos.x), 1);
		float height = Math.max(Math.abs(current_pos.y - lock_pos.y), 1);


		System.out.println("Width :" + width);
		System.out.println("Height :" + height);


		if (current_pos.y > lock_pos.y) {
			top_p = lock_pos;		
			_v_flip = true;
		}

		if (current_pos.x > lock_pos.x) {
			left_p = lock_pos;		
			_h_flip = true;
		}

		Math.max(0, left_p.x);
		Math.max(0, left_p.y);
		
		Rect new_rect = new Rect(left_p.x, top_p.y, width, height);
		System.out.println(new_rect);
		set_rect(new_rect);
	}
	

	public void alt_set_points(Vector2 p1, Vector2 p2) {
		_v_flip = false;
		
		Vector2 top_left = p1;
		Vector2 other_p = p2;
			
		float width = Math.abs(p1.x - p2.x);
		float height = Math.abs(p1.y - p2.y);


		if (p1.y > p2.y) {
			if (top_left.x > other_p.x) {
				top_left.x -= width;
			}
			
			other_p = p1;
			top_left = p2.copy();
		}
		else if (top_left.x > other_p.x) {
			_v_flip = true;
		}

		
		
		set_rect(top_left.x, top_left.y, width, height);
	}

	@Override
	public void draw(Transform draw_transform, IDrawObject.DrawParameters params) {
		Rect rect = draw_transform.get_scale_rect();

		Rect pos_rect = new Rect(
			rect.x, 
			rect.y, 
			rect.x + rect.w, 
			rect.y + rect.h
		);

		if (_v_flip) {
			float temp = pos_rect.y;
			pos_rect.y = pos_rect.h;
			pos_rect.h = temp;
		}
		if (_h_flip) {
			float temp = pos_rect.x;
			pos_rect.x = pos_rect.w;
			pos_rect.w = temp;
		}
		
		
		Line2D draw_line = new Line2D.Double(
			pos_rect.x,
			pos_rect.y,
			pos_rect.w,
			pos_rect.h
		);


		params.painter().setColor(Color.LIGHT_GRAY);
		params.painter().draw(draw_line);
	}

	private Vector2 pos_to_adjusted_pos(Vector2 position, Vector2 offset) {
		Vector2 new_pos = position.copy();
		new_pos.add(offset);
		return new_pos;
	}

	@Override
	protected void on_parent_object_updated(Vector2 parent_pos, Vector2 obj_position) {
		Vector2 adjusted_pos = pos_to_adjusted_pos(parent_pos, _parent_pos_diff);	
	
		set_points(adjusted_pos, _old_lock_pos);
		
	}

	private Listener.ISignalFire<GenericSceneObject> _on_signal_target_object_moved() {
		return (emitter, sig) -> {
			if (!(sig.sig_data instanceof GenericSceneObject.SignalTransformChanged)) {
				return;
			}

			GenericSceneObject.SignalTransformChanged signal = (GenericSceneObject.SignalTransformChanged)sig.sig_data;

			Vector2 new_lock_pos = signal.tranfsorm().get_position();
			
			
			Vector2 adjusted_pos = pos_to_adjusted_pos(new_lock_pos, _lock_pos_diff);	
			
			set_points(_old_current_pos, adjusted_pos);
		};
	}
}
