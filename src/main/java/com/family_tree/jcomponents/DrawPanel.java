package com.family_tree.jcomponents;

import com.family_tree.util.Util;
import com.family_tree.math_classes.Geometry.*;

import com.family_tree.draw_objects.*;
import com.family_tree.structures.classes.node_structures.*;


import com.family_tree.draw_components.classes.Camera;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DrawPanel extends JPanel implements ActionListener{
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

	
	private Camera _camera = new Camera();

	public DrawPanel() {
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

	public void position_new_draw_node(NArrayNode<DrawNode> node, DrawNode new_draw_node) {
		//node.child_count()
	}

	public Camera get_camera() {
		return _camera;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2D = (Graphics2D) g;

		_camera.draw(g2D);
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
