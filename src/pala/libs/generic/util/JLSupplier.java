package pala.libs.generic.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import pala.libs.generic.JavaTools;

public interface JLSupplier<T> extends Supplier<T>, Iterable<T> {
	default List<T> collectList(int amount) {
		List<T> res = new ArrayList<>(amount);
		for (; amount > 0; amount--)
			res.add(get());
		return res;
	}

	default <L extends Collection<? super T>> L collectInto(L list, int amount) {
		for (; amount > 0; amount--)
			list.add(get());
		return list;
	}

	default T[] collectArray(int amount) {
		T[] arr = JavaTools.array(amount);
		for (int i = 0; i < arr.length; i++)
			arr[i] = get();
		return arr;
	}

	default T[] fill(T[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = get();
		return array;
	}

	@Override
	default Iterator<T> iterator() {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public T next() {
				return get();
			}
		};
	}
}
