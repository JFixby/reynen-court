
package com.jfixby.reynencourt.demo.storage.v001;

import java.util.ArrayList;

import com.jfixby.scarabei.api.log.L;

class Node<T> {
	/**
	 *
	 */
	private final SimpleStorageIndex Node;

	/** @param simpleStorageIndex */
	public Node (final SimpleStorageIndex simpleStorageIndex) {
		this.Node = simpleStorageIndex;
	}

	private final ArrayList<Node<T>> left = new ArrayList<>();
	private final ArrayList<Node<T>> right = new ArrayList<>();
	T content;

	public int maxRightLevel () {
		return this.right.size() - 1;
	}

	public int maxLeftLevel () {
		return this.left.size() - 1;
	}

	@Override
	public String toString () {
		return this.toString(0);
	}

	public String toString (final int level) {
		String left = "<-";
		if (this.getLeft(level) == null) {
			left = "x-";
		}
		String right = "->";
		if (this.getRight(level) == null) {
			right = "-x";
		}

		String print = "";

		if (new Integer(96).equals(this.content)) {
			L.d();
			this.printLevels();
		}

		if (level == 0) {
			if (this.content != null) {
				print = this.content + "";
			} else {
				print = "...";
			}
		} else if (level == this.maxNodeLevel()) {
			print = "T ";
		}

		return left + String.format("%3s", print) + right;

	}

	int maxNodeLevel () {
		return Math.max(this.maxLeftLevel(), this.maxRightLevel());
	}

	public void clearLeft () {
		this.left.clear();

	}

	public void clearRight () {
		this.right.clear();

	}

	public void addLeft (final Node<T> left) {
		this.left.add(left);

	}

	public void addRight (final Node<T> right) {
		this.right.add(right);

	}

	public Node<T> getRight (final int level) {
		if (this.right.size() <= level) {
			return null;
		}
		return this.right.get(level);
	}

	public Node<T> getLeft (final int level) {
		if (this.left.size() <= level) {
			return null;
		}
		return this.left.get(level);
	}

	public void setLeft (final int level, final Node<T> newNode) {
		this.left.set(level, newNode);
	}

	public void setRight (final int level, final Node<T> newNode) {
		this.right.set(level, newNode);
	}

	public void printLevels () {
		for (int i = this.maxNodeLevel(); i >= 0; i--) {
			final String l = this.getLeft(i) + "";
			final String r = this.getRight(i) + "";
			L.d(l + " :" + i + ": " + r);
		}
	}

}
