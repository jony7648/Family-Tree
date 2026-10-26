package com.family_tree.jcomponents;

import com.family_tree.util.Util;
import com.family_tree.math_classes.Geometry.*;

import com.family_tree.draw_objects.*;
import com.family_tree.draw_objects.geometric.*;
import com.family_tree.algorithms.TreeAligner;
import com.family_tree.Listener.*;

import com.family_tree.structures.classes.node_structures.DrawNodeSearchTree;
import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.math_classes.VSpaceMap;

import com.family_tree.draw_components.classes.Camera;
import com.family_tree.draw_components.interfaces.IDrawObject;
import com.family_tree.scene_components.*;
import com.family_tree.jcomponents.DateSelectionField;
import com.family_tree.jcomponents.interfaces.*;
import com.family_tree.serialized_structures.*;
import com.family_tree.data_storage.Person;
import com.family_tree.singletons.*;
import com.family_tree.application_menu.*;
import com.family_tree.scene_components.generic_windows.*;
import com.family_tree.algorithms.Search;
import com.family_tree.algorithms.Sort;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.time.LocalDate;

record DebugLine (
	String message,
	Object value
) {
	public String toString() {
		String value_str = "";	

		if (value == null) {
			value_str = "NULL";	
		}
		else {
			value_str = value.toString();
		}

		return message + ": " + value_str;
	}
};

class OptionCycler<T> extends JLayeredPane {
	enum Signal {
		OptionCycled,
	}

	public class SignalOptionCycled {
		int index;
		T object;
	}

	public Listener<OptionCycler<T>, Signal> listener = new Listener<>(this, Signal.class);
	
	private JButton _left_button = new JButton();	
	private JButton _right_button = new JButton();	
	private JLabel _index_label = new JLabel("N/A");
	Component left_spacer = Box.createHorizontalStrut(_index_label.getPreferredSize().width);

	private PositionalList<T> _option_list = new PositionalList<>();

	private int _cycle_index = 0;

	private Dimension dimensions = new Dimension(200,100);

	public OptionCycler() {
		_left_button.addActionListener(e -> {
			cycle(-1);
		});

		_right_button.addActionListener(e -> {
			cycle(1);
		});
		

		_index_label.setPreferredSize(new Dimension(75, 75));
		add(_left_button);
		add(left_spacer, BorderLayout.WEST);
		add(_index_label);
		add(_right_button);
		setLayout(new FlowLayout());

		_left_button.setVisible(true);
		_index_label.setVisible(true);
		_right_button.setVisible(true);

		setVisible(false);
	}

	@Override 
	public Dimension getPreferredSize() {
		return new Dimension(400,100);	
	}

	public void set_list(PositionalList<T> list) {
		_option_list = list;

		//reset the cycle index
		_cycle_index = 0;	
		cycle(0);
		System.out.println(_right_button.getSize());

	}

	public T cycle(int direction) throws IllegalStateException {
		if (_option_list.is_empty()) {
			throw new IllegalStateException("The option list is empty!!");
		}
		
		_cycle_index += direction;
		_cycle_index = Math.clamp(_cycle_index, 0, _option_list.get_size()-1);

		T ret_obj = _option_list.get(_cycle_index);

		Integer numerator = _cycle_index+1;
		Integer denomintor = _option_list.get_size();

		String display_text = numerator + "/" + denomintor;

		_index_label.setText(display_text);

		SignalOptionCycled option_cycled = new SignalOptionCycled();
		option_cycled.index = _cycle_index;
		option_cycled.object = ret_obj;

		Listener.Signal<OptionCycler<T>> signal = new Listener.Signal<>(this, option_cycled);

		listener.fire_signal(Signal.OptionCycled, signal);

		return ret_obj;
	}

	public void reposition(Dimension size) {
		int center_x = (int)size.getWidth() / 2;

		int left_pos = center_x - getPreferredSize().width / 2;
		int bottom_pos = (int)(size.getHeight() - getPreferredSize().height);
		
		setBounds(left_pos, bottom_pos, getPreferredSize().width, getPreferredSize().height);
	}
}



public class DrawPanel extends JDesktopPane {
	public class PaneInfo {
		private Dimension _win_size = new Dimension();
		private JDesktopPane _pane;

		public PaneInfo(JDesktopPane pane) {
			_pane = pane;
		}

		public Dimension get_win_size() {
			return _win_size;
		}

		public JDesktopPane get_pane() {
			return _pane;
		}
	}
	
	public class PlayArea extends JLayeredPane implements ActionListener {
		private enum MovementKey {
			Up,
			Down,
			Left,
			Right,
		};

		private enum ZoomKey {
			Minus,
			Equals,
		}

		Util.InputActionGroup movement_action_group = new Util.InputActionGroup("CameraMovement", new int[][] {
			{KeyEvent.VK_UP, KeyEvent.VK_W},
			{KeyEvent.VK_LEFT, KeyEvent.VK_A},
			{KeyEvent.VK_DOWN, KeyEvent.VK_S},
			{KeyEvent.VK_RIGHT, KeyEvent.VK_D},
		});

		Util.InputActionGroup zoom_action_group = new Util.InputActionGroup("CameraZoom", new int[][] {
			{KeyEvent.VK_Q, KeyEvent.VK_MINUS},
				{KeyEvent.VK_E, KeyEvent.VK_EQUALS},
		});


		private Scene _scene;
		private Camera _camera;
		private DrawNode _selected_node;

		public PlayArea(Scene render_scene) {
			_camera = new Camera(this, render_scene, getWidth(), getHeight());
			_scene = render_scene;

			Timer frameTimer = new Timer(16, this);
			frameTimer.start();

			ActionMap action_map = this.getActionMap();
			InputMap input_map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

			addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					final int MOUSE_WIDTH = 10;
					final int MOUSE_HEIGHT = 10;
					
					Vector2 mouse_screen_pos = new Vector2(e.getX(), e.getY());
					
					VSpaceMap vmap = _scene.get_vmap();

					Rect mouse_world_rect = new Rect(
						_camera.get_position().x + mouse_screen_pos.x,
						_camera.get_position().y + mouse_screen_pos.y,
						MOUSE_WIDTH,
						MOUSE_HEIGHT
					);

					Iterable<IDrawObject> obj_iterator = vmap.get_elements_from_region(_camera);
					IDrawObject clicked_obj = null;

					for (IDrawObject obj : obj_iterator) {
						boolean colliding = Util.is_colliding(obj.get_transform().rect, mouse_world_rect);

						if (colliding) {
							clicked_obj = obj;
							break;
						}
					}

					if (clicked_obj instanceof DrawNode) {
						DrawNode node = (DrawNode)clicked_obj;

						_selected_node = node;
						_camera.teleport_to_transform(node.get_transform());
					}


					
				}
			});

			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					_camera.update_win_res(getWidth(), getHeight());
				}
			});

			addKeyListener(Util.get_key_adapater(new Util.IKeyAdapaterMethod() {
				@Override
				public void on_event(KeyEvent e, boolean held) {
					int key_code = e.getExtendedKeyCode();

					java.awt.AWTKeyStroke stroke = java.awt.AWTKeyStroke.getAWTKeyStrokeForEvent(e);

					Vector2 pos_change = new Vector2(0);
					final int MOVE_SPEED = 1;

					int multiplier = held ? 1 : 0;

					switch (stroke.getKeyCode()) {
						case KeyEvent.VK_W:
						case KeyEvent.VK_UP:
							pos_change.y = -multiplier;
							break;
						case KeyEvent.VK_A:
						case KeyEvent.VK_LEFT:
							pos_change.x = -multiplier;
							break;
						case KeyEvent.VK_S:
						case KeyEvent.VK_DOWN:
							pos_change.y = multiplier;
							break;
						case KeyEvent.VK_D:
						case KeyEvent.VK_RIGHT:
							pos_change.x = multiplier;
							break;
						case KeyEvent.VK_MINUS:
							_camera.zoom_out();
							break;
						case KeyEvent.VK_EQUALS:
							_camera.zoom_in();
							break;
					}

					_camera.move(pos_change);
				}
			}));

			setVisible(true);
		}

		
		public Person get_selected_person() {
			return _selected_node.get_person();
		}

		public void set_selected_node(DrawNode node) {
			_selected_node = node;
		}

		public Camera get_camera() {
			return _camera;
		}

		public void draw_debug_info(Graphics2D g) {
			final int DRAW_START_HEIGHT = 30;
			final int DEBUG_LINE_SPACING = 30;
			Scene render_scene = _camera.get_render_scene();
			VSpaceMap vspacemap = _camera.get_render_scene().get_vmap();

			DebugLine debug_line_arr[] = {
				new DebugLine("Cam Pos", _camera.get_position()),
				new DebugLine("Curr Block", vspacemap.position_to_row_column_pair(_camera.get_position()).toString()),
				new DebugLine("Row Count", render_scene.get_block_row_count()),
				new DebugLine("Column Count", render_scene.get_block_column_count()),
				new DebugLine("Rendered Objects", _camera.get_rendered_objects_count()),
			};

			for (int i=0; i<debug_line_arr.length; i++) {
				DebugLine debug_line = debug_line_arr[i];

				int draw_height = DRAW_START_HEIGHT + i*DEBUG_LINE_SPACING;

				g.drawString(debug_line.toString(), 0, draw_height);
			}
		}

		public void draw_background(Graphics2D g) {
			final Color TOP_COLOR = new Color(3, 5, 56);
			final Color BOTTOM_COLOR = new Color(62,39,230);

			int x1 = 50;
			int y1 = 150;
			int y2 = 250;
			int x2 = 50;

			GradientPaint gradient = new GradientPaint(
				x1, y1, TOP_COLOR,
				x2, y2, BOTTOM_COLOR
			);

			g.setPaint(gradient);
			g.fillRect(0,0, getWidth(), getHeight());
		}

		@Override
		protected void paintComponent(Graphics BaseG) {
			super.paintComponent(BaseG);
			Graphics2D g = (Graphics2D) BaseG;


			draw_background(g);

			_camera.on_draw(g);
			draw_debug_info(g);

		}


		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(300, 300);
		}
	}

	
	private PaneInfo _pane_info = new PaneInfo(this);
	NArrayNode<DrawNode> _draw_root_node;
	DrawNodeSearchTree _node_search_tree;
	Scene _scene;
	JFrame _main_frame;
	public PlayArea _play_area;	
	public SearchPanel _search_panel = new SearchPanel(_pane_info);
	public NodeCreationWindow _node_creation_menu = new NodeCreationWindow(_pane_info);
	public EditAttributeMenu _edit_attrib_menu = new EditAttributeMenu(_pane_info);
	public OptionCycler<DrawNode> _option_cycler = new OptionCycler<>();

	public DrawPanel(JFrame main_frame, NArrayNode<DrawNode> root_node, DrawNodeSearchTree search_tree, Dimension win_dimensions, AppConfig app_config) {
		Scene.Parameters scene_parameters = new Scene.Parameters();
		scene_parameters.block_width = (int)app_config.block_size().x;
		scene_parameters.block_height = (int)app_config.block_size().y;
		scene_parameters.region_width = 5000;
		scene_parameters.region_height = 5000;
		
		_main_frame = main_frame;
		_scene = new Scene(scene_parameters);
		_play_area = new PlayArea(_scene);
		_draw_root_node = root_node;
		_node_search_tree = search_tree;
		_play_area.set_selected_node(_draw_root_node.get_value());
		
		_play_area.setBounds(0, 0, (int)win_dimensions.getWidth(), (int)win_dimensions.getHeight());

		TreeAligner.setup_draw_nodes(_scene, _draw_root_node);		
		

			
		setVisible(true);
		setOpaque(true);
		add(_search_panel);
		add(_edit_attrib_menu);
		add(_node_creation_menu);

		add(_option_cycler);
		add(_play_area);	

		apply_input();
	
		
		_search_panel.listener.add_receiver(SearchPanel.Signal.NodeSearched, (search_panel, signal) -> {
			if (!(signal.sig_data instanceof SearchPanel.SignalSearchInfo)) {
				return;
			}

			SearchPanel.SignalSearchInfo search_info = (SearchPanel.SignalSearchInfo)signal.sig_data;
			String first_name = search_info.first_name();
			String last_name = search_info.last_name();
			final String ERROR_MESSAGE = "Failed to find node!";

			PositionalList<DrawNode> node_list;

			switch (search_info.search_mode()) {
				case Exact:
					node_list = _node_search_tree.get(first_name, last_name);
					break;
				case Fuzzy:
					node_list = _node_search_tree.fuzzy_get(first_name, last_name);
					break;
				default:
					JOptionPane.showMessageDialog(_main_frame, ERROR_MESSAGE);
					return;
			}


			if (node_list.is_empty()) {
				JOptionPane.showMessageDialog(_main_frame, ERROR_MESSAGE);
				return;	
			}

			_option_cycler.set_list(node_list);
			_option_cycler.setVisible(true);
			_search_panel.setVisible(false);
		});

		_option_cycler.listener.add_receiver(OptionCycler.Signal.OptionCycled, (_option_cycler, signal) -> {
			if (!(signal.sig_data instanceof OptionCycler.SignalOptionCycled)) {
				return;
			}

			@SuppressWarnings("unchecked")
			OptionCycler<DrawNode>.SignalOptionCycled cycle_result = (OptionCycler<DrawNode>.SignalOptionCycled)signal.sig_data;
			DrawNode node = cycle_result.object;

			_play_area.get_camera().teleport_to_object(node);
		});

		_edit_attrib_menu.listener.add_receiver(EditAttributeMenu.Signal.value_edited, (emitter, emit_signal) -> {
			if (!(emit_signal.sig_data instanceof EditAttributeMenu.SignalValueEdited)) {
				return;
			}

			EditAttributeMenu.SignalValueEdited signal = (EditAttributeMenu.SignalValueEdited)emit_signal.sig_data;

			Person.EditMenuOption attrib_menu = (Person.EditMenuOption)signal.option_value();
			Person person = _play_area.get_selected_person();
			
			person.set_value_from_edit_option((Person.EditMenuOption)signal.option_value(), signal.value());
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Dimension size = getSize();
				_play_area.setBounds(0, 0, (int)size.getWidth(), (int)size.getHeight());
				_option_cycler.reposition(size);
				_pane_info._win_size = size;
			}
		});
	}

	@Override
	protected void paintComponent(Graphics BaseG) {
		super.paintComponent(BaseG);
		Graphics2D g = (Graphics2D) BaseG;
	}

	public void edit_node(Person.EditMenuOption edit_option) {
		Person person = _draw_root_node.get_value().get_person();

		_edit_attrib_menu.prompt_value_change(person.get_value_from_edit_option(edit_option), edit_option);
	}

	

	public void give_focus_to_play_area() {
		_play_area.requestFocusInWindow();
	}

	public PlayArea play_area() {
		return _play_area;
	}

	public void display_node_search_menu() {
		_search_panel.setVisible(true);
	}

	public void display_node_creation_menu() {
		_node_creation_menu.setVisible(true);
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
				give_focus_to_play_area();
			}
		});
	}
}

