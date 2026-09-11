package com.family_tree.panels;

import com.family_tree.util.*;

import com.family_tree.math_classes.Geometry.*;

import com.family_tree.data_storage.*;
import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.structures.classes.generic.Stack;
import com.family_tree.algorithms.Search;
import com.family_tree.Listener.*;

import com.family_tree.draw_objects.*;
import com.family_tree.draw_components.classes.*;

import com.family_tree.jcomponents.*;

import java.awt.LayoutManager;
import java.awt.GridLayout;

import java.time.LocalDate;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import java.util.Collections;
import java.util.ArrayList;

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
	private DrawPanel _draw_panel = new DrawPanel();
	private InfomationPanel _information_panel = new InfomationPanel();
	private NArrayNode<DrawNode> _draw_root_node;
	
	private JPanel _top_panel = new JPanel(new GridBagLayout());
	private JPanel _bottom_panel = new JPanel(new GridLayout(1,1));
		
	public MainPanel(Dimension dimension, NArrayNode<DrawNode> root_node) {
		super();

		_draw_root_node = root_node;


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
		Stack<NArrayNode<DrawNode>> node_stack = new Stack<>();

		

		/*
		for (int parent_index=0; parent_index<_draw_root_node.child_count(); parent_index++) {
			NArrayNode<DrawNode> parent_node = _draw_root_node.get_child(parent_index);
			DrawNode parent_draw_node = parent_node.get_value();

			//What we are going to do instead is figure out how left the nodes are
			//then reverse bfs to draw them

			camera.add_draw_object(parent_draw_node);
		}
		*/

		//NodeWidthHolder current_holder = new NodeWidthHolder(_draw_root_node, _draw_root_node.get_child_arr().size(), 0);	
		//node_stack.push(current_holder);
		

		int bottom_depth = 0;

		
		for (NArrayNode<DrawNode> node : _draw_root_node.bfs_iter()) {
			int width = node.get_child_arr().size();
			DrawNode draw_node = node.get_value();
			Transform parent_transform = node.get_parent().get_value().get_transform();
			float depth = parent_transform.rect.y + DrawNode.TOTAL_NODE_GAP_SPACE_Y;
			
			//child leaf node will position the parents
			

			draw_node.set_position(-600, depth);
			
			if (node.has_children() == false) {
				node_stack.push(node);	
			}


			bottom_depth = (int)depth;
			camera.add_draw_object(node.get_value());
		}


		//we alread have the nodes in a stack so a reverse BFS is easy

		
		int depth = bottom_depth;
		NArrayNode<DrawNode> current_parent = _draw_root_node;
		
		int iter_x = 0;
		while (!node_stack.is_empty()) {
			NArrayNode<DrawNode> node = node_stack.top();
			DrawNode draw_node = node.get_value();
			node_stack.pop();
			System.out.println(node.get_value().get_person().get_first_name() + " Should?: " + node.get_parent().get_value().get_person());
			System.out.println(node.get_parent().get_value().get_initial_position_flag());


			if (draw_node.get_position().y != bottom_depth && node.get_value().get_initial_position_flag()) {
				get_position_based_on_parent();
				continue;
			}
			
			int node_y = (int)draw_node.get_transform().rect.y;

			draw_node.set_position(iter_x * DrawNode.TOTAL_NODE_GAP_SPACE_X, node_y);

			if (time_to_position_parents(node, current_parent, bottom_depth)) {
				int parent_width = position_parents(node);	 
				iter_x += parent_width; 
			}
			
			
			current_parent = node.get_parent();
			iter_x++;
		}

		
		camera.add_draw_object(_draw_root_node.get_value());
	}

	private void get_position_based_on_parent(NArrayNode<DrawNode> node) {
		if (node.has_parent() == false)
			return;

		Vector2 node_pos = node.get_value().get_position().copy();	

		//finish this code to set the position

	}

	private boolean time_to_position_parents(NArrayNode<DrawNode> child_node, NArrayNode<DrawNode> current_parent, int bottom_depth) {
		int depth = (int)child_node.get_value().get_transform().rect.y;
		NArrayNode<DrawNode> parent = child_node.get_parent();
		DrawNode parent_draw_node = parent.get_value();
		
		//stack reverses order so the last node becomes the first
		NArrayNode<DrawNode> last_node = child_node.get_parent().get_child_arr().getFirst();
		
		return (
			parent_draw_node.get_initial_position_flag() == false &&
			child_node.has_parent() &&
			child_node == last_node
		);
	}

	private Vector2 calculate_parent_pos(DrawNode left_node, DrawNode right_node) {
		float left_node_x = left_node.get_position().x;
		float right_node_wall = right_node.get_transform().rect.x;
		
		return new Vector2(
			left_node_x + (right_node_wall - left_node_x) / 2,
			left_node.get_position().y - DrawNode.TOTAL_NODE_GAP_SPACE_Y
		);
	}

	private int position_parents(NArrayNode<DrawNode> root_child_node) {
		NArrayNode<DrawNode> root_parent_node = root_child_node.get_parent();
		NArrayNode<DrawNode> curr_node = root_child_node;
		
		int root_parent_child_count = root_parent_node.get_child_arr().size();

		DrawNode left_node = root_parent_node.get_child_arr().getLast().get_value();	
		DrawNode right_node = root_parent_node.get_child_arr().getFirst().get_value();	


		while(true) {
			NArrayNode<DrawNode> parent = curr_node.get_parent();	
			parent.get_value().set_initial_position_flag();
			
			System.out.println(left_node.get_person().get_first_name() + " " + left_node.get_position());
			System.out.println("left position: " + left_node.get_position());
			System.out.println("parent: " + parent.get_value().get_person().get_first_name());
			System.out.println(curr_node.get_value().get_person());
			
			Vector2 parent_position = calculate_parent_pos(left_node, right_node);
			parent.get_value().set_position(parent_position);

		
			System.out.println(parent.get_value().get_person().get_first_name() + " pos: " + parent_position);

			
			//positions parent's siblings
			

			//set root node then quit
			if (parent.has_parent() == false) {
				DrawNode root_left_node = parent.get_child_arr().getFirst().get_value();
				DrawNode root_right_node = parent.get_child_arr().getLast().get_value();


				Vector2 root_pos = calculate_parent_pos(root_left_node, root_right_node);
				parent.get_value().set_position(root_pos);
				System.out.println("Root pos: " +  root_pos);
				
				break;
			}
			
			int parent_index = 0;
			ArrayList<NArrayNode<DrawNode>> ancestor_list = parent.get_parent().get_child_arr();
			for (int i=0; i<ancestor_list.size(); i++) {
				NArrayNode<DrawNode> ancestor = ancestor_list.get(i);
				
				if (ancestor == parent) {
					parent_index = i;
					break;
				}
			}



			float right_node_wall = right_node.get_transform().rect.x;
			for (int i=0; i<ancestor_list.size(); i++) {
				NArrayNode<DrawNode> ancestor = ancestor_list.get(i);
				DrawNode ancestor_draw_node = ancestor.get_value();
				
				if (ancestor == parent) {
					continue;
				}

				System.out.println("i: "+ i);

				Vector2 position = new Vector2(
					right_node_wall + (i - parent_index) * DrawNode.TOTAL_NODE_GAP_SPACE_X,
					parent_position.y
				);


				ancestor.get_value().set_position(position);
				ancestor_draw_node.set_initial_position_flag();

			}


			System.out.println(curr_node.get_value().get_position() + " " + parent.get_value().get_position());
			curr_node = parent;
			

				System.out.println("LOOPING START");
			for (NArrayNode<DrawNode> node : parent.get_child_arr()) {
				System.out.println(node.get_value().get_person().get_first_name());
			}

				System.out.println("LOOPING END");
			
			left_node = curr_node.get_parent().get_child_arr().getFirst().get_value();	
            right_node = curr_node.get_parent().get_child_arr().getLast().get_value();	
		}

		return root_parent_child_count;
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
