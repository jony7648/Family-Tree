

public class BinarySearchTree<T> {
	public class Node {
		private T _elem;
		private Node _left_node;
		private Node _right_node;

		public void Node(Node left_node, Node right_node, T elem) {
			_elem = elem;
			_left_node = left_node;
			_right_node = right_node;
		}	

		public Node get_left_node() {
			return _left_node;
		}

		public Node get_right_node() {
			return _right_node;
		}
	}
	
	T elem;
	
}
