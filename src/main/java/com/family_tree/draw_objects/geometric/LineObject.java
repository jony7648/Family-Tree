package com.family_tree.draw_objects.geometric;

import com.family_tree.draw_components.interfaces.IDrawObject;
import com.family_tree.draw_components.classes.GenericSceneObject;
import com.family_tree.draw_components.classes.*;
import com.family_tree.math_classes.Geometry.*;
import java.awt.geom.Line2D;
import java.awt.*;

public class LineObject extends GenericSceneObject implements IDrawObject {
	private boolean _v_flip = false;
	private boolean _h_flip = false;

	public LineObject(Vector2 start, Vector2 end, Vector2 scale) {
		set_scale(scale);
		set_points(start, end);
	}

	public void set_points(Vector2 p1, Vector2 p2){
		_v_flip = false;
		_h_flip = false;

		Vector2 top_p = p1;
		Vector2 left_p = p1;

		float width = Math.abs(p1.x - p2.x);
		float height = Math.abs(p1.y - p2.y);


		if (p1.y > p2.y) {
			top_p = p2;		
			_v_flip = true;
		}

		if (p1.x > p2.x) {
			left_p = p2;		
			_h_flip = true;
		}

		set_rect(left_p.x, top_p.y, width, height);
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
}
