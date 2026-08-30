package com.family_tree.jcomponents;

import javax.swing.*;
import java.awt.*;

public class EntryFieldLabel extends JPanel implements IJComponentPair {
	public JLabel label = new JLabel("PlaceHolder");
	public JTextField text_field = new JTextField("PlaceHolder");

	public EntryFieldLabel(String text) {
		super();

		label.setText(text);
		add(label);
		add(text_field);
	}

	public EntryFieldLabel(String text, String label_text) {
		super();

		label.setText(text);
		text_field.setText(label_text);
		add(label);
		add(text_field);
	}
	
	@Override public JComponent get_component1() {
		return label;
	}

	@Override public JComponent get_component2() {
		return text_field;
	}
	
	@Override public JPanel get_panel() {
		return this;
	}
}
