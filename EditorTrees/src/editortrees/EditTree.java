package editortrees;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import editortrees.Node.Code;

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {

	private Node root;
	private int totalRotates = 0;
	public static final Node NULL_NODE = new Node();
	private int size = 0;

	/**
	 * MILESTONE 1 Construct an empty tree
	 */
	public EditTree() {
		this.root = NULL_NODE;
	}

	/**
	 * MILESTONE 1 Construct a single-node tree whose element is ch
	 * 
	 * @param ch
	 */
	public EditTree(char ch) {
		this.root = new Node(ch);
	}

	/**
	 * MILESTONE 2 Make this tree be a copy of e, with all new nodes, but the
	 * same shape and contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		if (e.root == NULL_NODE) {
			this.root = NULL_NODE;
			return;
		}
		this.size = e.size();
		Iterator<Node> myI = new PreOrderIterator(e.root);
		Node current = myI.next();
		this.root = new Node(current.element);
		this.root.balance = current.balance;
		this.root.rank = current.rank;

		if (current.left != NULL_NODE) {
			// give root a left child
			this.root.left = this.root.copyTree(myI);
		}
		if (current.right != NULL_NODE) {
			// give root a right child
			this.root.right = this.root.copyTree(myI);
		}
	}

	/**
	 * MILESTONE 3 Create an EditTree whose toString is s. This can be done in
	 * O(N) time, where N is the size of the tree (note that repeatedly calling
	 * insert() would be O(N log N), so you need to find a more efficient way to
	 * do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		if (s.length() == 0) {
			return;
		}
		this.root = new Node(s.charAt(s.length() / 2));
		this.root.rank = s.length() / 2;
		if (s.length() % 2 != 1) {
			this.root.balance = Code.LEFT;
		}
		this.root.left = this.root.buildFromString(s.substring(0, s.length() / 2));
		this.root.right = this.root.buildFromString(s.substring(s.length() / 2 + 1));
		this.size = s.length();
	}

	/**
	 * MILESTONE 1 returns the total number of rotations done in this tree since
	 * it was created. A double rotation counts as two.
	 *
	 * @return number of rotations since this tree was created.
	 */
	public int totalRotationCount() {
		return this.totalRotates;
	}

	/**
	 * MILESTONE 1 return the string produced by an inorder traversal of this
	 * tree
	 */
	@Override
	public String toString() {
		Iterator<Node> myI = new InOrderIterator(this.root);
		StringBuilder sb = new StringBuilder();
		while (myI.hasNext()) {
			sb.append(myI.next().element);
		}
		return sb.toString();
	}

	/**
	 * MILESTONE 1 This one asks for more info from each node. You can write it
	 * like the arraylist-based toString() method from the BinarySearchTree
	 * assignment. However, the output isn't just the elements, but the
	 * elements, ranks, and balance codes. Former CSSE230 students recommended
	 * that this method, while making it harder to pass tests initially, saves
	 * them time later since it catches weird errors that occur when you don't
	 * update ranks and balance codes correctly. For the tree with root b and
	 * children a and c, it should return the string: [b1=, a0=, c0=] There are
	 * many more examples in the unit tests.
	 * 
	 * @return The string of elements, ranks, and balance codes, given in a
	 *         pre-order traversal of the tree.
	 */
	public String toDebugString() {
		Iterator<Node> myI = new PreOrderIterator(this.root);
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		while (myI.hasNext()) {
			Node current = myI.next();
			sb.append(current.element);
			sb.append(current.rank);
			sb.append(current.balance);
			if (myI.hasNext())
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch
	 *            character to add to the end of this tree.
	 */
	public void add(char ch) {
		// Notes:
		// 1. Please document chunks of code as you go. Why are you doing what
		// you are doing? Comments written after the code is finalized tend to
		// be useless, since they just say WHAT the code does, line by line,
		// rather than WHY the code was written like that. Six months from now,
		// it's the reasoning behind doing what you did that will be valuable to
		// you!
		// 2. Unit tests are cumulative, and many things are based on add(), so
		// make sure that you get this one correct.

		if (this.root == NULL_NODE) {
			this.root = new Node(ch);
			this.size++;
			return;
		}
		Wrapper aw = new Wrapper();
		root = this.root.add(ch, aw);

		this.totalRotates += aw.numRotate;
		this.size++;
	}

	public class Wrapper {
		public boolean bool = false;
		public int numRotate = 0;
		public Node node = NULL_NODE;
		public Node deletedNode;
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @throws IndexOutOfBoundsException
	 *             if pos is negative or too large for this tree
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {
		if (pos > this.size || pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (this.root == NULL_NODE) {
			this.root = new Node(ch);
			this.size++;
			return;
		}
		Wrapper aw = new Wrapper();
		if (pos == this.size) {
			this.root = this.root.add(ch, aw);
		} else {
			this.root = this.root.add(ch, pos, aw);
		}
		this.totalRotates += aw.numRotate;
		this.size++;
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		return this.root.get(pos);
	}

	/**
	 * MILESTONE 1
	 * 
	 * @return the height of this tree
	 */
	public int height() {
		if (this.root == NULL_NODE)
			return -1;
		return this.root.height();
	}

	/**
	 * MILESTONE 2
	 * 
	 * @return the number of nodes in this tree, not counting the NULL_NODE if
	 *         you have one.
	 */
	public int size() {
		return this.size;
	}

	/**
	 * MILESTONE 2
	 * 
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		// Implementation requirement:
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// The tests assume assume that you will replace it with the
		// *successor*.

		if (pos < 0 || pos >= this.size) {
			throw new IndexOutOfBoundsException();
		}

		Wrapper dw = new Wrapper();
		this.root = this.root.delete(pos, dw);
		this.totalRotates += dw.numRotate;
		this.size--;

		return dw.deletedNode.element; // replace by a real calculation.
	}

	/**
	 * MILESTONE 3, EASY This method operates in O(length*log N), where N is the
	 * size of this tree.
	 * 
	 * @param pos
	 *            location of the beginning of the string to retrieve
	 * @param length
	 *            length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException
	 *             unless both pos and pos+length-1 are legitimate indexes
	 *             within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		if (pos < 0 || pos + length > this.size()) {
			throw new IndexOutOfBoundsException();
		}
		Node node;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < length; i++) {
			node = this.root.getNode(pos + i);
			sb.append(node.element);
		}
		return sb.toString();
	}

	/**
	 * MILESTONE 3, MEDIUM - SEE THE PAPER REFERENCED IN THE SPEC FOR ALGORITHM!
	 * Append (in time proportional to the log of the size of the larger tree)
	 * the contents of the other tree to this one. Other should be made empty
	 * after this operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException
	 *             if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {
		if(this == other){
			throw new IllegalArgumentException();
		}
		if(other.size == 0){
			return;
		}
		if(this.size == 0){
			this.root = other.root;
			this.size = other.size;
			other.size = 0;
			other.root = NULL_NODE;
			return;
		}
		int t1 = this.height();
		int t2 = other.height();
		Wrapper cw = new Wrapper();
		if (t1 > t2) {
			// call Recursion(t1,t2)
			cw.node = new Node(other.delete(0));
			this.root = this.root.concatenate(other, this.height(), this.size, other.height(), cw);
		} else if (t2 > t1) {
			// call Recursion(t2,t1)
			cw.node = new Node(this.delete(this.size-1));
			this.root = other.root.concatinateTwo(this, other.height(), this.size, this.height(), cw);
		} else {
			// trees equal
			char rootChar = other.delete(0);
			Node newRoot = new Node(rootChar);
			newRoot.left = this.root;
			newRoot.right = other.root;
			newRoot.rank = this.size;
			this.root = newRoot;
			if(other.height() != t2){
				this.root.balance = Code.LEFT;
			}
		}
		this.size += other.size;
		other.root = NULL_NODE;

	}

	/**
	 * MILESTONE 3: DIFFICULT This operation must be done in time proportional
	 * to the height of this tree.
	 * 
	 * @param pos
	 *            where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this
	 *         tree.
	 * @throws IndexOutOfBoundsException
	 */
	public EditTree split(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= this.size) {
			throw new IndexOutOfBoundsException();
		}
		
		EditTree tree1 = new EditTree();
		EditTree tree2 = new EditTree();
		InOrderIterator iter = new InOrderIterator(this.root);
		for(int i = 0; i < pos; i++) {
			tree1.add(iter.next().element);
		}
		for(int i = pos; i < this.size; i++) {
			tree2.add(iter.next().element);
		}
		this.root = tree1.root;
		return tree2; // replace by a real calculation.
	}

	/**
	 * MILESTONE 3: JUST READ IT FOR USE OF SPLIT/CONCATENATE This method is
	 * provided for you, and should not need to be changed. If split() and
	 * concatenate() are O(log N) operations as required, delete should also be
	 * O(log N)
	 * 
	 * @param start
	 *            position of beginning of string to delete
	 * 
	 * @param length
	 *            length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException
	 *             unless both start and start+length-1 are in range for this
	 *             tree.
	 */
	public EditTree delete(int start, int length) throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.size())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete" : "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * MILESTONE 3 Don't worry if you can't do this one efficiently.
	 * 
	 * @param s
	 *            the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s
	 *         does not occur
	 */
	public int find(String s) {
		return find(s, 0);
	}

	public int findHelper(String s, int pos, InOrderIterator myI) {
		int i = 0;
		char current = myI.next().element;
		while (i < pos) {
			current = myI.next().element;
			i++;
		}
		while (s.charAt(i - pos) == current) {
			if (i - pos == s.length() - 1) {
				return pos;
			}
			current = myI.next().element;
			i++;
		}
		return -1;
	}

	/**
	 * MILESTONE 3
	 * 
	 * @param s
	 *            the string to search for
	 * @param pos
	 *            the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does
	 *         not occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		for (int i = pos; i < this.size - s.length() + 1; i++) {
			if (s.equals(get(i, s.length())))
				return i;
		}
		return -1;
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}

	public class InOrderIterator implements Iterator<Node> {
		private Stack<Node> nodes;

		public InOrderIterator(Node root) {
			this.nodes = new Stack<Node>();
			Node current = root;
			while (current != NULL_NODE) {
				this.nodes.push(current);
				current = current.left;
			}
		}

		@Override
		public boolean hasNext() {
			return !this.nodes.isEmpty();
		}

		@Override
		public Node next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Node temp = this.nodes.pop();
			if (temp.right != NULL_NODE) {
				Node current = temp.right;
				while (current != NULL_NODE) {
					this.nodes.push(current);
					current = current.left;
				}
			}
			return temp;
		}

	}

	public class PreOrderIterator implements Iterator<Node> {
		private Stack<Node> nodes;

		public PreOrderIterator(Node root) {
			this.nodes = new Stack<Node>();
			if (root != NULL_NODE)
				this.nodes.push(root);
		}

		@Override
		public boolean hasNext() {
			return !this.nodes.isEmpty();
		}

		@Override
		public Node next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Node current = this.nodes.pop();
			if (current.right != NULL_NODE)
				this.nodes.push(current.right);
			if (current.left != NULL_NODE)
				this.nodes.push(current.left);
			return current;
		}

	}
}
