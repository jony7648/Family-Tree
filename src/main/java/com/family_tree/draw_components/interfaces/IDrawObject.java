package com.family_tree.draw_components.interfaces;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.structures.classes.node_structures.LinkedList;
import java.awt.*;
import javax.swing.*;
import java.awt.image.ImageObserver;

public interface IDrawObject {
	public record DrawParameters (
		ImageObserver holder,
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
}
