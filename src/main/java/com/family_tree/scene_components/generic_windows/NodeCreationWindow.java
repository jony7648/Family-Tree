package com.family_tree.scene_components.generic_windows;

import com.family_tree.draw_objects.*;
import com.family_tree.Listener.*;

import com.family_tree.math_classes.VSpaceMap;

import com.family_tree.jcomponents.DateSelectionField;
import com.family_tree.jcomponents.interfaces.*;
import com.family_tree.data_storage.Person;
import com.family_tree.singletons.*;
import com.family_tree.jcomponents.*;
import com.family_tree.util.*;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;

import java.time.LocalDate;
import java.io.File;



public class NodeCreationWindow extends GenericDesktopPanel {
	enum Signal {
		NodeCreated
	}
	
	public record SignalNodeCreated (
		DrawNode drawnode
	) {}


		
	final String WIN_NAME = "Node Creation Window";

	public Listener<NodeCreationWindow, Signal> listener = new Listener<>(this, Signal.class);
		
	private JPanel _main_panel = new JPanel();
	private DrawPanel.PaneInfo _pane_info;

	private Dimension _window_bounds = new Dimension(300, 600);
		
	
	private EntryFieldLabel _first_name_pair = new EntryFieldLabel("First Name");
	private EntryFieldLabel _last_name_pair = new EntryFieldLabel("Last Name");
	private JComponentPair<JLabel, JComboBox<Person.Gender>> _gender_field = new JComponentPair<>(new JLabel("Person"), new JComboBox<>(Person.Gender.values()));
	private JComponentPair<JLabel, JComboBox<String>> _orgin_country_field = new JComponentPair<>(new JLabel("Country"), new JComboBox<String>(GeoInfo.get_country_arr()));
	private EntryFieldLabel _orgin_province_field = new EntryFieldLabel("Province");

	private JComponentPair<JLabel, DateSelectionField> _birth_day_start_panel = new JComponentPair<> (
		new GridLayout(2,1),
		new JLabel("Birth Date Start"),
		new DateSelectionField()
	);

	private JComponentPair<JLabel, DateSelectionField> _birth_day_end_panel = new JComponentPair<> (
		new GridLayout(2,1),
		new JLabel("Birth Date End"),
		new DateSelectionField()
	);

	private JButton _load_image_button = new JButton("Select photo from disk");
	
	private JButton _confirm_search_button = new JButton("Create!");

	JComponent left_align_components[] = {
		_first_name_pair,
		_last_name_pair,
		_gender_field,
		_birth_day_start_panel,
		_birth_day_end_panel,
		_orgin_country_field,
		_orgin_province_field,
		_confirm_search_button
	};

	public NodeCreationWindow(DrawPanel.PaneInfo pane_info) {
		_pane_info = pane_info;

		//setUndecorated(true);
		//setAlwaysOnTop(true);
		_main_panel.setLayout(new BoxLayout(_main_panel, BoxLayout.Y_AXIS));

		_main_panel.add(_first_name_pair);
		_main_panel.add(_last_name_pair);
		_main_panel.add(_gender_field);
		_main_panel.add(_birth_day_start_panel);
		_main_panel.add(_birth_day_end_panel);
		_main_panel.add(_orgin_country_field);
		_main_panel.add(_orgin_province_field);
		_main_panel.add(_orgin_province_field);
		_main_panel.add(_load_image_button);
		_main_panel.add(_confirm_search_button);

		_load_image_button.addActionListener(e -> {
			String[] file_types = {"jpg", "png", "webp"};
			
			Util.Optional<File> image_file = Util.prompt_file("JPG, PNG, & WEBP", file_types);
		});

		_confirm_search_button.addActionListener(e -> {
			String first_name = _first_name_pair.get_text_field().getText();
			String last_name = _last_name_pair.get_text_field().getText();
			Person.Gender gender = (Person.Gender)_gender_field.component2.getSelectedItem();
			LocalDate start_birth_date = _birth_day_start_panel.component2.to_localdate();
			LocalDate end_birth_date = _birth_day_start_panel.component2.to_localdate();
			LocalDate start_pass_date = null;
			LocalDate end_pass_date = null;
			String orgin_country = (String)_orgin_country_field.component2.getSelectedItem();
			String orgin_province = _orgin_province_field.text_field.getText();
		});



		panel_to_generic_window(WIN_NAME, _main_panel, pane_info);


		
		 _first_name_pair.setAlignmentX(Component.LEFT_ALIGNMENT);
		 _last_name_pair.setAlignmentX(Component.LEFT_ALIGNMENT);
		 _gender_field.setAlignmentX(Component.LEFT_ALIGNMENT);
		 _birth_day_start_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		 _birth_day_end_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		 _orgin_country_field.setAlignmentX(Component.LEFT_ALIGNMENT);
		 _orgin_province_field.setAlignmentX(Component.LEFT_ALIGNMENT);
		 _orgin_province_field.setAlignmentX(Component.LEFT_ALIGNMENT);
		 _confirm_search_button.setAlignmentX(Component.LEFT_ALIGNMENT);
	}
	
	@Override
	protected void paintComponent(Graphics BaseG) {
		Graphics2D g = (Graphics2D)BaseG;
		
		g.setColor(new Color(255, 255, 255));
		g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
	}
}
