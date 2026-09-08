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

import com.family_tree.math_classes.Geometry.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class NodeDirectionTracker {
	enum Direction {
		CENTER,
		LEFT,
		RIGHT,
	}
	
	public int furthest = 0;
	public Direction direction = Direction.LEFT;
	
	public NodeDirectionTracker(Direction direction) {
		this.direction = direction;
	}
	
}

public class Main {
	private static NArrayNode<DrawNode> create_tree(JsonNode j_node) {
		NArrayNode<DrawNode> root_node = new NArrayNode<>(new DrawNode(new Person()));
		root_node.get_value().assign_to_root();


		if (j_node == null || j_node.isArray() == false) {
			return root_node;	
		}

		for (JsonNode json_child : j_node) {
			NodeDirectionTracker dir_tracker = new NodeDirectionTracker(NodeDirectionTracker.Direction.LEFT);
			NArrayNode<DrawNode> draw_child = create_person(json_child, dir_tracker);
			root_node.add_child(draw_child);
		}

		return root_node;
	}
	
	private static NArrayNode<DrawNode> create_person(JsonNode j_node, NodeDirectionTracker dir_tracker) {
		Person person = new Person();
		DrawNode draw_node = new DrawNode(person);
		NArrayNode<DrawNode> narray_node = new NArrayNode<>(draw_node);


		try {
			person.set_first_name(Util.access_json_node_property(j_node, Person.FIRST_NAME_JSON_PROPERTY).asText());
			person.set_last_name(Util.access_json_node_property(j_node, Person.LAST_NAME_JSON_PROPERTY).asText());
			person.set_gender(Util.access_json_node_property(j_node, Person.GENDER_JSON_PROPERTY).asText());
			person.set_birth_date(Util.access_json_node_property(j_node, Person.BIRTH_DATE_JSON_PROPERTY).asText());
			person.set_date_of_passing(Util.access_json_node_property(j_node, Person.PASSING_DATE_JSON_PROPERTY).asText());
			person.set_orgin_country(Util.access_json_node_property(j_node, Person.ORGIN_COUNTRY_JSON_PROPERTY).asText());
			person.set_orgin_province(Util.access_json_node_property(j_node, Person.ORGIN_PROVINCE_JSON_PROPERTY).asText());
			person.set_description(Util.access_json_node_property(j_node, Person.DESCRIPTION_JSON_PROPERTY).asText());
			
			JsonNode child_arr = j_node.get(Person.CHILDREN_JSON_PROPERTY);

			if (child_arr != null && child_arr.isArray()) {
				for (JsonNode child : child_arr) {
					narray_node.add_child(create_person(child, dir_tracker));
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
			
		ObjectMapper mapper = new ObjectMapper();
		String json_str = FileUtil.read_file("test.json");


		NArrayNode<DrawNode> root_node;

		try {	
			JsonNode json_node = mapper.readTree(json_str);
			root_node = create_tree(json_node);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}


		Dimension win_dimension = new Dimension(600, 600);

		JFrame frame = new JFrame(TITLE);

		MainPanel main_panel = new MainPanel(win_dimension, root_node);

	

		frame.setSize(win_dimension);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(main_panel);

		frame.setVisible(true);
	}
}
