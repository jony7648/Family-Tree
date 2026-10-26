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
import java.awt.event.*;

import java.time.LocalDate;


public class SearchPanel extends GenericDesktopPanel {
	public enum Signal {
		NodeSearched
	}

	public enum SearchMode {
		Exact,
		Fuzzy,
	}

	public record SignalSearchInfo (
		SearchMode search_mode,
		String first_name,
		String last_name,
		Person.Gender gender,
		LocalDate start_birth_date,
		LocalDate end_birth_date,
		LocalDate start_pass_date,
		LocalDate end_pass_date,
		String orgin_country,
		String orgin_province
	) {}
	
	final String WIN_NAME = "Search Panel";

	public Listener<SearchPanel, Signal> listener = new Listener<>(this, Signal.class);
		
	private JPanel _main_panel = new JPanel();
	private DrawPanel.PaneInfo _pane_info;

	private Dimension _window_bounds = new Dimension(300, 600);
		
	
	private EntryFieldLabel _first_name_pair = new EntryFieldLabel("First Name");
	private EntryFieldLabel _last_name_pair = new EntryFieldLabel("Last Name");
	private JComponentPair<JLabel, JComboBox<Person.Gender>> _gender_field = new JComponentPair<>(new JLabel("Person"), new JComboBox<>(Person.Gender.values()));
	private JComponentPair<JLabel, JComboBox<String>> _orgin_country_field = new JComponentPair<>(new JLabel("Country"), new JComboBox<String>(GeoInfo.get_country_arr()));
	private EntryFieldLabel _orgin_province_field = new EntryFieldLabel("Province");

	public JComponentPair<JLabel, DateSelectionField> _birth_day_start_panel = new JComponentPair<> (
		new GridLayout(2,1),
		new JLabel("Birth Date Start"),
		new DateSelectionField()
	);

	public JComponentPair<JLabel, DateSelectionField> _birth_day_end_panel = new JComponentPair<> (
		new GridLayout(2,1),
		new JLabel("Birth Date End"),
		new DateSelectionField()
	);
	
	JComponentPair<EnumButton<SearchMode>, EnumButton<SearchMode>> _confirm_search_button_pair = new JComponentPair<>( 
		new EnumButton<>(SearchMode.Exact, "Exact"),
		new EnumButton<>(SearchMode.Fuzzy, "Fuzzy")
	);
	
	public SearchPanel(DrawPanel.PaneInfo pane_info) {
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
		_main_panel.add(_confirm_search_button_pair.component1);
		_main_panel.add(_confirm_search_button_pair.component2);
		
		ActionListener search_button_action = e -> {
			String first_name = _first_name_pair.get_text_field().getText();
			String last_name = _last_name_pair.get_text_field().getText();
			Person.Gender gender = (Person.Gender)_gender_field.component2.getSelectedItem();
			LocalDate start_birth_date = _birth_day_start_panel.component2.to_localdate();
			LocalDate end_birth_date = _birth_day_start_panel.component2.to_localdate();
			LocalDate start_pass_date = null;
			LocalDate end_pass_date = null;
			String orgin_country = (String)_orgin_country_field.component2.getSelectedItem();
			String orgin_province = _orgin_province_field.text_field.getText();
			
			EnumButton<SearchMode> button = (EnumButton<SearchMode>)e.getSource();
			
			Listener.Signal<SearchPanel> signal = new Listener.Signal<SearchPanel>(this, 
				new SignalSearchInfo(button.get_enum_option(),
					first_name, last_name, gender, start_birth_date, 
					end_birth_date, start_pass_date, 
					end_pass_date, orgin_country, orgin_province
				)
			);

			listener.fire_signal(Signal.NodeSearched, signal);
		};

		_confirm_search_button_pair.component1.addActionListener(search_button_action);
		_confirm_search_button_pair.component2.addActionListener(search_button_action);


		_first_name_pair.setAlignmentX(Component.LEFT_ALIGNMENT);
		_last_name_pair.setAlignmentX(Component.LEFT_ALIGNMENT);
		_gender_field.setAlignmentX(Component.LEFT_ALIGNMENT);
		_birth_day_start_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		_birth_day_end_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		_orgin_country_field.setAlignmentX(Component.LEFT_ALIGNMENT);
		_orgin_province_field.setAlignmentX(Component.LEFT_ALIGNMENT);
		_orgin_province_field.setAlignmentX(Component.LEFT_ALIGNMENT);
		_confirm_search_button_pair.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel_to_generic_window(WIN_NAME, _main_panel, pane_info);
	}

	@Override
	protected void paintComponent(Graphics BaseG) {
		Graphics2D g = (Graphics2D)BaseG;
		
		g.setColor(new Color(255, 255, 255));
		g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
	}
}
