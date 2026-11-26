package com.family_tree.algorithms;

import com.family_tree.math_classes.Geometry.*;

import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.structures.classes.generic.Stack;
import com.family_tree.structures.classes.generic.Queue;
import com.family_tree.util.Iterators;

import com.family_tree.draw_objects.*;
import com.family_tree.draw_objects.geometric.*;
import com.family_tree.scene_components.generic_windows.*;
import com.family_tree.scene_components.*;
import com.family_tree.data_storage.*;
import java.util.ArrayList;
import java.nio.file.Path;

public class TreeAligner {
	enum PositionType {
		Left,
		Right
	}

	public static void position_leafs(NArrayNode<DrawNode> node, Queue<NArrayNode<DrawNode>> leaf_queue, Stack<NArrayNode<DrawNode>> parent_stack, int y_pos) {
		y_pos += DrawNode.TOTAL_NODE_GAP_SPACE_Y;


		DrawNode draw_node = node.get_value();

		draw_node.set_y(y_pos);
		
		if (node.has_children()) {
			parent_stack.push(node);
			
			for (NArrayNode<DrawNode> iter_node : node.get_children()) {
				position_leafs(iter_node, leaf_queue, parent_stack, y_pos);
			}

			return;
		}

		leaf_queue.add(node);
		
	}
	
	public static void setup_draw_nodes(Scene scene, NArrayNode<DrawNode> draw_root_node) {
		Queue<NArrayNode<DrawNode>> leaf_queue = new Queue<>();
		Stack<NArrayNode<DrawNode>> parent_stack = new Stack<>();

		int bottom_depth = 0;

		//bfs from the right so we can add leafs from the left

		position_leafs(draw_root_node, leaf_queue, parent_stack, 0);
	

		
		//iterate through each leaf from left to right
		int depth = bottom_depth;
		for (int i=0; leaf_queue.is_empty() == false;) {
			NArrayNode<DrawNode> node = leaf_queue.remove_front();	
			DrawNode draw_node = node.get_value();	

			if (depth != draw_node.get_position().y) {
				depth = (int)draw_node.get_position().y;
			}

			//add 0.1 so 0 will also have a width of 1

			draw_node.set_position(new Vector2(i * DrawNode.TOTAL_NODE_GAP_SPACE_X, draw_node.get_position().y));
			i++;
			scene.add_object(node.get_value());

		}

		while (parent_stack.is_empty() == false) {
			NArrayNode<DrawNode> node = parent_stack.pop();
			DrawNode draw_node = node.get_value();

			if (node == draw_root_node) {
				break;
			}

			draw_node.set_position(get_position_based_on_children(node));

			//camera.add_draw_object(draw_node);
			scene.add_object(draw_node);	

			//add the lines connecting to the children
			for (NArrayNode<DrawNode> child : node.get_children()) {
				LineObject line = draw_node.add_child_node(child.get_value());

				scene.add_object(line);
			}
		}


		draw_root_node.get_value().set_position(get_position_based_on_children(draw_root_node));

		scene.add_object(draw_root_node.get_value());
	}

	private static Vector2 get_position_based_on_children(NArrayNode<DrawNode> node) {
		DrawNode left_draw_node = node.get_first().get_value();
		DrawNode right_draw_node = node.get_last().get_value();

		return new Vector2 (
			left_draw_node.get_position().x - DrawNode.NODE_WIDTH / 2 + (right_draw_node.get_position().x + DrawNode.NODE_WIDTH - left_draw_node.get_position().x) / 2,
			left_draw_node.get_position().y - DrawNode.TOTAL_NODE_GAP_SPACE_Y
		);
	}

	public static void insert_node(Scene scene, NArrayNode<DrawNode> parent_node, NodeCreationWindow.SignalNodeCreated signal) {
		//adjust all first cousins
		//after ajusting the cousins go to the very root parent of the tree
		//adjust every node going to the left and to the right by the node gap size
		//Target: O(n)		
		
		Person new_person = signal.person();
		
		DrawNode parent_draw_node = parent_node.get_value();
		DrawNode new_draw_node = new DrawNode(new_person);
		NArrayNode<DrawNode> new_node = new NArrayNode<>(new_draw_node);

		final int X_ADJUST_DISTANCE = DrawNode.TOTAL_NODE_GAP_SPACE_X;

		new_draw_node.load_image(signal.image_path());

		
		if (parent_node.is_leaf()) {
			new_draw_node.set_position(new Vector2(parent_draw_node.get_position().x, parent_draw_node.get_position().y + DrawNode.TOTAL_NODE_GAP_SPACE_Y));
			parent_node.add_child(new_node);
			
			LineObject new_line_object = parent_draw_node.add_child_node(new_draw_node);
			scene.add_object(new_line_object);

			scene.add_object(new_draw_node);	
			scene.add_object(new_line_object);	
			return;
		}

		DrawNode anchor_draw_node = get_anchor_node(parent_node); 

		new_draw_node.set_y(anchor_draw_node.get_y());

		new_draw_node.set_x(anchor_draw_node.get_x() + X_ADJUST_DISTANCE);

		parent_node.add_child(new_node);
		LineObject new_line_object = parent_draw_node.add_child_node(new_draw_node);

		scene.add_object(new_line_object);
		scene.add_object(new_draw_node);	




		//adjust siblings of new nodes parent
		for (NArrayNode<DrawNode> sibling : parent_node.get_parent().get_reversed_children()) {
			if (sibling == parent_node) {
				break;
			}

			DrawNode sibling_draw_node = sibling.get_value();

			sibling_draw_node.adjust_x(X_ADJUST_DISTANCE);
		}


		//recenter ancestors
		NArrayNode<DrawNode> top_node = adjust_nodes(new_node, X_ADJUST_DISTANCE);		
		recenter_ancestors(new_node);		
		NArrayNode<DrawNode> root_node = top_node.get_parent();

	


		//
		// //position the right siblings of the top level node
		// for (NArrayNode<DrawNode> iter_node : root_node.get_child_arr().reversed()) {
		// 	if (top_node == iter_node) {
		// 		break;
		// 	}
		//
		// 	DrawNode top_draw_node = top_node.get_value();
		//
		// 	for (NArrayNode<DrawNode> child : iter_node) {
		// 		DrawNode child_draw = child.get_value();
		// 		child_draw.adjust_x(X_ADJUST_DISTANCE);
		// 		if (child.has_children()) {
		//
		// 		}
		// 	}
		//
		// 	top_draw_node.adjust_x(X_ADJUST_DISTANCE);
		// }
	}

	public static void delete_node(DrawNode delete_node) {
		NArrayNode<DrawNode> delete_narray_node = delete_node.get_narray_node();						
		NArrayNode<DrawNode> delete_node_parent = delete_narray_node.get_parent();

		final int X_ADJUST_DISTANCE = -DrawNode.TOTAL_NODE_GAP_SPACE_X;

		//adjust deleted nodes siblings
		for (NArrayNode<DrawNode> node : delete_node_parent.get_reversed_children()) {
			DrawNode draw_node = node.get_value();	

			if (draw_node == delete_node) {
				break;
			}

			draw_node.adjust_x(X_ADJUST_DISTANCE);
		}

		//delete the narraynode from the parent
		delete_node_parent.remove(delete_node);

		adjust_nodes(delete_narray_node, X_ADJUST_DISTANCE);
		recenter_ancestors(delete_node_parent.get_first());
	}

	private static NArrayNode<DrawNode> recenter_ancestors(NArrayNode<DrawNode> new_node) {
		//position parent nodes
		NArrayNode<DrawNode> iter_node = new_node.get_parent();

		while (iter_node.has_parent()) {
			DrawNode draw_node = iter_node.get_value();

			Vector2 new_position = get_position_based_on_children(iter_node);

			draw_node.set_position(new_position);
			iter_node = iter_node.get_parent();
		}

		System.out.println("We are done");

		return iter_node;
	}

	private static NArrayNode<DrawNode> adjust_nodes(NArrayNode<DrawNode> new_node, int adjust_ammount) {
		NArrayNode<DrawNode> iter_node = new_node;	


		//if this is the very first node

		while (iter_node.has_parent()) {
			NArrayNode<DrawNode> parent = iter_node.get_parent();
			Iterable<NArrayNode<DrawNode>> iterable;


			if (adjust_ammount >= 0) {
				NArrayNode<DrawNode> next_node = parent.get_next_child(new_node);
				iterable = parent.get_children(next_node);
			}
			else {
				NArrayNode<DrawNode> prev_node = parent.get_prev_child(new_node);
				iterable = parent.get_children(prev_node);
			}
			
			for (NArrayNode<DrawNode> sub_tree : iterable) {
				if (sub_tree == iter_node) {
					break;	
				}
				
				for (NArrayNode<DrawNode> child : sub_tree) {
					DrawNode child_draw_node = child.get_value();

					child_draw_node.adjust_x(adjust_ammount);
				}
			}


			iter_node = iter_node.get_parent();
		}

		return iter_node;
	}

	public static DrawNode get_anchor_node(NArrayNode<DrawNode> parent_node) {
		return parent_node.get_last().get_value();
	}
	
	
}
