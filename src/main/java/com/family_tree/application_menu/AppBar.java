package com.family_tree.application_menu;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;


import com.family_tree.jcomponents.DrawPanel;

public class AppBar {
	public interface MenuItemAction<EnumType extends Enum<EnumType>> {
		void item_clicked(ActionEvent e, EnumType option);
	}

	public static class EnumMenuItem<EnumType extends Enum<EnumType>> extends JMenuItem {
		private EnumType _enum_value;

		public EnumMenuItem(String item_text, EnumType value) {
			super(item_text);
			_enum_value = value;	
		}

		public EnumType get_enum_value() {
			return _enum_value;
		}
	}
	
	

	public static <EnumType extends Enum<EnumType>> void create_menu_items(JMenu menu, Class<EnumType> options, MenuItemAction<EnumType> menu_action) {
		EnumType[] option_arr = options.getEnumConstants();
		
		for (int i=0; i<option_arr.length; i++) {
			EnumType option = option_arr[i];
			
			EnumMenuItem<EnumType> item = new EnumMenuItem<>(option.toString(), option);
			menu.add(item);

			item.addActionListener(e -> {
				menu_action.item_clicked(e, option);
			});
		}
	}

	
}
