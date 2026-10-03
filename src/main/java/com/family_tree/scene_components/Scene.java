package com.family_tree.scene_components;

import com.family_tree.math_classes.VSpaceMap;
import com.family_tree.draw_components.interfaces.IDrawObject;
import com.family_tree.math_classes.Geometry.*;
import com.family_tree.math_classes.VSpaceMap;

public class Scene {
	public static class Parameters {
		public int region_width = 0;
		public int region_height = 0;
		public int block_width = 0;
		public int block_height = 0;
	}
	
	public final int SCENE_WIDTH;
	public final int SCENE_HEIGHT;
	VSpaceMap vspacemap;

	public Scene(Parameters param) {
		SCENE_WIDTH = param.region_width;
		SCENE_HEIGHT = param.region_height;

		vspacemap = new VSpaceMap(param.region_width, param.region_height, param.block_width, param.block_height);
	}

	public void add_object(IDrawObject obj) {
		vspacemap.add(obj);	
	}

	public void remove_object(IDrawObject obj) {
		vspacemap.remove(obj);	
	}

	public int get_block_column_count() {
		return vspacemap.get_block_column_count();
	}

	public int get_block_row_count() {
		return vspacemap.get_block_row_count();
	}

	public int get_block_width() {
		return vspacemap.BLOCK_WIDTH;
	}

	public int get_block_height() {
		return vspacemap.BLOCK_HEIGHT;
	}

	public VSpaceMap.Block get_render_block(Vector2 position) {
		return vspacemap.access(position);
	}

	public VSpaceMap get_vmap() {
		return vspacemap;
	}
}
