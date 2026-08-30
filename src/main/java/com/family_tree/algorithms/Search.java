package com.family_tree.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.family_tree.data_storage.*;
import com.family_tree.structures.classes.node_structures.*;

public class Search {
	public static interface ISearchableObject{
		String get_search_string(int property);
	}
	
	public static ISearchableObject[] fuzzy(Iterable<? extends ISearchableObject> iterable, int property, String search) {
		final int MINIMUM_MATCH_POINTS = 1;


		ArrayList<Sort.OrderObject<ISearchableObject>> entry_list = new ArrayList<>();

		for (ISearchableObject searchable : iterable) {
			String iter_str = searchable.get_search_string(property);

			final int STRING_LEN = iter_str.length();

			int score = 0;

			for (int search_index=0, string_index=0; ; string_index++) {
				
				if (string_index >= STRING_LEN) {
					string_index = 0;
					search_index++;
				}

				//do not access anything with search index before this
				if (search_index >= search.length()) {
					break;
				}

				char name_letter = iter_str.charAt(string_index);	
				char search_letter = search.charAt(search_index);

				if (name_letter == search_letter) {
					//clamp cause searches should always get points for a match
					score += search.length() - string_index + MINIMUM_MATCH_POINTS;
					score = Math.clamp(MINIMUM_MATCH_POINTS, score, Integer.MAX_VALUE);
					search_index++;

				}
			}

			
			Sort.OrderObject<ISearchableObject> entry = new Sort.OrderObject<>(score, searchable);
			entry_list.add(entry);
		}

		Sort.qsort(entry_list, 0, entry_list.size());

		ISearchableObject[] search_array = new ISearchableObject[entry_list.size()];	
		int search_array_index = 0;

		for (Sort.OrderObject<ISearchableObject> order_obj : entry_list.reversed()) {
			search_array[search_array_index] = order_obj.get_object();	
			search_array_index++;
		}

		return search_array;
	}
}
