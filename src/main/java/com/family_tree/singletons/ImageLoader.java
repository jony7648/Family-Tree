package com.family_tree.singletons;

import java.util.HashMap;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.io.File;
import java.nio.file.Path;
import com.family_tree.constants.*;
import java.nio.file.FileSystemException;


public class ImageLoader {
	public static class ImageReference {
		Path _image_path;
		Image _image;
		int _ref_count = 1;
		
		public ImageReference(Path path, Image image) {
			_image_path = path;
			_image = image;
		}

		public ImageReference() {
			this(Path.of(""), ImageLoader._image_not_found_image);
		}


		private ImageReference add_reference() {
			_ref_count++;
			return this;
		}

		private void remove_reference() {
			_ref_count--;
		}
		
		private boolean should_remove() {
			return _ref_count <= 0;
		}

		private void clear() {
			_image = null;
		}

		private Path get_path() {
			return _image_path;
		}

		public Image get_image() {
			return _image;
		}
	}
	
	public final static Path IMAGE_NOT_FOUND_IMAGE_PATH = Path.of(DirPaths.ERROR_IMAGES_DIR + "/no_image_found.png");
	private static Image _image_not_found_image;
	private static HashMap<String, ImageReference> _image_map = new HashMap<>();

	public static void init() throws FileSystemException {
		try {
			_image_not_found_image = load_error_image();
		}
		catch (Exception e) {
			throw new FileSystemException("Error initilizing the image loader!");
		}
	}

	public static ImageReference request_image(Path path) {
		path = path.toAbsolutePath();
		String str_path = path.toString();

		//if the image is already loaded return a refrence to it
		if (_image_map.containsKey(str_path)) {
			return _image_map.get(str_path).add_reference();
		}

		//if the image is not loaded create it and return it's refrence
		Image image;
		ImageReference image_refrence;

		try {
			image = ImageIO.read(new File(path.toString()));
			image_refrence = new ImageReference(path, image);
			_image_map.put(path.toString(), image_refrence);
		}
		catch(Exception e) {
			System.out.println("File at path: \"" + path + "\" could not be read!");	
			e.printStackTrace();
			image_refrence = new ImageReference();
		}


		return image_refrence;
	}

	public static void derefrence_image(ImageReference reference) throws IllegalStateException {
		if (!_image_map.containsKey(reference.get_path().toString())) {
			throw new IllegalStateException("Attepted to derefrence a key not in the image map");	
		}

		reference.remove_reference();
		
		if (reference.should_remove()) {
			_image_map.remove(reference.get_path().toString());
			reference.clear();
		}
	}

	public static Image load_error_image() throws FileSystemException {
		Image image;
		
		try {
			image = ImageIO.read(new File(IMAGE_NOT_FOUND_IMAGE_PATH.toString()));
		}
		catch (Exception e) {
			throw new FileSystemException("Failed loading the error image at path: + \"" + _image_not_found_image + "\"");
		}

		return image;
	}

	public static int size() {
		return _image_map.size();
	}
}
