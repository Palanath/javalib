package pala.libs.generic.data.files;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pala.libs.generic.json.JSONSavable;
import pala.libs.generic.json.JSONValue;

public abstract class PropertyObject implements JSONSavable {

	public class Edit {
		private final Map<Property<?>, Object> changes = new HashMap<>(2);

		public <V> void set(Property<? super V> property, V value) {
			changes.put(property, value);
		}

		public void undo(Property<?> property) {
			changes.remove(property);
		}

		public void commit() {
			commitWithoutDirtying();
			markDirty();
		}

		@SuppressWarnings("unchecked")
		public void commitWithoutDirtying() {
			for (Entry<Property<?>, Object> e : changes.entrySet())
				((Property<Object>) e.getKey()).value = e.getValue();
		}

	}

	public abstract class DefaultProperty<V> extends Property<V> {

	}

	public abstract class Property<V> {
		private V value;
		private final String name;

		/**
		 * Updates the value of this {@link Property}, causing the
		 * {@link PropertyObject} it belongs to be {@link PropertyObject#markDirty()
		 * marked dirty}.
		 * 
		 * @param value The value to update the {@link Property} to.
		 */
		public void update(V value) {
			this.value = value;
			markDirty();
		}

		public V get() {
			return value;
		}

		public abstract V fromJSON(JSONValue json) throws InvalidJSONException;

		public abstract JSONValue toJSON(V value);

		/**
		 * Constructs a {@link Property} with the provided initial value and name.
		 * Calling this constructor does not mark the surrounding {@link PropertyObject}
		 * as dirty.
		 * 
		 * @param value The initial value of the {@link Property}. Calls to
		 *              {@link #get()} after construction will return the provided value
		 *              and the provided value will be saved when the surrounding
		 *              {@link PropertyObject} is saved, unless this {@link Property}'s
		 *              value is changed before saving.
		 * @param name  The name of the property. Must be unique against all other
		 *              properties in thi {@link PropertyObject}. Used to uniquely
		 *              identify the data in the file.
		 */
		public Property(V value, String name) {
			this.value = value;
			this.name = name;
		}

		/**
		 * Constructs a {@link Property} with the provided name. The property's initial
		 * value is <code>null</code>.
		 * 
		 * @param name The name of the property. Must be unique against all other
		 *             properties in thi {@link PropertyObject}. Used to uniquely
		 *             identify the data in the file.
		 */
		public Property(String name) {
			this.name = name;
		}

	}

	protected abstract void markDirty();

	@Override
	public JSONValue toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
