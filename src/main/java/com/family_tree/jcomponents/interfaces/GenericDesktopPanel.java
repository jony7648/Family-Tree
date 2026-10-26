package com.family_tree.jcomponents.interfaces;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.geom.Rectangle2D;
import com.family_tree.jcomponents.DrawPanel;

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

	final static Border DEFAULT_BORDER = BorderFactory.createLineBorder(Color.lightGray, 5, false);
	final static Color TOOL_TIP_COLOR = new Color(255,255,255);
	final int MAIN_PANEL_INNER_BORDER_SIZE = 10;
	final Border MAIN_PANEL_INNER_BORDER = BorderFactory.createEmptyBorder(MAIN_PANEL_INNER_BORDER_SIZE,MAIN_PANEL_INNER_BORDER_SIZE,MAIN_PANEL_INNER_BORDER_SIZE,MAIN_PANEL_INNER_BORDER_SIZE);

	DrawPanel.PaneInfo _pane_info;
	
	//final static Color TOOL_TIP_COLOR = new Color(36,65,249);

	public void panel_to_generic_window(String win_name, JComponent main_panel, DrawPanel.PaneInfo pane_info) {
		//we should add the main panel to gbc to allow for tool bar	
		setBorder(DEFAULT_BORDER);
		_pane_info = pane_info;

		_title_lable = new JLabel(win_name);
		_exit_button.setIcon(EXIT_BUTTON_IMAGE);
		_exit_button.setSize(0, EXIT_BUTTON_IMAGE.getIconHeight());

		_exit_button.addActionListener(e -> {
			setVisible(false);
		});

		_exit_button.setBorderPainted(false);      
        _exit_button.setContentAreaFilled(false);   
        _exit_button.setFocusPainted(false);  
		

		GridBagConstraints gbc = new GridBagConstraints();
		
		setLayout(new GridBagLayout());
		setOpaque(true);
		setVisible(false);
		
		_tool_bar.setVisible(true);
		_tool_bar.setLayout(new BorderLayout());
		_tool_bar.setBackground(TOOL_TIP_COLOR);
		main_panel.setVisible(true);
		main_panel.setBorder(MAIN_PANEL_INNER_BORDER);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 0;
		add(_tool_bar, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1;
		add(main_panel, gbc);

		
		_title_lable.setHorizontalAlignment(SwingConstants.CENTER);
		_tool_bar.add(_title_lable, BorderLayout.CENTER);
		_tool_bar.add(_exit_button, BorderLayout.EAST);

		//forces title lable to be in the center
		Component left_spacer = Box.createHorizontalStrut(_exit_button.getPreferredSize().width);

		_tool_bar.add(left_spacer, BorderLayout.WEST);

		setBounds (0, 0,
			(int)(_tool_bar.getPreferredSize().getWidth() + main_panel.getPreferredSize().getWidth()),
			(int)(_tool_bar.getPreferredSize().getHeight() + main_panel.getPreferredSize().getHeight() + 30)
		);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point mouse_pos = SwingUtilities.convertPoint(
					e.getComponent(), 
					e.getPoint(),    
					pane_info.get_pane()
				);

				
				if (mouse_pos.y - getLocation().y - VERTICAL_DRAG_OFFSET <= GRAB_AREA_FROM_TOP) {
					_window_grabbed = true;
					_click_distance.x = mouse_pos.x - getLocation().x;
					_click_distance.y = mouse_pos.y - getLocation().y;
				}
			}

			public void mouseReleased(MouseEvent e) {
				_window_grabbed = false;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point mouse_pos = SwingUtilities.convertPoint(
					e.getComponent(), 
					e.getPoint(),    
					pane_info.get_pane()
				);

				final int FORGIVENESS = 10;

				int left_border = FORGIVENESS;
				int right_border = (int)pane_info.get_win_size().getWidth() - FORGIVENESS;
				int top_border = FORGIVENESS;
				int bottom_border = (int)pane_info.get_win_size().getHeight() - FORGIVENESS;
						
				mouse_pos.x = Math.clamp(mouse_pos.x, left_border, right_border);
				mouse_pos.y = Math.clamp(mouse_pos.y, top_border, bottom_border);
				
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
