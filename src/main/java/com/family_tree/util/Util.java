package com.family_tree.util;

import java.util.function.BinaryOperator;

import javax.swing.*;

import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

import java.util.NoSuchElementException;


public class Util {
	public static class Pair<T1, T2> {
		public T1 value1;
		public T2 value2;
		
		public Pair(){}
		public Pair(T1 value1, T2 value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
	}

	public static class Optional<T> {
		private T _value;

		public boolean valid() {
			return _value != null;
		}

		public T value() {
			if (!valid()) {
				throw new NoSuchElementException();
			}

			return _value;
		}

		public void set_value(T value) {
			_value = value;
		}
	}
	
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

	public static String title_string(String str) {
		StringBuilder ret_str = new StringBuilder(str.toLowerCase());

		boolean capatilize_next = true;
		for (int i=0; i<str.length(); i++ ) {
			char curr_char = str.charAt(i);

			if (capatilize_next) {
				Character upper = Character.toUpperCase(curr_char);
				ret_str.replace(i,i+1, upper.toString());
				capatilize_next = false;
			}
			else if (curr_char == ' ') {
				capatilize_next = true;
			}
		}

		System.out.println(ret_str);
		return ret_str.toString();
	}

	public static Optional<File> prompt_file(String file_description, String[] file_types) { 
		//this method is used to prompt users for files from their disk


		Optional<File> optional = new Optional<>();
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG, PNG, & WEBP", "jpg", "png", "webp");
		
		chooser.setFileFilter(filter);	

		int ret_code = chooser.showOpenDialog(null);


		if (ret_code == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			System.out.println(file.getAbsolutePath());

			optional.set_value(file);
		}

		return optional;
	}


}
