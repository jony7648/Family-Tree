package com.family_tree.panels;

import com.family_tree.util.*;

import com.family_tree.math_classes.Geometry.*;

import com.family_tree.data_storage.*;
import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.structures.classes.generic.Stack;
import com.family_tree.algorithms.Search;
import com.family_tree.Listener.*;
import com.family_tree.serialized_structures.*;

import com.family_tree.draw_objects.*;
import com.family_tree.draw_objects.geometric.*;
import com.family_tree.draw_components.classes.*;
import com.family_tree.scene_components.*;

import com.family_tree.jcomponents.*;

import java.awt.LayoutManager;
import java.awt.GridLayout;

import java.time.LocalDate;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


class NodeWidthHolder {
	public NArrayNode<DrawNode> node;
	public int width = 0;
	public int depth = 0;

	public NodeWidthHolder(NArrayNode<DrawNode> node, int width, int depth) {
		this.node = node;	
		this.width = width;
		this.depth = depth;
	}
}

class DateSelectionPanel extends JPanel {
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

	public DateSelectionPanel(LayoutManager layout) {
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

class FilterSelectionPanel extends JPanel {
	enum Signals {
		NodeSearched
	}

	final LayoutManager DATE_SELECTION_FIELD_LAYOUT = new FlowLayout(FlowLayout.LEFT);

	public Listener<FilterSelectionPanel, Signals> listener = new Listener<>(this, Signals.class);
	
	public EntryFieldLabel first_name_field_panel = new EntryFieldLabel("First Name");
	public EntryFieldLabel last_name_field_panel = new EntryFieldLabel("Last Name"); 

	public JComponentPair<JLabel, JComboBox<Person.Gender>> gender_selection_panel = new JComponentPair<>(
		new JLabel("Gender"),
		new JComboBox<>(Person.Gender.values())
	); 

	//public EntryFieldLabel	birth_date_range_panel = new EntryFieldLabel("Birth Date Range"); 
	public JComponentPair<JLabel, DateSelectionPanel> birth_date_selection_start_panel = new JComponentPair<> (
		new GridLayout(2,1),
		new JLabel("Birth Date Start"),
		new DateSelectionPanel(DATE_SELECTION_FIELD_LAYOUT)	
	);

	public JComponentPair<JLabel, DateSelectionPanel> birth_date_selection_end_panel = new JComponentPair<> (
		new GridLayout(2,1),
		new JLabel("Birth Date End"),
		new DateSelectionPanel(DATE_SELECTION_FIELD_LAYOUT)	
	);
	
	public EntryFieldLabel orgin_country_panel = new EntryFieldLabel("Orgin Country");
	public EntryFieldLabel orgin_province_panel = new EntryFieldLabel("Orgin Province");
	public CheckBoxLabel has_children_panel = new CheckBoxLabel("Has Children");

	public JButton confirm_search_button = new JButton("Confirm Search");

	public FilterSelectionPanel() {
		super();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


		confirm_search_button.addActionListener(e -> {
			listener.fire_signal(Signals.NodeSearched);
		});

		//name_field_panel.text_field.setBounds(new Rectangle(0,0,30,30));

		birth_date_selection_start_panel.component1.setAlignmentX(Component.RIGHT_ALIGNMENT);
		birth_date_selection_end_panel.component1.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		add(first_name_field_panel);
		add(last_name_field_panel);
		add(gender_selection_panel);
		add(birth_date_selection_start_panel);
		add(birth_date_selection_end_panel);
		add(orgin_country_panel);
		add(orgin_province_panel);
		add(has_children_panel);
		add(confirm_search_button);


		setVisible(true);
		
		align_panels();

	}
	private void align_panels() {
		for (Component component : getComponents()) {
			if (!(component instanceof JComponent)) {
				continue;
			}
			JComponent jcomponent = (JComponent)component;

			jcomponent.setAlignmentX(Component.CENTER_ALIGNMENT);

		}
	}

	public LocalDate get_start_date() {
		return birth_date_selection_start_panel.component2.to_localdate();
	}

	public LocalDate get_end_date() {
		return birth_date_selection_end_panel.component2.to_localdate();
	}
}

class InfomationPanel extends JPanel {
	public final String BASE_CHILD_COUNT_TEXT = "Children:";
	public final String BASE_SIBLING_COUNT_TEXT = "Siblings:";
	public final String BASE_AUNT_COUNT_TEXT = "Aunts:";
	public final String BASE_ANCESTOR_COUNT_TEXT = "Ancestors:";
	public final String BASE_TREE_LEVEL_TEXT = "Generation:";

	public final String BASE_NODE_COUNT_TEXT = "Node Count:";
	public final String BASE_GENERATION_COUNT_TEXT = "Total Generations:";

	public final String BASE_PERSON_NAME_TEXT = "First Name: ";
	public final String BASE_PERSON_LAST_NAME_TEXT = "Last Name: ";
	public final String BASE_PERSON_GENDER_TEXT = "Gender: ";
	public final String BASE_PERSON_BIRTH_DATE_TEXT = "Birth Date: ";
	public final String BASE_PERSON_PASSING_DATE_TEXT = "Passing Date: ";
	public final String BASE_PERSON_ORGIN_COUNTRY_TEXT = "Orgin Country: ";
	public final String BASE_PERSON_ORGIN_PROVINCE_TEXT = "Orgin Province: ";

	

	private JPanel _node_position_info_panel = new JPanel(new FlowLayout());
	private JPanel _general_tree_info_panel = new JPanel(new FlowLayout());
	private JPanel _node_person_info_panel = new JPanel(new FlowLayout());
	
	private JLabel _child_count_label = new JLabel(BASE_AUNT_COUNT_TEXT);
	private JLabel _sibling_count_label = new JLabel(BASE_SIBLING_COUNT_TEXT);
	private JLabel _aunt_count_label = new JLabel(BASE_AUNT_COUNT_TEXT);
	private JLabel _ancestor_count_label = new JLabel(BASE_ANCESTOR_COUNT_TEXT);
	private JLabel _tree_level_label = new JLabel(BASE_TREE_LEVEL_TEXT);

	private JLabel _node_count_label = new JLabel(BASE_NODE_COUNT_TEXT);
	private JLabel _generations_label = new JLabel(BASE_GENERATION_COUNT_TEXT);

	private JLabel person_first_name_label = new JLabel(BASE_PERSON_NAME_TEXT);
	private JLabel person_last_name_label = new JLabel(BASE_PERSON_LAST_NAME_TEXT);
	private JLabel person_gender_label = new JLabel(BASE_PERSON_GENDER_TEXT);
	private JLabel person_birth_date_label = new JLabel(BASE_PERSON_BIRTH_DATE_TEXT);
	private JLabel person_passing_date_label = new JLabel(BASE_PERSON_PASSING_DATE_TEXT);
	private JLabel person_orgin_country_label = new JLabel(BASE_PERSON_ORGIN_COUNTRY_TEXT);
	private JLabel person_orgin_province_panel = new JLabel(BASE_PERSON_ORGIN_PROVINCE_TEXT);
	
	public InfomationPanel() {
		super();	
		setLayout(new GridLayout(3, 1));

		_node_person_info_panel.add(person_first_name_label);
		_node_person_info_panel.add(person_last_name_label);
		_node_person_info_panel.add(person_gender_label);
		_node_person_info_panel.add(person_birth_date_label);
		_node_person_info_panel.add(person_passing_date_label);
		_node_person_info_panel.add(person_orgin_country_label);
		_node_person_info_panel.add(person_orgin_province_panel);

		_node_position_info_panel.add(_node_person_info_panel);
		_node_position_info_panel.add(_child_count_label);
		_node_position_info_panel.add(_sibling_count_label);
		_node_position_info_panel.add(_aunt_count_label);
		_node_position_info_panel.add(_ancestor_count_label);
		_node_position_info_panel.add(_tree_level_label);

		_general_tree_info_panel.add(_node_count_label);
		_general_tree_info_panel.add(_generations_label);

		add(_node_person_info_panel);
		add(_node_position_info_panel);
		add(_general_tree_info_panel);
		
		_node_position_info_panel.setVisible(true);
		_general_tree_info_panel.setVisible(true);
		_node_person_info_panel.setVisible(true);


		setVisible(true);
	}
}


public class MainPanel extends JPanel {
	private FilterSelectionPanel _filter_selection_panel = new FilterSelectionPanel();
	private InfomationPanel _information_panel = new InfomationPanel();
	private NArrayNode<DrawNode> _draw_root_node;
	
	private JPanel _top_panel = new JPanel(new GridBagLayout());
	private JPanel _bottom_panel = new JPanel(new GridLayout(1,1));

	Scene _scene;
	private DrawPanel _draw_panel;
		
	public MainPanel(Dimension dimension, NArrayNode<DrawNode> root_node, AppConfig app_config) {
		super();
		_draw_root_node = root_node;

		Scene.Parameters scene_parameters = new Scene.Parameters();
		scene_parameters.block_width = (int)app_config.block_size().x;
		scene_parameters.block_height = (int)app_config.block_size().y;
		scene_parameters.region_width = 5000;
		scene_parameters.region_height = 5000;

		_scene = new Scene(scene_parameters);
		_draw_panel = new DrawPanel(_scene);

		final LayoutManager GRID_LAYOUT  = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		setLayout(GRID_LAYOUT);	
		setup_draw_nodes();

		_filter_selection_panel.listener.add_receiver(FilterSelectionPanel.Signals.NodeSearched, new Listener.ISignalFire<FilterSelectionPanel>() {
			@Override
			public void fire(FilterSelectionPanel holder, Object raw_emit_signal) {
				Search.ISearchableObject[] results = Search.fuzzy(_draw_root_node, 0, "Jonathan"); 

				for (int i=0; i<results.length; i++) {
					Search.ISearchableObject result = results[i];	

					assert result instanceof NArrayNode : "result is not an isearchable object!"; 

					NArrayNode<Person> node = (NArrayNode<Person>)result;	
					Person person = node.get_value();	

					System.out.println(person);
				}

				System.out.println(holder.get_start_date());
			}
		});


		_top_panel.add(_filter_selection_panel);
		_top_panel.add(_draw_panel);
		_bottom_panel.add(_information_panel);
			
		apply_input();	

		_draw_panel.transferFocus();
		
		position_components();
		
		_top_panel.setVisible(true);
		_bottom_panel.setVisible(true);

		setVisible(true);

	}

	

	private void setup_draw_nodes() {
		Camera camera = _draw_panel.get_camera();

		final int MAX_STACK_HEIGHT = Integer.MAX_VALUE;
		Stack<NArrayNode<DrawNode>> leaf_stack = new Stack<>();
		Stack<NArrayNode<DrawNode>> parent_stack = new Stack<>();

		int bottom_depth = 0;

		
		//bfs from the right so we can add leafs from the left
		for (NArrayNode<DrawNode> node : _draw_root_node.from_right_bfs()) {
			int width = node.get_child_arr().size();
			DrawNode draw_node = node.get_value();
			Transform parent_transform = node.get_parent().get_value().get_transform();
			float depth = parent_transform.rect.y + DrawNode.TOTAL_NODE_GAP_SPACE_Y;
			
			System.out.println("child: " + draw_node.get_person().get_first_name());
			//child leaf node will position the parents
			

			draw_node.set_position(new Vector2(-600, depth));
			
			if (node.is_leaf()) {
				leaf_stack.push(node);	
			}
			else {
				parent_stack.push(node);	
			}


			bottom_depth = (int)depth;
		}


		//we already have the nodes in a stack so a reverse BFS is easy
		
		int depth = bottom_depth;
		NArrayNode<DrawNode> current_parent = _draw_root_node;
		
		int iter_x = 0;

		while (leaf_stack.is_empty() == false) {
			NArrayNode<DrawNode> node = leaf_stack.pop();	
			DrawNode draw_node = node.get_value();	
			int node_child_count = node.get_child_arr().size();

			if (depth != draw_node.get_position().y) {
				depth = (int)draw_node.get_position().y;
				
			}

			//add 0.1 so 0 will also have a width of 1
			int iter_width = (int)Math.ceil(node_child_count+0.1 / DrawNode.NODES_PER_POSITION_WIDTH);

			
			draw_node.set_position(new Vector2(iter_x * DrawNode.TOTAL_NODE_GAP_SPACE_X, draw_node.get_position().y));
			iter_x++;
			_scene.add_object(node.get_value());
			
		}


		while (parent_stack.is_empty() == false) {
			NArrayNode<DrawNode> node = parent_stack.pop();
			DrawNode draw_node = node.get_value();

			draw_node.set_position(get_position_based_on_children(node));

			//camera.add_draw_object(draw_node);
			_scene.add_object(draw_node);	

			//add the lines connecting to the children
			for (NArrayNode<DrawNode> child : node.get_child_arr()) {
				LineObject line = draw_node.add_child_node(child.get_value());

				_scene.add_object(line);
			}
		}

		
		_draw_root_node.get_value().set_position(get_position_based_on_children(_draw_root_node));
		
		_scene.add_object(_draw_root_node.get_value());
	}

	private Vector2 get_position_based_on_children(NArrayNode<DrawNode> node) {
		DrawNode left_draw_node = node.get_child_arr().getLast().get_value();
		DrawNode right_draw_node = node.get_child_arr().getFirst().get_value();

		return new Vector2 (
			left_draw_node.get_position().x - DrawNode.NODE_WIDTH / 2 + (right_draw_node.get_position().x + DrawNode.NODE_WIDTH - left_draw_node.get_position().x) / 2,
			left_draw_node.get_position().y - DrawNode.TOTAL_NODE_GAP_SPACE_Y
		);
	}
		

	private void position_components() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;

		//top_panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.ipadx = 100;
		_top_panel.add(_filter_selection_panel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		_top_panel.add(_draw_panel, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		add(_top_panel, gbc);
	
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		add(_bottom_panel, gbc);

	}

	public void apply_input() {
		InputMap input_map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap action_map = getActionMap();

		final String GIVE_BACK_FOCUS_ACTION = "ReturnFocus";


		input_map.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), 
				GIVE_BACK_FOCUS_ACTION
				);

		action_map.put(GIVE_BACK_FOCUS_ACTION, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_draw_panel.requestFocusInWindow();
			}
		});
	}
}
