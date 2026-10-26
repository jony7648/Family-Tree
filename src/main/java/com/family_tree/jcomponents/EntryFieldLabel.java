package com.family_tree.jcomponents;

import javax.swing.*;
import java.awt.*;

public class EntryFieldLabel extends JPanel implements IJComponentPair {
	public JLabel label;
	public JTextField text_field;

	final static int DEFAULT_BOX_WIDTH = 10;

	public EntryFieldLabel(String text) {
		this(text, DEFAULT_BOX_WIDTH);
	}

	public EntryFieldLabel(String label_text, int size) {
		super();

		text_field = new JTextField(size);
		label = new JLabel(label_text);

		add(label);
		add(text_field);

		text_field.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	@Override public JComponent get_component1() {
		return label;
	}

	@Override public JComponent get_component2() {
		return text_field;
	}

	public JTextField get_text_field() {
		return text_field;
	}
	
	@Override public JPanel get_panel() {
		return this;
	}
}
