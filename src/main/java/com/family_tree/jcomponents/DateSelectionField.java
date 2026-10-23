package com.family_tree.jcomponents;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.family_tree.data_storage.Date;
import java.time.LocalDate;

public class DateSelectionField extends JPanel {
	DefaultComboBoxModel<Integer> day_list_model = new DefaultComboBoxModel<>();
	
	JLabel name_label = new JLabel();
	EntryFieldLabel year_combo_pair = new EntryFieldLabel("Year", "2026");

	JComponentPair<JLabel, JComboBox<Date.Month>> month_combo_panel = new JComponentPair<> (
		new JLabel("Month"),
		new JComboBox<Date.Month>(Date.Month.values())
	);
	JComboBox<Date.Month> month_combo_box = month_combo_panel.component2;
	
	JComponentPair<JLabel, JComboBox<Integer>> day_combo_panel = new JComponentPair<>(
		new JLabel("Day"),
		new JComboBox<Integer>(day_list_model)
	);
	JComboBox<Integer> day_combo_box = day_combo_panel.component2;

	private boolean _leap_year_selected = false;
	int previous_year = 2026;

	public DateSelectionField(LayoutManager layout) {
		setLayout(layout);

		year_combo_pair.setLayout(new BoxLayout(year_combo_pair, BoxLayout.Y_AXIS));
		month_combo_panel.setLayout(new BoxLayout(month_combo_panel, BoxLayout.Y_AXIS));
		day_combo_panel.setLayout(new BoxLayout(day_combo_panel, BoxLayout.Y_AXIS));

		year_combo_pair.text_field.addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent e) {}

			@Override
			public void focusLost(FocusEvent e) {
				String year_text = year_combo_pair.text_field.getText().strip();
				int year = 0;

				try {
					year = Integer.parseInt(year_text);
				}
				catch (Exception error) {
					year_combo_pair.text_field.setText(Integer.valueOf(previous_year).toString());	
					return;
				}

				Date.Month month = (Date.Month)month_combo_box.getSelectedItem();

				_leap_year_selected = Date.is_leap_year(year);

				sync_day_list(month);
				previous_year = year;
			}
		});

		month_combo_box.addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent e) {}

			@Override
			public void focusLost(FocusEvent e) {
				Date.Month month = (Date.Month)month_combo_box.getSelectedItem();
				int day_count = Date.DAYS_PER_MONTH[month.ordinal()];

				sync_day_list(month);


			}
		});

		sync_day_list(Date.Month.January);

		add(year_combo_pair);
		add(month_combo_panel);
		add(day_combo_panel);

		align_components();
		
		setVisible(true);
	}

	public DateSelectionField() {
		this(new GridLayout(1,3));
	}


	private void sync_day_list(Date.Month month) {
		int list_current_size = day_list_model.getSize();

		int day_count = Date.DAYS_PER_MONTH[month.ordinal()];

		if (_leap_year_selected && month == Date.Month.February) {
			day_count += 1;
		}

		int day_difference = day_count - list_current_size;

		//prevent division by zero
		if (day_difference == 0) {
			return;
		}

		int direction_multiplier = day_difference / Math.abs(day_difference);
		day_difference = Math.abs(day_difference);

		for (int i=0; i<day_difference; i++) {
			switch (direction_multiplier) {
				case 1:
					day_list_model.addElement(list_current_size+i+1);
					break;
				case -1:
					day_list_model.removeElementAt(day_list_model.getSize()-1);
					break;
			}
		}
	}

	private void align_components() {
		//center align components
		for (Component panel_component : getComponents()) {
			if (!(panel_component instanceof JPanel)) {
				continue;
			}

			JPanel jpanel_component = (JPanel)panel_component;

			for (Component component : jpanel_component.getComponents()) {
				if (!(component instanceof JComponent)) {
					continue;
				}

				JComponent jcomponent = (JComponent)component;

				jcomponent.setAlignmentX(Component.CENTER_ALIGNMENT);
			}
		}
	}

	public LocalDate to_localdate() {
		int year = 0;
		int month = 0;
		int day = 0;

		try {
			year = Integer.parseInt(year_combo_pair.text_field.getText());
			month = (int)month_combo_box.getSelectedItem();
			day = (int)day_combo_box.getSelectedItem();
		}
		catch (Exception e) {
			System.out.println("Unable to parse date!!");
			return Date.ERROR_DATE;
		}

		return LocalDate.of(year, month, day);
	}
}
