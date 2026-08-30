package com.family_tree.algorithms;

import java.util.List;

public class Sort {
	public interface ISortable {
		int get_order();
		Object get_object();
		ISortable copy();
	}


	static class OrderObject<T> implements ISortable {
		private int order = 0;
		public T object;	

		public OrderObject(int order, T object) {
			this.order = order;
			this.object = object;
		}

		@Override
		public int get_order() {
			return order;
		}

		@Override
		public OrderObject<T> copy() {
			return new OrderObject<T>(this.order, this.object);
		}

		@Override
		public T get_object() {
			return object;
		}
	}
	
	static<T> void swap_sortable(List<OrderObject<T>> list, int index1, int index2) {
		OrderObject<T> tmp = list.get(index1);

		list.set(index1, list.get(index2));
		list.set(index2, tmp);
	}

	static<T> void qsort(List<OrderObject<T>> list, int start, int end) {
		int i = start + 1;
		int j = end;
		int p = start;

		int i_num = 0;
		int j_num = 0;
		int p_num = 0;

		if (start >= end - 1) {
			System.out.println(start);
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

