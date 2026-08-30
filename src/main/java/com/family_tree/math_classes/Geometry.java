package com.family_tree.math_classes;
public class Geometry {
	public static class Vector2 {
		public float x = 0;
		public float y = 0;

		@Override public String toString() {
			return String.format("(%f, %f)", x, y);
		}

		public Vector2(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public Vector2 copy() {
			return new Vector2(x,y);
		}

		public Vector2(float xy) {
			this.x = xy;
			this.y = xy;
		}

		public Vector2 add(Vector2 vec) {
			return new Vector2(
				x + vec.x,
				y + vec.y
			);
		}

		public Vector2 multiply(Vector2 vec) {
			return new Vector2(
				x * vec.x,
				y * vec.y
			);
		}

		public Vector2 scale(float scale_value) {
			return new Vector2(
				x * scale_value,
				y * scale_value
			);
		}
	}

	

	public static class Rect {
		public float x = 0;
		public float y = 0;
		public float w = 0;
		public float h = 0;

		@Override public String toString() {
			return String.format("x: %s\ny: %s\nw: %s\nh: %s", x, y, w, h);
		}

		public Rect(float x, float y, float w, float h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public Rect copy() {
			return new Rect(x,y,w,h);
		}

		public void add_position(Vector2 position) {
			x += position.x;
			y += position.y;
		}

		public void subtract_position(Vector2 position) {
			x -= position.x;
			y -= position.y;
		}
	}

	public static class Transform {
		public Rect rect = new Rect(0,0,0,0);
		public Vector2 scale = new Vector2(1);

		public Transform(Rect rect, Vector2 scale) {
			this.rect = rect;
			this.scale = scale;
		}

		public Transform() {}

		@Override public String toString() {
			return "Rect: " + rect + "\nScale: " + scale;
		}

		public Rect get_scale_rect() {
			return new Rect(
				rect.x * scale.x,
				rect.y * scale.y,
				rect.w * scale.x,
				rect.h * scale.y
			);
		}

		public Transform copy() {
			return new Transform(rect.copy(), scale.copy());
		}
	}
}
