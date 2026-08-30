package com.family_tree.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class FileUtil {
	public static String read_file(String file_path) {
		Path path = Path.of(file_path);
		
		try {
			String content = Files.readString(path);
			return content;
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	
	}
}
