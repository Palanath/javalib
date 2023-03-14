package pala.libs.generic.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Tree<E> {

	private E element;
	private final Set<Tree<E>> children = new HashSet<Tree<E>>(0);
	private Tree<E> parent;

	public E getElement() {
		return element;
	}

	public int getBranchCount() {
		return children.size();
	}

	public void setElement(E element) {
		this.element = element;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public Tree<E> getParent() {
		return parent;
	}

	public Set<Tree<E>> getChildren() {
		return Collections.unmodifiableSet(children);
	}

	/**
	 * Adds the provided {@link Tree} as a branch of this {@link Tree}. If the
	 * provided node is already a direct child of this tree, the call simply
	 * returns.
	 * 
	 * @param child The child to add to this tree. Cyclic trees are supported,
	 *              although may only be at most one branch of at most one parent.
	 */
	public void addChild(Tree<E> child) {
		if (child == null)
			throw null;
		if (!child.isRoot()) {
			if (child.parent == this)
				return;
			child.parent.children.remove(child);
		}
		child.parent = this;
		children.add(child);
	}

	public void removeChild(Tree<E> child) {
		if (children.remove(child))
			child.parent = null;
	}

	public void addChildren(@SuppressWarnings("unchecked") Tree<E>... children) {
		for (Tree<E> v : children)
			addChild(v);
	}

	public void addChildren(Iterable<? extends Tree<E>> children) {
		for (Tree<E> v : children)
			addChild(v);
	}

	public void removeChildren(@SuppressWarnings("unchecked") Tree<E>... children) {
		for (Tree<E> v : children)
			removeChild(v);
	}

	public void removeChildren(Iterable<? extends Tree<E>> children) {
		for (Tree<E> v : children)
			removeChild(v);
	}

	public Tree() {
	}

}
