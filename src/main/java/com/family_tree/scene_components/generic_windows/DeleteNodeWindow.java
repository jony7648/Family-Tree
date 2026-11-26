package com.family_tree.scene_components.generic_windows;

import com.family_tree.Listener.*;
import com.family_tree.jcomponents.interfaces.*;
import com.family_tree.jcomponents.*;
import javax.swing.*;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;

public class DeleteNodeWindow extends GenericDesktopPanel {
	JPanel _main_panel = new JPanel();
	JButton delete_all_button = new JButton("Delete All Decendants");
	JButton delete_parent_button = new JButton("Delete just this node");
	JButton choose_new_parents = new JButton("Choose New Parent");

	FlowLayout layout = new FlowLayout();

	public enum Signal {
		Delete
	}

	public enum DeleteType {
		DeleteAll,
		OnlyDeleteChild,
		ChooseNewParent,
	}

	public record SignalDelete (
		DeleteType type
	){}

	public Listener<DeleteNodeWindow, Signal> listener = new Listener<>(this, Signal.class);
	
	public DeleteNodeWindow(DrawPanel.PaneInfo pane_info) {
		super(pane_info);

		_main_panel.setLayout(layout);
		_main_panel.add(delete_all_button);
		_main_panel.add(delete_parent_button);

		
		panel_to_generic_window("Delete Node", _main_panel); 

		delete_all_button.addActionListener(e -> {
			SignalDelete sig_data = new SignalDelete(DeleteType.DeleteAll);
			
			listener.fire_signal(Signal.Delete, new Listener.Signal<>(this, sig_data));
		});

		delete_parent_button.addActionListener(e -> {
			SignalDelete sig_data = new SignalDelete(DeleteType.OnlyDeleteChild);
			
			listener.fire_signal(Signal.Delete, new Listener.Signal<>(this, sig_data));
		});

		choose_new_parents.addActionListener(e -> {
			SignalDelete sig_data = new SignalDelete(DeleteType.ChooseNewParent);
			
			listener.fire_signal(Signal.Delete, new Listener.Signal<>(this, sig_data));
		});
	}
}
