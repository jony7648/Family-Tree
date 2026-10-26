import com.family_tree.panels.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;

import java.util.ArrayList;
import java.util.List;
import com.family_tree.util.*;
import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.structures.interfaces.*;
import com.family_tree.structures.classes.generic.*;
import com.family_tree.algorithms.*;
import com.family_tree.data_storage.Person;
import com.family_tree.draw_objects.DrawNode;
import com.family_tree.structures.classes.node_structures.DrawNodeSearchTree;
import com.family_tree.jcomponents.DrawPanel;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.serialized_structures.*;
import com.family_tree.singletons.*;
import com.family_tree.application_menu.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;

import java.io.File;

/*

String[] edit_menu_options = {"First Name", "Last Name", "Gender", "Birth Date", 
	"Passing Date", "Orgin Country", "Orgin Country", "Description", "Children"};
	*/



public class Main {
	private static NArrayNode<DrawNode> create_tree(JsonNode j_node) {
		NArrayNode<DrawNode> root_node = new NArrayNode<>(new DrawNode());
		root_node.get_value().assign_to_root();


		if (j_node == null || j_node.isArray() == false) {
			return root_node;	
		}

		for (JsonNode json_child : j_node) {
			NArrayNode<DrawNode> draw_child = create_person(json_child);
			root_node.add_child(draw_child);
		}

		return root_node;
	}
	
	private static NArrayNode<DrawNode> create_person(JsonNode j_node) {
		Person person = new Person();
		DrawNode draw_node = new DrawNode(person);
		NArrayNode<DrawNode> narray_node = new NArrayNode<>(draw_node);

		draw_node.set_tree_node(narray_node);
		

		try {
			person.set_first_name(Util.access_json_node_property(j_node, Person.FIRST_NAME_JSON_PROPERTY).asText());
			person.set_last_name(Util.access_json_node_property(j_node, Person.LAST_NAME_JSON_PROPERTY).asText());
			person.set_gender(Util.access_json_node_property(j_node, Person.GENDER_JSON_PROPERTY).asText());
			person.set_birth_date(Util.access_json_node_property(j_node, Person.BIRTH_DATE_JSON_PROPERTY).asText());
			person.set_date_of_passing(Util.access_json_node_property(j_node, Person.PASSING_DATE_JSON_PROPERTY).asText());
			person.set_orgin_country(Util.access_json_node_property(j_node, Person.ORGIN_COUNTRY_JSON_PROPERTY).asText());
			person.set_orgin_province(Util.access_json_node_property(j_node, Person.ORGIN_PROVINCE_JSON_PROPERTY).asText());
			person.set_description(Util.access_json_node_property(j_node, Person.DESCRIPTION_JSON_PROPERTY).asText());
			person.set_image(Util.access_json_node_property(j_node, Person.PHOTO_JSON_PROPERTY).asText());
			
			

			JsonNode child_arr = j_node.get(Person.CHILDREN_JSON_PROPERTY);


			if (child_arr != null && child_arr.isArray()) {
				for (JsonNode child : child_arr) {
					NArrayNode<DrawNode> node = create_person(child);

					narray_node.add_child(node);
				}
			}
		}
		catch (Util.InvalidJsonPropertyException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return narray_node;
	}

	public static void main(String[] args) {
		final String TITLE = "Family Tree";

		final String CONFIG_FILE_PATH = "config.toml";
			
		ObjectMapper mapper = new ObjectMapper();
		String json_str = FileUtil.read_file("test.json");

		NArrayNode<DrawNode> root_node;
		DrawNodeSearchTree node_search_tree = new DrawNodeSearchTree();

		GeoInfo.load_country_file("assets/app_info/countries.txt");

		
		TomlMapper toml_mapper = new TomlMapper();
		AppConfig app_config;

		

		try {	
			app_config = toml_mapper.readValue(new File(CONFIG_FILE_PATH), AppConfig.class);
			
			JsonNode json_node = mapper.readTree(json_str);
			root_node = create_tree(json_node);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}

		//add nodes to the search tree
		for (NArrayNode<DrawNode> node : root_node.post_iter()) {
			node_search_tree.add(node.get_value());	
		}

		Dimension win_dimension = new Dimension(600, 600);




		//mac os menu bar setup
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.application.name", "Family Tree");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		
		javax.swing.SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame(TITLE);
			DrawPanel main_panel = new DrawPanel(frame, root_node, node_search_tree, win_dimension, app_config);
			JMenuBar menu_bar = create_menu_bar(main_panel);
			
			frame.setSize(win_dimension);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(main_panel);

		
			
			frame.setJMenuBar(menu_bar);
			
			frame.setVisible(true);
		});

	}

	public static JMenuBar create_menu_bar(DrawPanel draw_panel) {
		JMenuBar menu_bar = new JMenuBar();
		JMenu new_menu = new JMenu("New");
		
		JMenu edit_menu = new JMenu("Edit");
		JMenu history_menu = new JMenu("History");
		JMenu find_menu = new JMenu("Find");

		
		AppBar.create_menu_items(edit_menu, Person.EditMenuOption.class, (e, edit_option) -> {
			draw_panel.edit_node(edit_option);
		});
		
		menu_bar.add(new_menu);
		menu_bar.add(edit_menu);
		menu_bar.add(history_menu);
		menu_bar.add(find_menu);


		AppBar.create_menu_items(new_menu, AppBarEnums.NewMenuOptions.class, (e, edit_option) -> {
			switch (edit_option) {
				case Node -> draw_panel.display_node_creation_menu();
			}
		});
		AppBar.create_menu_items(find_menu, AppBarEnums.FindMenuOptions.class, (e, edit_option) -> {
			switch (edit_option) {
				case Nodes -> draw_panel.display_node_search_menu();
			}
		});

		return menu_bar;
	}
}
