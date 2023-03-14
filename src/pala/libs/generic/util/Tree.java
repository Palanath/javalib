package pala.libs.generic.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import pala.libs.generic.JavaTools;

public class Tree<E> implements Iterable<E> {

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

	public Tree<E> addChild(E child) {
		Tree<E> t = new Tree<>();
		t.setElement(child);
		addChild(t);
		return t;
	}

	/**
	 * Removes the first occurrence of a node with the specified element that is an
	 * <i>immediate</i> child of this {@link Tree} node.
	 * 
	 * @param childElem The element. The first child node that contains the
	 *                  specified element is removed.
	 * @return The removed {@link Tree} node. If not found, this method returns
	 *         <code>null</code>.
	 */
	public Tree<E> removeChild(E childElem) {
		for (Tree<E> c : children)
			if (c.getElement() == childElem) {
				removeChild(c);
				return c;
			}
		return null;
	}

	public boolean isLeaf() {
		return children.isEmpty();
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

	/**
	 * Recrusively creates an iterator that iterates over this node's element, then
	 * all the elements of the children of this node. This method can have high
	 * overhead because of it's implementation. Care should be taken when iterating
	 * over large trees, however the iterator returned is detached from this
	 * {@link Tree} and can be used independently of it; subsequent modifications to
	 * this {@link Tree} that happen during iteration on the iterator do not affect
	 * the iterator, although this tree should not be modified while the iterator is
	 * being <i>constructed</i>.
	 */
	@Override
	public Iterator<E> iterator() {
		Iterator<E> itr = JavaTools.iterator(element);
		for (Tree<E> child : children)
			itr = JavaTools.concat(itr, child.iterator());
		return itr;
	}

	public void collect(Consumer<? super E> consumer) {
		consumer.accept(element);
		for (Tree<E> c : children)
			c.collect(consumer);
	}

}
