package com.family_tree.util;

import java.util.Iterator;

public class Iterators {
	public static class ArrayIterator<T> implements Iterator<T> {
		private int _index = 0;
		private T[] _arr;
		
		public ArrayIterator(T[] arr) {
			_arr = arr;	
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

		

		public T next() {
			return _arr[_index--];	
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


	public static <T> Iterable<T> array_reversed(T[] arr) {
		return () -> new ReversedArrayIterator<T>(arr);
	}

	public static <T> Iterable<EnumerateResult<T>> enumerate(Iterator<T> iterator) {
		return () -> new Enumerator<T>(iterator);
	}

	public static <T> Iterable<EnumerateResult<T>> enumerate(Iterable<T> iterable) {
		return () -> new Enumerator<T>(iterable.iterator());
	}
}
