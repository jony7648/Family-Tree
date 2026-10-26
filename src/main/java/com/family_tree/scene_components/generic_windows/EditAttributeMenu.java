package com.family_tree.scene_components.generic_windows;

import com.family_tree.draw_objects.*;
import com.family_tree.Listener.*;

import com.family_tree.math_classes.VSpaceMap;

import com.family_tree.jcomponents.DateSelectionField;
import com.family_tree.jcomponents.interfaces.*;
import com.family_tree.data_storage.Person;
import com.family_tree.singletons.*;
import com.family_tree.jcomponents.*;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;

import java.time.LocalDate;


public class EditAttributeMenu extends GenericDesktopPanel {
	public enum Signal {
		value_edited
	}

	public record SignalValueEdited (
		Object option_value,
		String value
	) {}
	
	JPanel _main_panel = new JPanel();

	EntryFieldLabel _entry_field = new EntryFieldLabel("Value");
	JButton _confirm_button = new JButton("Confirm");

	private Object _current_attrib_option;

	public Listener<EditAttributeMenu, Signal> listener = new Listener<>(this, Signal.class); 
	
	public EditAttributeMenu(DrawPanel.PaneInfo pane_info) {
		_main_panel.add(_entry_field);	
		_main_panel.add(_confirm_button);
		
		panel_to_generic_window("Edit Attrib Menu", _main_panel, pane_info);

		_confirm_button.addActionListener(e -> {
			SignalValueEdited sig_data = new SignalValueEdited(_current_attrib_option, _entry_field.text_field.getText());
			
			listener.fire_signal(Signal.value_edited, new Listener.Signal<>(this, sig_data));
			setVisible(false);
		});
	}

	public void prompt_value_change(String old_value, Object edit_option) {
		_entry_field.text_field.setText(old_value);	
		_current_attrib_option = edit_option;	
		setVisible(true);
	}
}
