package editortrees;

import java.util.Iterator;

import editortrees.EditTree.Wrapper;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {

	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to
	// the
	// "publicly visible" effects

	char element;
	Node left, right; // subtrees
	int rank; // inorder position of this node within its own subtree.
	Code balance;
	// Node parent; // You may want this field.
	// Feel free to add other fields that you find useful

	// You will probably want to add several other methods

	// For the following methods, you should fill in the details so that they
	// work correctly

	public Node() {
		this.left = null;
		this.right = null;
		this.rank = -1;
		this.element = '\0';
		this.balance = Code.SAME;
	}

	public Node(char ch) {
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
		this.rank = 0;
		this.element = ch;
		this.balance = Code.SAME;
	}

	public Node copyTree(Iterator<Node> myI) {
		Node current = myI.next();
		Node temp = new Node(current.element);
		temp.balance = current.balance;
		temp.rank = current.rank;

		if (current.left != EditTree.NULL_NODE) {
			// give root a left child
			temp.left = temp.copyTree(myI);
		}
		if (current.right != EditTree.NULL_NODE) {
			// give root a right child
			temp.right = temp.copyTree(myI);
		}
		return temp;
	}

	public int height() {
		if (this == EditTree.NULL_NODE) {
			return -1;
		}
		if (this.balance == Code.LEFT) {
			return 1 + this.left.height();
		}
		return 1 + this.right.height();
	}

	public int size() {
		return -1;
	}

	public Node add(char ch, Wrapper aw) {
		if (this.right != EditTree.NULL_NODE) {
			this.right = this.right.add(ch, aw);
			if (aw.bool)
				return this;
			if (this.balance == Code.SAME) {
				this.balance = Code.RIGHT;
			} else if (this.balance == Code.LEFT) {
				this.balance = Code.SAME;
				aw.bool = true;
			} else {
				aw.bool = true;
				aw.numRotate++;
				return singleLeftRotate(this, this.right);
			}
		} else {
			this.right = new Node(ch);
			if (this.balance == Code.SAME) {
				this.balance = Code.RIGHT;
			} else {
				this.balance = Code.SAME;
				aw.bool = true;
			}
		}
		return this;
	}

	public Node add(char ch, int pos, Wrapper aw) {
		if (this.rank > pos) {
			this.left = this.left.add(ch, pos, aw);
			this.rank++;
			if (!aw.bool) {
				if (this.balance == Code.RIGHT && this.left.balance != Code.SAME) {
					this.balance = Code.SAME;
					aw.bool = true;
				} else if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
				} else if (this.balance == Code.LEFT && this.left.balance == Code.LEFT) {
					aw.bool = true;
					aw.numRotate++;
					return singleRightRotate(this, this.left);
				} else if (this.balance == Code.LEFT && this.left.balance == Code.RIGHT) {
					aw.bool = true;
					aw.numRotate += 2;
					return doubleRightRotate(this, this.left);
				}
			}
			// handle updates knowing that left returned
		} else if (this.rank < pos) {
			this.right = this.right.add(ch, pos - this.rank - 1, aw);
			if (!aw.bool) {
				if (this.balance == Code.LEFT && this.right.balance != Code.SAME) {
					this.balance = Code.SAME;
					aw.bool = true;
				} else if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
				} else if (this.balance == Code.RIGHT && this.right.balance == Code.RIGHT) {
					aw.bool = true;
					aw.numRotate++;
					return singleLeftRotate(this, this.right);
				} else if (this.balance == Code.RIGHT && this.right.balance == Code.LEFT) {
					aw.bool = true;
					aw.numRotate += 2;
					return doubleLeftRotate(this, this.right);
				}
			}
			// handle right return
		} else if (this.rank == pos) {
			if (this.left == EditTree.NULL_NODE) {
				this.left = new Node(ch);
				if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
					this.rank++;
				} else {
					this.balance = Code.SAME;
					aw.bool = true;
					this.rank++;
				}
			} else {
				this.left = this.left.add(ch, aw);
				this.rank++; // New
				if (!aw.bool) {
					if (this.balance == Code.RIGHT && this.left.balance != Code.SAME) {
						this.balance = Code.SAME;
						aw.bool = true;
					} else if (this.balance == Code.SAME) {
						this.balance = Code.LEFT;
					} else if (this.balance == Code.LEFT && this.left.balance == Code.LEFT) {
						aw.bool = true;
						aw.numRotate++;
						return singleRightRotate(this, this.left);

					} else if (this.balance == Code.LEFT && this.left.balance == Code.RIGHT) {
						aw.bool = true;
						aw.numRotate += 2;
						return doubleRightRotate(this, this.left);
					}
				}
			}
		}
		return this;
	}

	public Node doubleRightRotate(Node parent, Node child) {
		// balance codes
		Node grandChild = child.right;
		if (grandChild.balance == Code.LEFT) {
			child.balance = Code.SAME;
			parent.balance = Code.RIGHT;
		} else if (grandChild.balance == Code.RIGHT) {
			child.balance = Code.LEFT;
			parent.balance = Code.SAME;
		} else {
			parent.balance = Code.SAME;
			child.balance = Code.SAME;
		}
		grandChild.balance = Code.SAME;
		// child rearrangement

		Node root = child.right;

		// int rightRank = child.right.rank;
		// parent.rank = parent.rank - child.rank - rightRank - 2;
		// root.rank = child.rank + rightRank + 1;

		parent.rank = parent.rank - child.rank - root.rank - 2;
		root.rank = child.rank + root.rank + 1;

		// Minor edge case boi
		// if(parent.rank == -1) {
		// parent.rank = 0;
		// }
		parent.left = root.right;
		child.right = root.left;
		root.right = parent;
		root.left = child;

		return root;
	}

	public Node doubleLeftRotate(Node parent, Node child) {
		// balance codes
		Node grandChild = child.left;
		if (grandChild.balance == Code.LEFT) {
			parent.balance = Code.SAME;
			child.balance = Code.RIGHT;
		} else if (grandChild.balance == Code.RIGHT) {
			parent.balance = Code.LEFT;
			child.balance = Code.SAME;
		} else {
			parent.balance = Code.SAME;
			child.balance = Code.SAME;
		}
		grandChild.balance = Code.SAME;

		// child rearrangement
		Node root = child.left;
		// int rightRank = root.right.rank;
		// root.rank = parent.rank + root.rank + 1;
		// child.rank = rightRank + 1;

		child.rank = child.rank - root.rank - 1;
		root.rank = parent.rank + root.rank + 1;

		parent.right = root.left;
		child.left = root.right;
		root.left = parent;
		root.right = child;

		return root;
	}

	public Node singleLeftRotate(Node parent, Node child) {
		//EditTree.NULL_NODE.rank = 0;
		child.rank = parent.rank + child.rank + 1;
		// balance code fixin
		if (child.balance == Code.RIGHT) {
			parent.balance = Code.SAME;
			child.balance = Code.SAME;
		} else if (child.balance == Code.SAME) {
			//child.balance = Code.LEFT;
			//parent.balance = Code.RIGHT;
		} else {
			//parent.balance = Code.RIGHT;
			//child.balance = Code.LEFT;
		}
		parent.right = child.left;
		child.left = parent;
		//EditTree.NULL_NODE.rank = -1;

		return child;
	}

	public Node singleRightRotate(Node parent, Node child) {

		parent.rank = parent.rank - child.rank - 1;
		parent.left = child.right;
		child.right = parent;
		if (child.balance == Code.LEFT) {
			parent.balance = Code.SAME;
			child.balance = Code.SAME;

		} else if (child.balance == Code.SAME) {
			//child.balance = Code.RIGHT;
			//parent.balance = Code.LEFT;
		} else {
			//parent.balance = Code.SAME;
			//child.balance = Code.SAME;
		}

		return child;
	}

	public char get(int pos) {
		if (this == EditTree.NULL_NODE) {
			throw new IndexOutOfBoundsException();
		}
		if (this.rank == pos) {
			return this.element;
		}
		if (this.rank > pos) {
			return this.left.get(pos);
		}
		return this.right.get(pos - this.rank - 1);

	}

	public Node delete(int pos, Wrapper dw) {
		if (this.rank > pos) {
			// walking left
			this.rank--;
			this.left = this.left.delete(pos, dw);
			if (!dw.bool) {
				// Fix balance codes and rotate when coming up from the left
				if (this.balance == Code.RIGHT) {
					// Rotation Case
					if (this.right.balance == Code.RIGHT || this.right.balance == Code.SAME) {
						// do single left rotation
						if (this.right.balance == Code.SAME) {
							dw.bool = true;
						}
						dw.numRotate++;
						return singleLeftRotate(this, this.right);
					} else if (this.right.balance == Code.LEFT) {
						// do a double left rotation
						dw.numRotate += 2;
						return doubleLeftRotate(this, this.right);
					}

				} else if (this.balance == Code.LEFT) {
					this.balance = Code.SAME;
				} else {
					// If was =, lean and stop fixing balance codes
					this.balance = Code.RIGHT;
					dw.bool = true;
				}
			}

		} else if (this.rank < pos) {
			// walking right
			this.right = this.right.delete(pos - this.rank - 1, dw);
			if (!dw.bool) {
				// Fix balance codes and rotate when coming up from the right
				if (this.balance == Code.RIGHT) {
					this.balance = Code.SAME;
				} else if (this.balance == Code.LEFT) {
					// Rotation Case
					if (this.left.balance == Code.LEFT || this.left.balance == Code.SAME) {
						// single right rotation
						dw.numRotate++;
						if (this.left.balance == Code.SAME) {
							dw.bool = true;
						}
						return singleRightRotate(this, this.left);
					}

					// double right rotation
					dw.numRotate += 2;
					return doubleRightRotate(this, this.left);

				} else {
					// If was =, lean and stop fixing balance codes
					this.balance = Code.LEFT;
					dw.bool = true;
				}
			}
		} else if (this.rank == pos) {
			// position found

			dw.deletedNode = this;

			if (this.left != EditTree.NULL_NODE && this.right != EditTree.NULL_NODE) {
				this.right = this.right.findSuccessor(dw);
				this.element = dw.node.element;
				if (this.right == EditTree.NULL_NODE && this.left != EditTree.NULL_NODE) {
					// rotate to fix imbalance
					if (this.balance == Code.LEFT) {
						if (this.left.balance == Code.LEFT) {
							// single right
							return singleRightRotate(this, this.left);
						}
						// double right
						return doubleRightRotate(this, this.left);
					}
					this.balance = Code.LEFT;
					dw.bool = true;
				}
				if (!dw.bool) {
					// shift balance left
					if (this.balance == Code.SAME) {
						this.balance = Code.LEFT;
						dw.bool = true;
					} else if (this.balance == Code.RIGHT) {
						this.balance = Code.SAME;
					} else {
						// rotate maybe?
					}
				}
				return this;
			} else if (this.left == EditTree.NULL_NODE) {
				return this.right;
			} else {
				return this.left;
			}
		}

		return this;

	}

	// Pass in nodes left child
	private Node findPredecessor(Wrapper dw) {
		if (this.right != EditTree.NULL_NODE) {
			this.right = this.right.findPredecessor(dw);
			if (!dw.bool) {
				// fix balance on the way up
				if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
					dw.bool = true;
				} else if (this.balance == Code.RIGHT) {
					this.balance = Code.SAME;
				} else {
					if (this.left.balance == Code.LEFT || this.left.balance == Code.SAME) {
						dw.numRotate++;
						return singleRightRotate(this, this.left);
					} else {
						dw.numRotate += 2;
						return doubleRightRotate(this, this.left);
					}
				}
			}
			return this;
		}
		this.balance = Code.SAME;
		dw.node = this;
		return this.left;
	}

	// Pass in node's right child
	private Node findSuccessor(Wrapper dw) {
		if (this.left != EditTree.NULL_NODE) {
			this.left = this.left.findSuccessor(dw);
			this.rank--;
			if (!dw.bool) {
				// fix balance on the way up
				if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
					dw.bool = true;
				} else if (this.balance == Code.LEFT) {
					this.balance = Code.SAME;
				} else {
					if (this.right.balance == Code.RIGHT || this.right.balance == Code.SAME) {
						dw.numRotate++;
						return singleLeftRotate(this, this.right);
					} else {
						dw.numRotate += 2;
						return doubleLeftRotate(this, this.right);
					}
				}
			}
			return this;
		}
		this.balance = Code.SAME;
		dw.node = this;
		return this.right;
	}

	public Node buildFromString(String s) {
		if (s.length() == 0) {
			return EditTree.NULL_NODE;
		}
		Node temp = new Node(s.charAt(s.length() / 2));
		temp.rank = s.length() / 2;
		if (s.length() % 2 != 1) {
			temp.balance = Code.LEFT;
		}
		temp.left = this.buildFromString(s.substring(0, s.length() / 2));
		temp.right = this.buildFromString(s.substring(s.length() / 2 + 1));
		return temp;
	}

	public Node getNode(int index) {
		// Left
		if (this.rank > index) {
			return this.left.getNode(index);
		}
		// Right
		else if (this.rank < index) {
			return this.right.getNode(index - this.rank - 1);
		} else {
			return this;
		}
	}

	public Node concatenate(EditTree other, int myHeight, int size, int targetHeight, Wrapper cw) {
		if (myHeight - targetHeight <= 0) {
			cw.node.left = this;
			cw.node.right = other.getRoot();
			cw.node.rank = size;
			if (myHeight != targetHeight) {
				cw.node.balance = Code.RIGHT;
			}
			return cw.node;
		}
		int newHeight = myHeight - 1;
		if (this.balance == Code.LEFT)
			newHeight--;
		this.right = this.right.concatenate(other, newHeight, size - this.rank - 1, targetHeight, cw);
		// walk back up
		if (cw.bool)
			return this;
		if (this.balance == Code.SAME) {
			this.balance = Code.RIGHT;
		} else if (this.balance == Code.LEFT) {
			this.balance = Code.SAME;
			cw.bool = true;
		} else {

			cw.bool = true;
			cw.numRotate++;
			return singleLeftRotate(this, this.right);
		}
		return this;
	}
	
	public Node concatinateTwo(EditTree other, int myHeight, int size, int targetHeight, Wrapper cw) {
		if (myHeight - targetHeight <= 0) {
			cw.node.right = this;
			cw.node.left = other.getRoot();
			cw.node.rank = size;
			if (myHeight != targetHeight) {
				cw.node.balance = Code.LEFT;
			}
			return cw.node;
		}
		int newHeight = myHeight - 1;
		if (this.balance == Code.RIGHT)
			newHeight--;
		this.left = this.left.concatinateTwo(other, newHeight, size, targetHeight, cw);
		// walk back up
		this.rank += size+1;
		if (cw.bool)
			return this;
		if (this.balance == Code.SAME) {
			this.balance = Code.LEFT;
		} else if (this.balance == Code.RIGHT) {
			this.balance = Code.SAME;
			cw.bool = true;
		} else {
			cw.bool = true;
			cw.numRotate++;
			return singleRightRotate(this, this.left);
		}
		return this;
	}

}