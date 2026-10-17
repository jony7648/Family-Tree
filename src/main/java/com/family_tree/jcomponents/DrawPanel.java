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
import com.family_tree.scene_components.*;
import com.family_tree.jcomponents.interfaces.*;
import com.family_tree.serialized_structures.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;

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




class SearchPanel extends GenericDesktopPanel {
	enum Signal {
		NodeSearched
	}

	public record SignalSearchInfo (
		String name,
		String last_name
	) {}
	
	final String WIN_NAME = "Search Panel";

	public Listener<SearchPanel, Signal> listener = new Listener<>(this, Signal.class);
		
	JPanel main_panel = new JPanel();

	private Dimension _window_bounds = new Dimension(200, 200);
		
	EntryFieldLabel first_name_pair = new EntryFieldLabel("First Name", "N/A");
	EntryFieldLabel last_name_pair = new EntryFieldLabel("Last Name", "N/A");
	JButton confirm_search_button = new JButton("Search!");

	

	public SearchPanel() {
		panel_to_generic_window(WIN_NAME, main_panel);

		//setUndecorated(true);
		//setAlwaysOnTop(true);
		setBounds(0, 0, (int)_window_bounds.getWidth(), (int)_window_bounds.getHeight());
		main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));

		

		main_panel.add(first_name_pair);
		main_panel.add(last_name_pair);
		main_panel.add(confirm_search_button);

		confirm_search_button.setAlignmentX(Component.CENTER_ALIGNMENT);

		confirm_search_button.addActionListener(e -> {
			String first_name = first_name_pair.get_text_field().getText();
			String last_name = last_name_pair.get_text_field().getText();

			Listener.Signal<SearchPanel> signal = new Listener.Signal<SearchPanel>(this, 
				new SignalSearchInfo(first_name, last_name)
			);

			listener.fire_signal(Signal.NodeSearched, signal);
		});

	}
	
	@Override
	protected void paintComponent(Graphics BaseG) {
		Graphics2D g = (Graphics2D)BaseG;
		
		g.setColor(new Color(255, 255, 255));
		g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
	}
}



public class DrawPanel extends JDesktopPane {
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


		Scene _scene;
		private Camera _camera;


		public PlayArea(Scene render_scene) {
			_camera = new Camera(render_scene, getWidth(), getHeight());

			Timer frameTimer = new Timer(16, this);
			frameTimer.start();

			ActionMap action_map = this.getActionMap();
			InputMap input_map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

			addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					Vector2 mouse_pos = new Vector2(e.getX(), e.getY());

					System.out.println(mouse_pos.toString());	
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


		public void on_frame_update() {
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
			on_frame_update();
			repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(300, 300);
		}
	}

	
	NArrayNode<DrawNode> _draw_root_node;
	DrawNodeSearchTree _node_search_tree;
	Scene _scene;
	public PlayArea _play_area;	
	public SearchPanel _search_panel = new SearchPanel();
	

	public DrawPanel(NArrayNode<DrawNode> root_node, DrawNodeSearchTree search_tree, Dimension win_dimensions, AppConfig app_config) {
		Scene.Parameters scene_parameters = new Scene.Parameters();
		scene_parameters.block_width = (int)app_config.block_size().x;
		scene_parameters.block_height = (int)app_config.block_size().y;
		scene_parameters.region_width = 5000;
		scene_parameters.region_height = 5000;
		
		_scene = new Scene(scene_parameters);
		_play_area = new PlayArea(_scene);
		_draw_root_node = root_node;
		_node_search_tree = search_tree;
		
		_play_area.setBounds(0, 0, (int)win_dimensions.getWidth(), (int)win_dimensions.getHeight());

		TreeAligner.setup_draw_nodes(_scene, _draw_root_node);		

			
		setVisible(true);
		add(_search_panel, 1);
		add(_play_area, 2);	

		apply_input();

		

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Dimension size = getSize();
				_play_area.setBounds(0, 0, (int)size.getWidth(), (int)size.getHeight());
			}
		});

		_search_panel.listener.add_receiver(SearchPanel.Signal.NodeSearched, (search_panel, signal) -> {
			if (!(signal.sig_data instanceof SearchPanel.SignalSearchInfo)) {
				return;
			}

			SearchPanel.SignalSearchInfo search_info = (SearchPanel.SignalSearchInfo)signal.sig_data;
			
			System.out.println("YES IT WORKED");
			String first_name = search_info.name();
			String last_name = search_info.last_name();

			LinkedList<DrawNode> node_list = _node_search_tree.get(first_name, last_name);

			_play_area._camera.set_position(node_list.get(1).get_position());

			
			
		});

		/*
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
		

			//setComponentZOrder(_play_area, 0);
		//setComponentZOrder(_search_panel, 1);
		*/
	}

	
	@Override
		protected void paintComponent(Graphics BaseG) {
			super.paintComponent(BaseG);
			Graphics2D g = (Graphics2D) BaseG;
			
		}

	public void give_focus_to_play_area() {
		_play_area.requestFocusInWindow();
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

