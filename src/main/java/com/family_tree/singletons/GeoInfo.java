package com.family_tree.singletons;

import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.stream.Stream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GeoInfo {
	private static String[] _country_arr = {};

	public static void load_country_file(String file_path) {
		Path path = Paths.get(file_path);	
		
		try (Stream<String> lines = Files.lines(path)) {
			_country_arr = lines.toArray(String[]::new);
		}
		catch (Exception e) {
			System.out.println("Geo info file failed to load!");	
			e.printStackTrace();
		}
	}

	public static String[] get_country_arr() {
		return _country_arr;
	}
}
