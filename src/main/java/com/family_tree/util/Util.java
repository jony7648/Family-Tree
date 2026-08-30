package com.family_tree.util;

import java.util.function.BinaryOperator;

import javax.swing.*;
import java.awt.event.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



public class Util {
	public static class InvalidJsonPropertyException extends RuntimeException {
		public InvalidJsonPropertyException(String message) {
			super(message);
		}
	}
	
	public static interface IKeyEventFunction {
		void on_event(int key_id, boolean held);
	}

	public static interface IKeyAdapaterMethod {
		void on_event(KeyEvent e, boolean held);
	}

	public static JsonNode access_json_node_property(JsonNode node, String property) throws InvalidJsonPropertyException {
		JsonNode property_node = node.get(property);

		if (property_node == null) {
			throw new InvalidJsonPropertyException(String.format("Could not access the \"%s\" property", property));
		}

		return property_node;
	}
	
	public static class InputActionGroup {
		public String group_name = "";
		public int[][] group_arr = {{}};

		public InputActionGroup(String group_name, int[][] action_group_arr) {
			group_arr = action_group_arr;
			
		}

		public void apply_actions(InputMap input_map, ActionMap action_map, Util.IKeyEventFunction meathod) {
			for (int i=0; i<group_arr.length; i++) {
				int[] group = group_arr[i];
				String action_name = group_name + Integer.valueOf(i).toString();
				String held_action = action_name + "Held";
				String released_action = action_name + "Released";

				for (int j=0; j<group.length; j++) {
					int key_event = group[j];
					KeyStroke released_stroke = KeyStroke.getKeyStroke(key_event, 0, true);
					KeyStroke held_stroke = KeyStroke.getKeyStroke(key_event, 0, false);
					
					input_map.put(released_stroke, released_action);
					input_map.put(held_stroke, held_action);
				}

				final int meathod_i = i;
				action_map.put(held_action, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						meathod.on_event(meathod_i, true);
					}
				});

				action_map.put(released_action, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						meathod.on_event(meathod_i, false);
					}
				});
			}	
		}
	}

	public static KeyListener get_key_adapater(Util.IKeyAdapaterMethod meathod) {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				meathod.on_event(e, true);	
			}

			@Override
			public void keyReleased(KeyEvent e) {
				meathod.on_event(e, false);	
			}
		};
	}


	public static<T> boolean check_generic(Class<T> type, T object) {
		return type.isInstance(object);	
	}

}
