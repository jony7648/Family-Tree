package com.family_tree.draw_components.classes;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.draw_components.interfaces.IDrawObject;
import com.family_tree.draw_objects.*;
import com.family_tree.math_classes.VSpaceMap;

import com.family_tree.scene_components.*;
import com.family_tree.util.Util.Pair;

import java.awt.*;
import javax.swing.*;
import java.lang.Math;

public class Camera {
	public final static float MAX_ZOOM = 2.0f;
	public final static float MIN_ZOOM = 0.1f;
	public final static float ZOOM_FACTOR = 0.1f;
	public final static int BASE_MOVEMENT_SPEED = 50;
	
	private Vector2 _position = new Vector2(0);
	private float _scale = 1;
	private DefaultListModel<IDrawObject> draw_list = new DefaultListModel<>();

	private Scene _render_scene;

	private int _win_height = 0;
	private int _win_width = 0;

	private int _block_columns = 0;
	private int _block_rows = 0;

	private int _objects_rendered = 0;

	public Camera(Scene render_scene, int panel_width, int panel_height) {
		_render_scene = render_scene;

		update_win_res(panel_width, panel_height);
	}


	public void on_draw(Graphics2D painter) {
		_objects_rendered = 0;
		
		for (int row_index=0; row_index<_block_rows; row_index++) {
			for(int column_index=0; column_index<_block_columns; column_index++) {
				Vector2 pos = new Vector2(
					_position.x + column_index * _render_scene.get_block_width(),
					_position.y + row_index * _render_scene.get_block_height()
				);

				//VSpaceMap.Block block = _render_scene.get_render_block(pos);
				VSpaceMap.Block block = _render_scene.get_render_block(pos);

				
				for (IDrawObject obj : block.get_object_list()) {
					Transform object_transform = obj.get_transform().copy();

					object_transform.rect.subtract_position(_position);
					object_transform.scale = new Vector2(_scale, _scale);

					IDrawObject.DrawParameters params = new IDrawObject.DrawParameters(painter, _position);

					obj.draw(object_transform, params);
					_objects_rendered++;
				}
			}
		}
	}

	public void move(Vector2 move_vec) {
		float movement_speed = BASE_MOVEMENT_SPEED * 2 * (MAX_ZOOM - _scale);

		move_vec.scale(movement_speed);

				
		_position.add(move_vec);

		_position.x = Math.clamp(_position.x, 0, _render_scene.SCENE_WIDTH);
		_position.y = Math.clamp(_position.y, 0, _render_scene.SCENE_HEIGHT);

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

	public void set_render_scene(Scene render_scene) {
		_render_scene = render_scene;
	}

	public void update_win_res(int width, int height) {
		_win_width = width;
		_win_height = height;
		
		

		_block_columns = (int)Math.ceilDiv(width, _render_scene.get_block_width());
		_block_rows = (int)Math.ceilDiv(height, _render_scene.get_block_height());

		System.out.println("camera rows " + _block_rows);
		System.out.println("camera columns " + _block_columns);

	}
	
	public Scene get_render_scene() {
		return _render_scene;
	}

	public Vector2 get_position() {
		return _position;
	}

	public int get_rendered_objects_count() {
		return _objects_rendered;
	}
}
