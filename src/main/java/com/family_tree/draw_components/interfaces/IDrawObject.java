package com.family_tree.draw_components.interfaces;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.structures.classes.node_structures.LinkedList;
import java.awt.*;

public interface IDrawObject {
	public record DrawParameters (
		Graphics2D painter,
		Vector2 cam_pos
	) {};
	
	public void draw(Transform draw_transform, DrawParameters draw_parameters);
	public void set_position(Vector2 position); 
	public Transform get_transform(); 

	default public Transform to_screen_transform(Vector2 cam_pos) {
		Transform new_transform = get_transform().copy();

		new_transform.rect.subtract_position(cam_pos);

		return new_transform;
	}

	default public void update_anchor_objects(LinkedList<IDrawObject> anchor_list, Vector2 new_parent_pos) {
		Rect parent_rect = get_transform().rect;
		
		for (IDrawObject obj : anchor_list) { 
			Rect obj_rect = obj.get_transform().rect;
			
			Vector2 pos_difference = new Vector2(
				obj_rect.x - parent_rect.x,
				obj_rect.y - parent_rect.y
			);
			
			Vector2 new_obj_pos = new Vector2(
				new_parent_pos.x + pos_difference.x,
				new_parent_pos.y + pos_difference.y
			);
			
			obj.set_position(new_obj_pos);
		}
	}
}
