package com.family_tree.jcomponents;

import javax.swing.*;

public class EnumButton<EnumType extends Enum<EnumType>> extends JButton {
	private EnumType _enum_option;

	public EnumButton(EnumType option) {
		super();
		_enum_option = option;
	}

	public EnumButton(EnumType option, String label) {
		super(label);
		_enum_option = option;
	}

	public EnumType get_enum_option() {
		return _enum_option;
	}
}
