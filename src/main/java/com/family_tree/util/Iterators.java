package com.family_tree.util;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class Iterators {
	public static class EmptyIterator<T> implements Iterator<T> {
		public T next() {return null;};	
		public boolean hasNext() {return false;};
	}
	
	public static class ArrayIterator<T> implements Iterator<T> {
		private int _index = 0;
		private T[] _arr;

		public ArrayIterator(T[] arr) {
			_arr = arr;	
		}

		public ArrayIterator(T[] arr, int start_index) {
			_arr = arr;	
			_index = start_index;
		}


		public T next() {
			return _arr[_index++];	
		}

		public boolean hasNext() {
			return _index < _arr.length;
		}
	}

	public static class ReversedArrayIterator<T> implements Iterator<T> {
		private int _index = 0;
		private T[] _arr;
		
		public ReversedArrayIterator(T[] arr) {
			_arr = arr;	
			_index = _arr.length - 1;
		}

		public ReversedArrayIterator(T[] arr, int start_index) {
			_arr = arr;	
			_index = start_index;
		}

		public T next() {
			return _arr[_index--];	
		}

		public boolean hasNext() {
			return _index >= 0;
		}
	}
	public static class ListIterator<T> implements Iterator<T> {
		private int _index = 0;
		private List<T> _list;
		
		public ListIterator(List<T> arr) {
			_list = arr;	
		}

		public ListIterator(List<T> arr, int start_index) {
			_list = arr;	
			_index = start_index;
		}

		public T next() {
			return _list.get(_index++);	
		}

		public boolean hasNext() {
			return _index < _list.size();
		}
	}

	public static class ReversedListIterator<T> implements Iterator<T> {
		private int _index = 0;
		private List<T> _list;
		
		public ReversedListIterator(List<T> list) {
			_list = list;	
			_index = _list.size() - 1;
		}

		public ReversedListIterator(List<T> list, int start_index) {
			_list = list;	
			_index = start_index;
		}

		public T next() {
			return _list.get(_index--);	
		}

		public boolean hasNext() {
			return _index >= 0;
		}
	}

	public static class EnumerateResult<T> {
		public T value;
		public int index;

		public EnumerateResult(int index, T value) {
			this.value = value;
			this.index = index;
		}
	}

	public static class Enumerator<T> implements Iterator<EnumerateResult<T>>{
		Iterator<T> _iterator;

		private int _index = 0;
		
		public Enumerator(Iterator<T> iterator) {
			_iterator = iterator;	
		}

		public EnumerateResult<T> next() {
			return new EnumerateResult<T>(_index++, _iterator.next());
				
		}

		public boolean hasNext() {
			return _iterator.hasNext();	
		}
	}

	public static <T> Iterable<T> array_iterator(T[] arr) {
		return () -> new ArrayIterator<T>(arr);
	}
	public static <T> Iterable<T> array_iterator(T[] arr, int start_index) {
		return () -> new ArrayIterator<T>(arr, start_index);
	}
	public static <T> Iterable<T> array_reversed(T[] arr) {
		return () -> new ReversedArrayIterator<T>(arr);
	}
	public static <T> Iterable<T> array_reversed(T[] arr, int start_index) {
		return () -> new ReversedArrayIterator<T>(arr, start_index);
	}
	public static <T> Iterable<T> list_iterator(List<T> arr) {
		return () -> new ListIterator<T>(arr);
	}
	public static <T> Iterable<T> list_iterator(List<T> arr, int start_index) {
		return () -> new ListIterator<T>(arr, start_index);
	}
	public static <T> Iterable<T> list_reversed(List<T> arr) {
		return () -> new ReversedListIterator<T>(arr);
	}
	public static <T> Iterable<T> list_reversed(List<T> arr, int start_index) {
		return () -> new ReversedListIterator<T>(arr, start_index);
	}

	public static <T> Iterable<EnumerateResult<T>> enumerate(Iterator<T> iterator) {
		return () -> new Enumerator<T>(iterator);
	}

	public static <T> Iterator<T> empty_iterate() {
		return new EmptyIterator<T>();
	}

	public static <T> Iterable<EnumerateResult<T>> enumerate(Iterable<T> iterable) {
		return () -> new Enumerator<T>(iterable.iterator());
	}
}
