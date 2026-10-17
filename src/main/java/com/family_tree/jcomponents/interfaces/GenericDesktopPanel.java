package com.family_tree.jcomponents.interfaces;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;


public class GenericDesktopPanel extends JLayeredPane {
	public final String EXIT_BUTTON_IMAGE_PATH = "assets/virtual-desktop/exit-button.png";
	public final ImageIcon EXIT_BUTTON_IMAGE = new ImageIcon(EXIT_BUTTON_IMAGE_PATH);
	
	public final int GRAB_AREA_FROM_TOP = 30;
	public final int VERTICAL_DRAG_OFFSET = 65;

	private Point _click_distance = new Point();
	
	boolean _window_grabbed = false;

	
	JPanel _tool_bar = new JPanel();
	JLabel _title_lable;
	JButton _exit_button = new JButton();
	
	
	public void panel_to_generic_window(String win_name, JPanel main_panel) {
		//we should add the main panel to gbc to allow for tool bar	

		_title_lable = new JLabel(win_name);
		_exit_button.setIcon(EXIT_BUTTON_IMAGE);
		_exit_button.setSize(0, EXIT_BUTTON_IMAGE.getIconHeight());

		_exit_button.setBorderPainted(false);      // Removes the default gray border
        _exit_button.setContentAreaFilled(false);   // Removes the default background paint
        _exit_button.setFocusPainted(false);  
		

		GridBagConstraints gbc = new GridBagConstraints();
		
		setLayout(new GridBagLayout());
		setOpaque(true);
		setVisible(true);
		
		_tool_bar.setVisible(true);
		_tool_bar.setLayout(new BorderLayout());
		main_panel.setVisible(true);

		gbc.fill = GridBagConstraints.HORIZONTAL; 
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0;
		add(_tool_bar, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1;
		add(main_panel, gbc);

		
		_title_lable.setHorizontalAlignment(SwingConstants.CENTER);
		_tool_bar.add(_title_lable, BorderLayout.CENTER);

		_tool_bar.add(_exit_button, BorderLayout.EAST);


		//forces title lable to be in the center
		Component leftSpacer = Box.createHorizontalStrut(_exit_button.getPreferredSize().width);
		_tool_bar.add(leftSpacer, BorderLayout.WEST);



		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getLocationOnScreen().y - getLocation().y - VERTICAL_DRAG_OFFSET <= GRAB_AREA_FROM_TOP) {
					_window_grabbed = true;
					_click_distance.x = e.getLocationOnScreen().x - getLocation().x;
					_click_distance.y = e.getLocationOnScreen().y - getLocation().y;
				}
			}

			public void mouseReleased(MouseEvent e) {
				_window_grabbed = false;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point mouse_pos = e.getLocationOnScreen();

				if (_window_grabbed) {
					setLocation(
						mouse_pos.x - _click_distance.x,
						mouse_pos.y - _click_distance.y
					);

				}
			}
		});

	}
}
