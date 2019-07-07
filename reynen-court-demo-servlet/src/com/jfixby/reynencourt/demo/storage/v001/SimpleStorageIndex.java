
package com.jfixby.reynencourt.demo.storage.v001;

import java.io.IOException;
import java.util.ArrayList;

import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.reynencourt.demo.storage.StorageIndex;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.random.Random;

public class SimpleStorageIndex implements StorageIndex {

	public SimpleStorageIndex () {

		this.reset();
	}

	@Override
	public void registerSampleFile (final File sampleFile) {
		try {
			final String raw_json = sampleFile.readToString();
			final DataSample sample = Json.deserializeFromString(DataSample.class, raw_json);
			this.register(sample);
		} catch (final IOException e) {
			Err.reportError(e);
		}
	}

	@Override
	public void reset () {
		this.clear();
	}

	private void register (final DataSample sample) {
		this.addElement(sample);
	}

	@Override
	public Collection<DataSample> queryFromToTimestamp (final long fromTimestamp, final long toTimestamp) {
		final SearchCondition condition = new SearchCondition() {
			@Override
			public boolean satisfy (final DataSample content) {
				return true;
			}
		};

		return this.collectElements(fromTimestamp, toTimestamp, condition);
	}

	private Collection<DataSample> collectElements (final long fromTimestamp, final long toTimestamp,
		final SearchCondition condition) {

		final DataSample search = new DataSample();
		search.timestamp = fromTimestamp;
		Node<DataSample> currentNode = this.findPreNode(search);
		if (currentNode == this.left || currentNode == this.right) {
			return Collections.newList();
		}
		currentNode = currentNode.getRight(0);
		if (currentNode == this.left || currentNode == this.right) {
			return Collections.newList();
		}
		final List<DataSample> result = Collections.newList();
		while (currentNode != this.right && currentNode.content.timestamp <= toTimestamp
			&& condition.satisfy(currentNode.content)) {
			result.add(currentNode.content);
			currentNode = currentNode.getRight(0);
		}
		return result;

	}

	@Override
	public Long aggregateSum (final long fromTimestamp, final long toTimestamp) {
		final DataSample result = new DataSample();
		result.value = 0L;
		final SearchCondition condition = new SearchCondition() {
			@Override
			public boolean satisfy (final DataSample content) {
				result.value = result.value + content.value;
				return true;
			}
		};

		this.collectElements(fromTimestamp, toTimestamp, condition);
		return result.value;
	}

	@Override
	public Long aggregateAverage (final long fromTimestamp, final long toTimestamp) {
		final DataSample result = new DataSample();
		result.value = 0L;
		final SearchCondition condition = new SearchCondition() {
			@Override
			public boolean satisfy (final DataSample content) {
				result.value = result.value + content.value;
				return true;
			}
		};

		final Collection<DataSample> list = this.collectElements(fromTimestamp, toTimestamp, condition);
		return result.value / list.size();
	}

	int size = 0;
	int maxLevel = 0;
	final Node<DataSample> left = new Node<>();
	final Node<DataSample> right = new Node<>();

	public void clear () {
		this.size = 0;
		this.maxLevel = 0;
		this.left.clearRight();
		this.right.clearLeft();
		this.ensureMaxLevel();

	}

	class Node<T> {
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

		private int maxNodeLevel () {
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

	public void addElement (final DataSample element) {
		final Node<DataSample> preNode = this.findPreNode(element);
		final Node<DataSample> newNode = this.insertNodeAfter(preNode, 0);
		newNode.content = element;

		int newLevel = 0;
		while (this.expand()) {
			newLevel++;
		}
		this.maxLevel = Math.max(newLevel, this.maxLevel);
		this.ensureMaxLevel();

		for (int level = 1; level <= newLevel; level++) {
			final Node<DataSample> leftNeighbour = this.findLeftNeighbourAtLevel(newNode, level);
			final Node<DataSample> rightNeighbour = leftNeighbour.getRight(level);

			leftNeighbour.setRight(level, newNode);
			newNode.addLeft(leftNeighbour);

			rightNeighbour.setLeft(level, newNode);
			newNode.addRight(rightNeighbour);

		}

	}

	private Node<DataSample> findLeftNeighbourAtLevel (final Node<DataSample> newNode, final int level) {
		Node<DataSample> current = newNode.getLeft(level - 1);
		while (current.maxRightLevel() < level) {
			current = current.getLeft(level - 1);
		}
		return current;
	}

	private void ensureMaxLevel () {
		for (int i = 0; i <= this.maxLevel; i++) {
			if (this.left.getRight(i) == null) {
				this.left.addRight(this.right);
			}
			if (this.right.getLeft(i) == null) {
				this.right.addLeft(this.left);
			}
		}

	}

	private boolean expand () {
		return Random.newCoin();
	}

	private Node<DataSample> findPreNode (final DataSample element) {
		Node<DataSample> current = this.left;
// L.d("searching", element);
		for (int level = this.maxLevel; level >= 0; level--) {
			while (current != this.right && this.compare(element, current.content) > 0) {
				current = current.getRight(level);
// L.d("jump level=" + level, current.toString(level) + " == " + current.content);
// current.printLevels();
			}
			current = current.getLeft(level);
// L.d("return ", current.toString(0));
		}
		return current;
	}

	private Node<DataSample> insertNodeAfter (final Node<DataSample> current, final int level) {
		final Node<DataSample> newNode = new Node<>();
		newNode.addLeft(current);
		newNode.addRight(current.getRight(level));
		current.getRight(level).setLeft(level, newNode);
		current.setRight(level, newNode);
		this.size++;
		return newNode;
	}

	private int compare (final DataSample a, final DataSample b) {
		if (a == null && b == null) {
			return 0;
		}
		if (a == null && b != null) {
			return -1;
		}
		if (a != null && b == null) {
			return 1;
		}
		return a.compareTo(b);
	}

	public void removeElement (final DataSample element) {
		if (this.size == 0) {
			return;
		}
		final Node<DataSample> preNode = this.findPreNode(element);
		if (preNode.getRight(0) == this.right) {
			return;
		}
		if (this.compare(element, preNode.getRight(0).content) == 0) {
			this.removeNode(preNode.getRight(0));
		}
	}

	public boolean contains (final DataSample element) {
		if (this.size == 0) {
			return false;
		}
		final Node<DataSample> preNode = this.findPreNode(element);
		if (preNode.getRight(0) == this.right) {
			return false;
		}
		if (this.compare(element, preNode.getRight(0).content) == 0) {
			return true;
		}
		return false;
	}

	public DataSample getElementAt (final int index) {
		if (index < 0 || index >= this.size) {
			throw new Error("Index outbound exception: " + index + " size=(" + index + ")");
		}
		final Node<DataSample> targetNode = this.findNodeAt(index);

		return targetNode.content;
	}

	private Node<DataSample> findNodeAt (final int index) {
		Node<DataSample> result = this.left;
		for (int k = 0; k <= index; k++) {
			result = result.getRight(0);
		}
		return result;
	}

	private void removeNode (final Node<DataSample> targetNode) {
		for (int level = 0; level <= targetNode.maxNodeLevel(); level++) {
			targetNode.getLeft(level).setRight(level, targetNode.getRight(level));
			targetNode.getRight(level).setLeft(level, targetNode.getLeft(level));
		}
		this.size--;
		if (this.size == 0) {
			this.clear();
		}
	}

	public void print (final String tag) {
		L.d("---[" + tag + "](" + this.size + ")-----------------------------------------------------");
		for (int level = this.maxLevel; level >= 0; level--) {
			this.print(tag, level);
		}

	}

	private void print (final String tag, final int level) {
// if (level == 0) {
// this.print0(tag, 0);
// return;
// }

		Node<DataSample> current = this.left;
		Node<DataSample> currentL = this.left;

		final StringBuilder t = new StringBuilder();

		while (current != null) {
			if (current == currentL) {
				final String print = current.toString(level);
				t.append("" + String.format("%7s", print));
				currentL = currentL.getRight(level);
			} else {
				t.append("" + String.format("%7s", " "));
			}

			current = current.getRight(0);
		}
		t.insert(0, "(" + level + ") ");
		L.d(t);

	}

	public int size () {
		return this.size;
	}

	static boolean equals (final Object a, final Object b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null && b != null) {
			return false;
		}
		if (a != null && b == null) {
			return false;
		}
		return a.equals(b);
	}

}
