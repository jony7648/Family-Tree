package com.family_tree.algorithms;

import com.family_tree.math_classes.Geometry.*;

import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.structures.classes.generic.Stack;
import com.family_tree.structures.classes.generic.Queue;

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
			
			for (NArrayNode<DrawNode> iter_node : node.get_child_arr()) {
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
			System.out.println(node.get_value().get_person().get_first_name());

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

	public static void insert_node(Scene scene, NArrayNode<DrawNode> parent_node, NodeCreationWindow.SignalNodeCreated signal) {
		//plan adjust all first cousins
		//after ajusting the cousins go to the very root parent of the tree
		//adjust every node going to the left and to the right by the node gap size
		//Target: O(n)		
		
		Person new_person = signal.person();
		
		DrawNode parent_draw_node = parent_node.get_value();
		DrawNode new_draw_node = new DrawNode(new_person);
		NArrayNode<DrawNode> new_node = new NArrayNode<>(new_draw_node);

		new_draw_node.load_image(signal.image_path());

		
		if (parent_node.get_child_arr().isEmpty()) {
			new_draw_node.set_position(new Vector2(parent_draw_node.get_position().x, parent_draw_node.get_position().y + DrawNode.TOTAL_NODE_GAP_SPACE_Y));
			parent_node.add_child(new_node);
			scene.add_object(new_draw_node);	
			return;
		}

		//position odd nodes to the left even to the right
		PositionType position_type = get_position_type(parent_node);
		position_type = PositionType.Right;

		DrawNode anchor_draw_node = get_anchor_node(parent_node); 

		new_draw_node.set_y(anchor_draw_node.get_y());


		switch (position_type) {
			case Left -> new_draw_node.set_x(anchor_draw_node.get_x() - DrawNode.TOTAL_NODE_GAP_SPACE_X);
			case Right -> new_draw_node.set_x(anchor_draw_node.get_x() + DrawNode.TOTAL_NODE_GAP_SPACE_X);
		}

		parent_node.add_child(new_node);

		//position parent nodes
		NArrayNode<DrawNode> iter_node = new_node;

		while (iter_node.has_parent() && iter_node.get_parent().has_parent()) {
			iter_node = iter_node.get_parent();
			DrawNode draw_node = iter_node.get_value();

			draw_node.set_position(get_position_based_on_children(iter_node));
		}

		NArrayNode<DrawNode> root_node = iter_node.get_parent();
		System.out.println(root_node.get_value().get_person());

		//position top parent decendants
		Iterable<NArrayNode<DrawNode>> iterable;

		switch(position_type) {
			case Left -> iterable = root_node.get_child_arr();
			case Right -> iterable = root_node.get_child_arr().reversed();
			default -> throw new IllegalStateException("Something went horribly wrong");
		}
		
		for (NArrayNode<DrawNode> top_parent : iterable) {
			if (top_parent == iter_node) {
				break;
			}

			DrawNode top_draw_node = top_parent.get_value();
			DrawNode iter_draw_node = iter_node.get_value();

			int move_factor = 1;

			if (top_draw_node.get_x() < iter_draw_node.get_x()) {
				//move_factor = -1;	
			}

			for (NArrayNode<DrawNode> child : top_parent) {
				DrawNode child_draw = child.get_value();
				child_draw.set_x(child_draw.get_x() + DrawNode.TOTAL_NODE_GAP_SPACE_X * move_factor);

				if (child.is_leaf()) {
					System.out.println(child.get_value().get_person().get_first_name());
				}
			}

			top_draw_node.set_x(top_draw_node.get_x() + DrawNode.TOTAL_NODE_GAP_SPACE_X * move_factor);
		}

		scene.add_object(new_draw_node);	
	}

	private static PositionType get_position_type(NArrayNode<DrawNode> parent_node) {
		ArrayList<NArrayNode<DrawNode>> arr = parent_node.get_child_arr();
		
		//if the parent has an odd number then next should be left
		//if the parent has an even number then next number should be right
		PositionType position_type = PositionType.values()[
			(1 + arr.size()) % 2 
		];

		return position_type;
	}

	public static DrawNode get_anchor_node(NArrayNode<DrawNode> parent_node) {
		ArrayList<NArrayNode<DrawNode>> arr = parent_node.get_child_arr();

		//final int opposite_side_offset = 2;
		//arr.get((int)Math.floorMod(arr.size() - opposite_side_offset, 2)).get_value();
		
		return arr.getLast().get_value();
	}
}
