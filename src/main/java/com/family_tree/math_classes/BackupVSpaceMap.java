package com.family_tree.math_classes;

import com.family_tree.structures.classes.node_structures.LinkedList;
import com.family_tree.draw_components.interfaces.IDrawObject;
import com.family_tree.math_classes.Geometry.*;

import java.awt.*;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

public class BackupVSpaceMap {
	/*
	public static class VisualEntry {
		public class PointIterator implements Iterator<Point> {
			final int END_POINT = 4;
			private int _point = 0;

			public Point next() {
				switch (_point++) {
					case 0:
						return _top_left;
					case 1:
						return _top_right;
					case 2:
						return _bottom_right;
					case 3:
						return _bottom_left;
				}

				return null;
			}

			public boolean hasNext() {
				return _point != END_POINT;
			}
		}
		
		public class Point implements IDrawObject {
			enum Type {
				TopLeft,
				TopRight,
				BottomLeft,
				BottomRight,
			}

			Type _type = Type.TopRight;

			public Point(Type type) {
				_type = type;
			}

			public Vector2 get_pos() {
				Rect rect = _obj.get_transform().rect;	
				
				switch (_type) {
					case TopLeft:
						 return new Vector2(rect.x, rect.y);
					case TopRight:
						 return new Vector2(rect.x + rect.w, rect.y);
					case BottomLeft:
						 return new Vector2(rect.x, rect.y + rect.h);
					case BottomRight:
						 return new Vector2(rect.x + rect.w, rect.y + rect.h);
				}

				return new Vector2(0);
			}


			public Transform get_transform() {
				Vector2 pos = get_pos();	
				Transform transform = new Transform(pos.x, pos.y, 1, 1);

				return transform;
			}

			public void draw(Transform draw_transform, DrawParameters params) {
				if (should_draw() == false) {
					return;
				}

				//set_object_drawn_flag();

				Transform obj_screen_transform = _obj.to_screen_transform(params.cam_pos());

				params.painter().draw(new Line2D.Double(0, 3, 3, 3));

				_obj.draw(obj_screen_transform, params);
			}

			public VisualEntry get_visual_entry() {
				return VisualEntry.this;
			}

		}
		
		private IDrawObject _obj;
		private Point _top_left = new Point(Point.Type.TopLeft);
		private Point _top_right = new Point(Point.Type.TopRight);
		private Point _bottom_left = new Point(Point.Type.BottomLeft);
		private Point _bottom_right = new Point(Point.Type.BottomRight);
		private boolean _already_drawn = false;

		public VisualEntry(IDrawObject obj) {
			Transform trans = obj.get_transform();
			_obj = obj;
		}

		public boolean should_draw() {
			return !_already_drawn;
		}

		public IDrawObject get_object() {
			return _obj;
		}

		public void reset_draw_flag() {
			_already_drawn = false;
		}

		public Point get_bottom_left_point() {
			return _bottom_left;
		}

		public Point get_bottom_right_point() {
			return _bottom_right;
		}

		public Point get_top_right_point() {
			return _top_right;
		}

		public Point get_top_left_point() {
			return _top_left;
		}

		public void set_object_drawn_flag() {
			_already_drawn = true;
		}

		public Iterable<Point> point_iterator() {
			return () -> new PointIterator();
		}

	}
	public static class Block {
		private LinkedList<IDrawObject> _object_list = new LinkedList<>();

		public void add(VisualEntry obj) {
			for (VisualEntry.Point point : obj.point_iterator()) {
				_object_list.append(point);
				System.out.println(point.get_pos());
			}
		}

		public void remove(VisualEntry obj) {
			for (VisualEntry.Point point : obj.point_iterator()) {
				_object_list.remove(point);
			}
		}

		public void reset_obj_draw_states() {
			for (IDrawObject obj : _object_list) {
				if (obj instanceof VisualEntry.Point) {
					VisualEntry.Point point = (VisualEntry.Point)obj;
					point.get_visual_entry().reset_draw_flag();
				}
			}
		}

		public LinkedList<IDrawObject> get_object_list() {
			return _object_list;
		}

		public int get_object_count() {
			return _object_list.get_size();
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
				_BLOCK_ARR[i][j] = new Block();
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
		Rect obj_rect = obj.get_transform().rect;
		RowColumnPair pair = position_to_row_column_pair(obj_rect);

		Block block = _BLOCK_ARR[pair.row][pair.column];

		VisualEntry entry = new VisualEntry(obj);
		
		block.add(entry);
	}

	public void remove(IDrawObject obj) throws IndexOutOfBoundsException {
		Rect obj_rect = obj.get_transform().rect;
		RowColumnPair pair = position_to_row_column_pair(obj_rect);


		Block block = _BLOCK_ARR[pair.row][pair.column];
		
		VisualEntry entry = new VisualEntry(obj);
		
		block.remove(entry);

	}

	public Block access(Vector2 position) throws IndexOutOfBoundsException {
		RowColumnPair pair = position_to_row_column_pair(position);
		
		Block block = _BLOCK_ARR[pair.row][pair.column];

		//When accessing a block reset each objects draw state
		block.reset_obj_draw_states();

		return block;
	}

	public int get_block_column_count() {
		return _BLOCK_COLUMN_COUNT;
	}

	public int get_block_row_count() {
		return _BLOCK_ROW_CONUT;
	}
*/
}
