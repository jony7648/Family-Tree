package com.family_tree.jcomponents;

import javax.swing.*;
import java.awt.LayoutManager;

public class JComponentPair<T1, T2> extends JPanel implements IJComponentPair {
	public T1 component1;
	public T2 component2;
	
	public JComponentPair(T1 component1, T2 component2) {
		if (component1 instanceof JComponent && component2 instanceof JComponent) {
			this.component1 = component1;
			this.component2 = component2;

			add((JComponent)component1);
			add((JComponent)component2);
		}
	}

	public JComponentPair(LayoutManager layout, T1 component1, T2 component2) {
		if (component1 instanceof JComponent && component2 instanceof JComponent) {
			setLayout(layout);
			
			this.component1 = component1;
			this.component2 = component2;

			add((JComponent)component1);
			add((JComponent)component2);
		}
	}

	@Override
	public JComponent get_component1() {
		return (JComponent)component1;
	}

	@Override
	public JComponent get_component2() {
		return (JComponent)component2;
	}

	@Override
	public JPanel get_panel() {
		return this;
	}
	
}
