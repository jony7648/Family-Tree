package com.family_tree.algorithms;

import com.family_tree.math_classes.Geometry.*;

import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.structures.classes.generic.Stack;

import com.family_tree.draw_objects.*;
import com.family_tree.draw_objects.geometric.*;
import com.family_tree.scene_components.*;


public class TreeAligner {
	public static void setup_draw_nodes(Scene scene, NArrayNode<DrawNode> draw_root_node) {
		Stack<NArrayNode<DrawNode>> leaf_stack = new Stack<>();
		Stack<NArrayNode<DrawNode>> parent_stack = new Stack<>();

		int bottom_depth = 0;


		//bfs from the right so we can add leafs from the left
		for (NArrayNode<DrawNode> node : draw_root_node.from_right_bfs()) {
			DrawNode draw_node = node.get_value();
			Transform parent_transform = node.get_parent().get_value().get_transform();
			float depth = parent_transform.rect.y + DrawNode.TOTAL_NODE_GAP_SPACE_Y;
			
			//child leaf node will position the parents
			draw_node.set_position(new Vector2(-600, depth));

			if (node.is_leaf()) {
				leaf_stack.push(node);	
			}
			else {
				parent_stack.push(node);	
			}

			bottom_depth = (int)depth;
		}


		//we already have the nodes in a stack so a reverse BFS is easy

		int depth = bottom_depth;

		int iter_x = 0;

		while (leaf_stack.is_empty() == false) {
			NArrayNode<DrawNode> node = leaf_stack.pop();	
			DrawNode draw_node = node.get_value();	

			if (depth != draw_node.get_position().y) {
				depth = (int)draw_node.get_position().y;

			}

			//add 0.1 so 0 will also have a width of 1

			draw_node.set_position(new Vector2(iter_x * DrawNode.TOTAL_NODE_GAP_SPACE_X, draw_node.get_position().y));
			iter_x++;
			scene.add_object(node.get_value());

		}


		while (parent_stack.is_empty() == false) {
			NArrayNode<DrawNode> node = parent_stack.pop();
			DrawNode draw_node = node.get_value();

			draw_node.set_position(get_position_based_on_children(node));

			//camera.add_draw_object(draw_node);
			scene.add_object(draw_node);	

			//add the lines connecting to the children
			for (NArrayNode<DrawNode> child : node.get_child_arr()) {
				LineObject line = draw_node.add_child_node(child.get_value());

				scene.add_object(line);
			}
		}


		draw_root_node.get_value().set_position(get_position_based_on_children(draw_root_node));

		scene.add_object(draw_root_node.get_value());
	}

	private static Vector2 get_position_based_on_children(NArrayNode<DrawNode> node) {
		DrawNode left_draw_node = node.get_child_arr().getLast().get_value();
		DrawNode right_draw_node = node.get_child_arr().getFirst().get_value();

		return new Vector2 (
			left_draw_node.get_position().x - DrawNode.NODE_WIDTH / 2 + (right_draw_node.get_position().x + DrawNode.NODE_WIDTH - left_draw_node.get_position().x) / 2,
			left_draw_node.get_position().y - DrawNode.TOTAL_NODE_GAP_SPACE_Y
		);
	}
}
