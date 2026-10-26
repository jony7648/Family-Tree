package com.family_tree.math_classes;

import com.family_tree.draw_components.interfaces.IDrawObject;
import com.family_tree.math_classes.Geometry.*;
import com.family_tree.structures.classes.node_structures.PositionalList;
import com.family_tree.draw_components.classes.Camera;

import java.awt.*;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

import java.util.Iterator;

public class VSpaceMap {
	public class ObjectIterator implements Iterator<IDrawObject> {
		int _render_rows_count = 0;
		int _render_columns_count = 0;

		int _row_start = 0;
		int _column_start = 0;
		
		int _row_index = 0;
		int _column_index = 0;

		Block _curr_block;
		final Block _TOP_LEFT_BLOCK;
		PositionalList<IDrawObject>.Node _curr_object;


		public ObjectIterator(Vector2 position, float render_width, float render_height, float scale) {
			_render_columns_count = (int)Math.ceil(1/scale * render_width / BLOCK_WIDTH);
			_render_rows_count = (int)Math.ceil(1/scale * render_height / BLOCK_HEIGHT);

			_column_start = (int)position.x / BLOCK_WIDTH;
			_row_start = (int)position.y / BLOCK_HEIGHT;
			_curr_block = _BLOCK_ARR[_row_start][_column_start];
			_curr_object = _curr_block.get_object_list().get_head_node();

			_TOP_LEFT_BLOCK = _curr_block;
		}

		public ObjectIterator(Camera camera) {
			//Vector2 render_size = camera.get_render_dimensions();
			
			this(camera.get_position(), camera.get_render_dimensions().x, camera.get_render_dimensions().y, (float)1.0);
		}


		public Block block_next() {
			int curr_row = _row_index + _row_start;
			int curr_column = _column_index + _column_start;

			if (curr_row >= _BLOCK_ROW_CONUT) {
				_curr_block = null;
				return null;	
			}
			
			Block block = _BLOCK_ARR[curr_row][curr_column];

			_column_index++;
			
			//add the molumn if we hit the last rendering column
			_row_index += _column_index/_render_columns_count;
			_column_index %= _render_columns_count;

			_curr_object = block.get_object_list().get_head_node();
			_curr_block = block;
			
			return block;
		}

		public IDrawObject next() {
			IDrawObject ret_obj = _curr_object.get_value();
			_curr_object = _curr_object.get_next();
			return ret_obj;
		}
		
		public boolean hasNext() {
			/*
			while (_curr_object == null) {
			block_next();
				if (_curr_block == null) {
					return false;
				}
			}
			*/
			
			//System.out.println(_curr_object);
			while (_curr_block != null) {
				if (_curr_object == null) {
					block_next();	
					
					continue;
				}

				Block obj_block = access(_curr_object.get_value().get_transform().rect);

				if (_curr_block == _TOP_LEFT_BLOCK) {
					break;
				}
				
				if (block_is_edge(obj_block)) {
					break;	
				}
				if (_curr_block == obj_block) {
					break;
				}
				
				_curr_object = _curr_object.get_next();
			}

			
			
			return _curr_object != null;
		}

		public boolean block_is_edge(Block obj_block) {
			//check if a block is at the very left of the screen
			boolean is_start = (_curr_block._index - _column_start) % _BLOCK_COLUMN_COUNT == 0 ||
				(_curr_block._index - _row_start*_BLOCK_COLUMN_COUNT) < _BLOCK_COLUMN_COUNT;
			
			is_start = is_start && 
				get_block_column(_curr_block) == get_block_column(obj_block) ||
				get_block_row(_curr_block) == get_block_row(obj_block);

			
			return is_start;
		}
	}
	
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
		private PositionalList<IDrawObject> _object_list = new PositionalList<>();

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

		public PositionalList<IDrawObject> get_object_list() {
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

	public int get_block_column(Block block) {
		return block._index % _BLOCK_COLUMN_COUNT;
	}

	public int get_block_row(Block block) {
		return block._index / _BLOCK_COLUMN_COUNT;
	}

	public Iterable<IDrawObject> get_elements_from_region(Vector2 position, float render_width, float render_height, float scale) {
		return () -> new ObjectIterator(position, render_width, render_height, scale);
	}

	public Iterable<IDrawObject> get_elements_from_region(Camera camera) {
		return () -> new ObjectIterator(camera);
	}
} 
