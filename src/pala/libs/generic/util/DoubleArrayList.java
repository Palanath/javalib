package pala.libs.generic.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

/**
 * <p>
 * A {@link List} implementation backed by {@link ArrayList}(s) that supports
 * amortized constant time complexity for appending to either the end <i>or</i>
 * beginning of this {@link List}. Although, insertions into the middle of the
 * list can incur the overhead associated with shifting every element over in an
 * {@link ArrayList}.
 * </p>
 * <p>
 * This {@link List} keeps track of (up to) two {@link ArrayList}s, one for the
 * "tail" of this list, and one for the "head." The head stores elements
 * <code>1</code> through <code>N</code>, in reverse order, and the tail stores
 * elements <code>N</code> through {@link #size()}.
 * </p>
 * <ul>
 * <li>When an element is appended to the end of this list, it is appended to
 * the end of the <code>tail</code>.</li>
 * <li>When an element is appended to the beginning of this list, it is appended
 * to the end of the <code>head</code>.</li>
 * </ul>
 * <p>
 * This implementation is useful for {@link List}s that generally append
 * elements to their ends rather than insert elements to their middles.
 * </p>
 * <p>
 * This {@link List} begins by storing only one {@link ArrayList}, the
 * <code>tail</code>, until elements begin to be appended to the beginning of
 * the {@link List}. At such point, the <code>head</code> is instantiated and
 * used.
 * </p>
 * 
 * @author Palanath
 *
 * @param <E> The type of element held by this {@link DoubleArrayList}.
 */
public class DoubleArrayList<E> extends AbstractList<E> implements RandomAccess {

	// 0...N...size
	// this

	// N...size
	private final ArrayList<E> tail = new ArrayList<>();
	// N...0
	private ArrayList<E> head;

	public DoubleArrayList() {
	}

	public DoubleArrayList(Collection<? extends E> other) {
		tail.addAll(other);
	}

	private int sizeOfHead() {
		return head == null ? 0 : head.size();
	}

	@Override
	public E get(int index) {
		return head != null ? index < head.size() ? head.get(head.size() - index) : tail.get(index - head.size())
				: tail.get(index);
	}

	@Override
	public void add(int index, E element) {
		if (head != null)
			if (index < head.size())
				head.add(head.size() - index, element);
			else if (index == head.size())
				(head.size() < tail.size() ? head : tail).add(0, element);
			else
				tail.add(index - head.size(), element);
		else if (index == 0)
			(head = new ArrayList<>()).add(element);
		else
			tail.add(index, element);
	}

	@Override
	public E set(int index, E element) {
		if (head != null)
			if (index < head.size())
				return head.set(head.size() - index, element);
			else
				return tail.set(index - head.size(), element);
		else
			return tail.set(index, element);
	}

	@Override
	public E remove(int index) {
		if (head != null)
			if (index < head.size()) {
				E res = head.remove(head.size() - index);
				if (head.isEmpty())
					head = null;
				return res;
			} else
				return tail.remove(index - head.size());
		else
			return tail.remove(index);
	}

	@Override
	public int size() {
		return tail.size() + sizeOfHead();
	}

	@Override
	public boolean contains(Object o) {
		return tail.contains(o) || head != null && head.contains(o);
	}

}
