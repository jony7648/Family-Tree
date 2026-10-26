package com.family_tree.singletons;

import java.util.HashMap;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.io.File;


public class ImageLoader {
	public class ImageRefrence {
		Image _image;
		int _ref_count = 1;
		
		public ImageRefrence(Image image) {
			_image = image;
		}

		public Image add_refrence() {
			_ref_count++;
			return _image;
		}

		public Image remove_refrence() {
			_ref_count--;
			return _image;
		}
		
		public boolean should_remove() {
			return _ref_count <= 0;
		}
	}
	
	private HashMap<String, ImageRefrence> _image_map = new HashMap<>();
	
	
	 public Image request_image(String path) {
		 if (_image_map.containsKey(path)) {
			return _image_map.get(path).add_refrence();
		 }

			
		Image image = ImageIO.read(new File(path));

		 _image_map.put(path, new ImageRefrence(image));
	 }

}
