package com.family_tree.jcomponents;

import javax.swing.*;
import java.awt.*;

public class CheckBoxLabel extends JPanel implements IJComponentPair {
	public JLabel label = new JLabel("PlaceHolder");	
	public JCheckBox check_box = new JCheckBox();

	public CheckBoxLabel(String text) {
		super();

		setLayout(new GridLayout(1,2));

		add(label);
		add(check_box);
		
		label.setText(text);	
	}

	@Override public JComponent get_component1() {
		return label;
	}

	@Override public JComponent get_component2() {
		return check_box;
	}

	@Override public JPanel get_panel() {
		return this;
	}
}
