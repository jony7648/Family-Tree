package com.family_tree.algorithms;

import java.util.List;
import java.util.Collection;
import com.family_tree.structures.classes.node_structures.LinkedList;
import com.family_tree.structures.classes.node_structures.PositionalList;
import com.family_tree.draw_objects.DrawNode;

public class Sort {
	public interface ISortable {
		long get_order();
	}


	public static class OrderObject<T> implements ISortable {
		private int order = 0;
		public T object;	

		public OrderObject(int order, T object) {
			this.order = order;
			this.object = object;
		}

		@Override
		public long get_order() {
			return order;
		}

		public T get_object() {
			return object;
		}
	}
	

	
	static<T> void swap_sortable(PositionalList<DrawNode> list, int index1, int index2) {
		DrawNode tmp = list.get(index1);

		list.set(index1, list.get(index2));
		list.set(index2, tmp);
	}

	public static<T> void qsort(PositionalList<DrawNode> list, int start, int end) {
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
			i_num = list.get(i).get_order();
			p_num = list.get(p).get_order();
			
			if (p_num > i_num) {
				swap_sortable(list, i, p);	
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
				j_num = list.get(j).get_order();

				if (p_num > j_num) {
					swap_sortable(list, p, j);

					i++;
					p++;
					break;
				}
			} while (i < j);
		}


		qsort(list, start, p);	
		qsort(list, i, end);
	}
}

