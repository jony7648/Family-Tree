package com.family_tree.math_classes;

import com.family_tree.structures.classes.node_structures.LinkedList;
import com.family_tree.draw_components.interfaces.IDrawObject;
import com.family_tree.math_classes.Geometry.*;

import java.awt.*;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

public class VSpaceMap {
	public static class RenderedObj {
		private IDrawObject _obj;
		public boolean _already_drawn = false;

		public RenderedObj(IDrawObject obj) {
			_obj = obj;	
		}

		public boolean should_draw() {
			return !_already_drawn;
		}

		public void set_drawn_flag() {
			_already_drawn = true;
		}
		
		public void reset_draw_flag() {
			_already_drawn = false;
		}

		public IDrawObject get_obj() {
			return _obj;
		}
	}
	
	public static class Block {
		private LinkedList<IDrawObject> _object_list = new LinkedList<>();

		int _index = 0;

		public Block(int index) {
			_index = index;
		}

		public void add(IDrawObject obj) {
			_object_list.append(obj);
		}

		public void remove(IDrawObject obj) {
			_object_list.remove(obj);
		}

		public LinkedList<IDrawObject> get_object_list() {
			return _object_list;
		}

		public int get_object_count() {
			return _object_list.get_size();
		}

		public int get_index() {
			return _index;
		}
	}

	public static class RowColumnPair {
		public int row = 0;
		public int column = 0;

		public String toString() {
			return String.format("(%d, %d)", row, column);
		}
	}
	
	private final Block _BLOCK_ARR[][];	
	public final int BLOCK_WIDTH;
	public final int BLOCK_HEIGHT;
	private int _REGION_WIDTH;
	private int _REGION_HEIGHT;


	private final int _BLOCK_ROW_CONUT;
	private final int _BLOCK_COLUMN_COUNT;


	public VSpaceMap(int region_x, int region_y, int block_width, int block_height) {
		BLOCK_WIDTH = block_width;
		BLOCK_HEIGHT = block_height;
		
		_REGION_WIDTH = region_x;
		_REGION_HEIGHT = region_y;
		
		_BLOCK_ROW_CONUT = (int)Math.ceil(region_y/block_height);
		_BLOCK_COLUMN_COUNT = (int)Math.ceil(region_x/block_width);

		_BLOCK_ARR = new Block[_BLOCK_ROW_CONUT][_BLOCK_COLUMN_COUNT];

		//initialize the block_arr
		for (int i=0; i<_BLOCK_ROW_CONUT; i++) {
			for (int j=0; j<_BLOCK_COLUMN_COUNT; j++) {
				int block_index = i*_BLOCK_COLUMN_COUNT + j%_BLOCK_COLUMN_COUNT;
				Block block = new Block(block_index);
				_BLOCK_ARR[i][j] = block;
			}
		}

	
	}

	public RowColumnPair position_to_row_column_pair(I2DSpace space) throws IndexOutOfBoundsException {
		//method takes position and converts it to a block row and column
		
		RowColumnPair pair = new RowColumnPair();

		pair.row = (int)Math.floor(space.get_y() / BLOCK_HEIGHT);
		pair.column = (int)Math.floor(space.get_x() / BLOCK_WIDTH);


		if (space.get_y() > _REGION_HEIGHT || space.get_y() < 0) {
			throw new IndexOutOfBoundsException("VSPACEMAP: is out of bounds of the vertical map!");
		}

		if (space.get_x() > _REGION_WIDTH || space.get_x() < 0) {
			throw new IndexOutOfBoundsException("VSPACEMAP: is out of bounds of the horizontal map!");
		}

		return pair;	
	}

	public void add(IDrawObject obj) throws IndexOutOfBoundsException {
		Transform transform = obj.get_transform();

		int x_block_count = get_obj_occupnacy_count(transform.rect.x, transform.rect.w, transform.scale.x);
		int y_block_count = get_obj_occupnacy_count(transform.rect.y, transform.rect.h, transform.scale.y);

		//System.out.println("Block Count: " + x_block_count*y_block_count);

		//System.out.println(transform);

		for (int x_index=0; x_index<x_block_count; x_index++) {
			Rect rect = transform.get_scale_rect();
			rect.x += x_index * BLOCK_WIDTH;
			
			for (int y_index=0; y_index<y_block_count; y_index++) {
				rect.y += y_index * BLOCK_HEIGHT;
				Block block = access(rect);

				block.add(obj);
			}
		}
	}

	public void remove(IDrawObject obj) throws IndexOutOfBoundsException {
		Transform transform = obj.get_transform();

		int x_block_count = get_obj_occupnacy_count(transform.rect.x, transform.rect.w, transform.scale.x);
		int y_block_count = get_obj_occupnacy_count(transform.rect.y, transform.rect.h, transform.scale.y);


		for (int x_index=0; x_index<x_block_count; x_index++) {
			Rect rect = transform.get_scale_rect();
			rect.x += x_index * BLOCK_WIDTH;
			
			for (int y_index=0; y_index<y_block_count; y_index++) {
				
				rect.y += y_index * BLOCK_HEIGHT;
				Block block = access(rect);

				block.add(obj);
			}
		}
	}

	public Block access(I2DSpace space) throws IndexOutOfBoundsException {
		RowColumnPair pair = position_to_row_column_pair(space);
		
		Block block = _BLOCK_ARR[pair.row][pair.column];

		//When accessing a block reset each objects draw state
		return block;
	}

	public int get_obj_occupnacy_count(float pos, float size, float scale) {

		//we don't want objects that are take up an entire block to be
		//rendered in two blocks, so an offset is used
		float OFFSET = 0.1f; 		
		float scaled_pos = pos * scale;
		float scaled_size = size * scale;

		
		int block_count = 1 + (int)Math.floor(
			(scaled_pos % BLOCK_WIDTH + scaled_size - OFFSET) / BLOCK_WIDTH
		);

		return block_count;
	}

	public int get_block_column_count() {
		return _BLOCK_COLUMN_COUNT;
	}

	public int get_block_row_count() {
		return _BLOCK_ROW_CONUT;
	}
} 
