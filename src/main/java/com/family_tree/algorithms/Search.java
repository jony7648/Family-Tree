package com.family_tree.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.family_tree.data_storage.*;
import com.family_tree.structures.classes.node_structures.*;
import com.family_tree.draw_objects.*;

public class Search {
	public static interface ISearchableObject {
		String get_search_string(int property);
	}

	public static long calculate_order_value(String src_str, String query_string) {
		final int MINIMUM_MATCH_POINTS = 1;

		final int STRING_LEN = src_str.length();

		long score = 0;

		for (int search_index=0, string_index=0; ; string_index++) {
			if (string_index >= STRING_LEN) {
				string_index = 0;
				search_index++;
			}

			//do not access anything with search index before this
			if (search_index >= query_string.length()) {
				break;
			}

			char name_letter = src_str.charAt(string_index);	
			char search_letter = query_string.charAt(search_index);

			if (name_letter == search_letter) {
				//clamp cause searches should always get points for a match
				score += query_string.length() - string_index + MINIMUM_MATCH_POINTS;
				score = Math.clamp(MINIMUM_MATCH_POINTS, score, Integer.MAX_VALUE);
				search_index++;

			}
		}
		
		//return the negative value allowing for closer matches to be ranked first
		return -score;
	}
	
	public static PositionalList<DrawNode> fuzzy(Iterable<DrawNode> iterable, int property, String search) {
		PositionalList<DrawNode> entry_list = new PositionalList<>();


		
		for (DrawNode node : iterable) {
			entry_list.append(node);
		}

		
		search_sort(search, entry_list, 0, entry_list.get_size());


		for (DrawNode node : entry_list) {
			System.out.println(node.get_person().get_first_name());
		}
		



		return entry_list;
	}

	private static long get_search_score(String query, DrawNode node) {
		return calculate_order_value(node.get_search_string(0), query);	
	}

	public static<T> void search_sort(String query, PositionalList<DrawNode> list, int start, int end) {
		int i = start + 1;
		int j = end;
		int p = start;

		long i_num = 0;
		long j_num = 0;
		long p_num = 0;

		if (start >= end - 1) {
			return;
		}
		
		while (i < j) {
			i_num = get_search_score(query, list.get(i));
			p_num = get_search_score(query, list.get(p));
			
			if (p_num > i_num) {
				Sort.swap_sortable(list, i, p);	
				i++;
				p++;
				continue;
			}

			if (p_num == i_num) {
				i++;
				continue;
			}
			
			do {
				j--;
				j_num = get_search_score(query, list.get(j));

				if (p_num > j_num) {
					Sort.swap_sortable(list, p, j);

					i++;
					p++;
					break;
				}
			} while (i < j);
		}


		search_sort(query, list, start, p);	
		search_sort(query, list, i, end);
	}
}
