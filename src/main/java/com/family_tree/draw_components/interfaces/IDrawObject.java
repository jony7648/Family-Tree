package com.family_tree.draw_components.interfaces;

import com.family_tree.math_classes.Geometry.*;
import java.awt.*;

public interface IDrawObject {
	public void draw(Graphics2D painter, Transform draw_transform);
	public Transform get_transform(); 
}
