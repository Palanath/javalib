package pala.libs.generic.data.files;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import pala.libs.generic.json.JSONObject;
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
		private final Predicate<? super V> defaultValueChecker;

		/**
		 * <p>
		 * Constructs a new {@link DefaultProperty} with the provided initial value, the
		 * provided name, and the provided default value checker {@link Predicate}.
		 * </p>
		 * <p>
		 * The {@link DefaultProperty} is initialized so that its initial value is as
		 * provided. The provided name is used to identify the {@link DefaultProperty}
		 * when saving and loading the surrounding {@link PropertyObject} from a
		 * {@link JSONObject}.
		 * </p>
		 * <p>
		 * The default value checker {@link Predicate} is used to determine if the
		 * {@link DefaultProperty} should be saved into the JSON output by
		 * {@link PropertyObject#toJSON()} during saving. The {@link Predicate} is
		 * provided the value of the {@link DefaultProperty} during the saving process.
		 * If the {@link Predicate} returns <code>false</code>, the
		 * {@link DefaultProperty} is not saved. Otherwise, it is.
		 * </p>
		 * <p>
		 * When a {@link PropertyObject} is restored from a {@link JSONObject},
		 * 
		 * @param value
		 * @param name
		 * @param defaultValueChecker
		 */
		public DefaultProperty(V value, String name, Predicate<? super V> defaultValueChecker) {
			super(value, name);
			this.defaultValueChecker = defaultValueChecker;
		}

		public DefaultProperty(String name, Predicate<? super V> defaultValueChecker) {
			super(name);
			this.defaultValueChecker = defaultValueChecker;
		}

	}

	/**
	 * <p>
	 * Used by {@link Property Properties} to indicate to the saving mechanism that
	 * a property should not be saved at all. The saving mechanism also provides
	 * {@link Property Properties} with this value whenever restoring a
	 * {@link PropertyObject} from JSON data if no value for that {@link Property}
	 * was found within the JSON data.
	 * </p>
	 * <p>
	 * The respective functions for handling these behavior are
	 * {@link Property#toJSON(Object)} and {@link Property#fromJSON(JSONValue)}.
	 * </p>
	 */
	public static final JSONValue NOT_WRITTEN = new JSONValue() {
		/**
		 * Serial UID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public String toString(String indentation) {
			return null;
		}
	};

	public abstract class Property<V> {
		private V value;
		private final String name;

		public String getName() {
			return name;
		}

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

		/**
		 * <p>
		 * Converts the provided {@link JSONValue} into a value that will be stored in
		 * this {@link Property} during a loading pass. This method should also handle
		 * the {@link PropertyObject#NOT_WRITTEN} special value, used to indicate that
		 * no value for this {@link Property} was contained within the
		 * {@link JSONObject} that the surrounding {@link PropertyObject} was loaded
		 * from.
		 * </p>
		 * <h2>Example</h2>
		 * <p>
		 * For properties that are <b>required</b>, a typical implementation will check
		 * if the provided {@link JSONValue} is {@link PropertyObject#NOT_WRITTEN} or of
		 * the appropriate type and handle accordingly. For example, an implementation
		 * may be as follows:
		 * </p>
		 * 
		 * <pre>
		 * <code>if (json == PropertyObject.NOT_WRITTEN)
		 * 	throw new PropertyRequiredException(this);
		 * else if (!(json instanceof JSONString))
		 * 	throw new InvalidJSONException("Invalid JSON value for property " + getName() + '.', this, json);
		 * else
		 * 	return ((JSONString) json).getValue();</code>
		 * </pre>
		 * 
		 * <p>
		 * For properties that are not required, implementations typically return a
		 * "default value" when the provided JSON data is
		 * {@link PropertyObject#NOT_WRITTEN}:
		 * </p>
		 * 
		 * <pre>
		 * <code>if (json == PropertyObject.NOT_WRITTEN)
		 * 	return "default value";
		 * else if (!(json instanceof JSONString))
		 * 	throw new InvalidJSONException("Invalid JSON value for property " + getName() + '.', this, json);
		 * else
		 * 	return ((JSONString) json).getValue();</code>
		 * </pre>
		 * 
		 * <p>
		 * <b>Note</b> that if the value for this property is found to be the JSON
		 * <code>null</code> constant, then <code>null</code> is provided to this method
		 * when this method is called.
		 * </p>
		 * 
		 * @param json The {@link JSONValue} to load from, possibly <code>null</code> or
		 *             {@link PropertyObject#NOT_WRITTEN}.
		 * @return The value loaded, which this {@link Property}'s value will be set to.
		 * @throws PropertyException If an exception related to the loading of the value
		 *                           occurs.
		 */
		public abstract V fromJSON(JSONValue json) throws PropertyException;

		/**
		 * <p>
		 * Converts the provided value to JSON format, so that it may be saved. This
		 * method is called when the surrounding {@link PropertyObject} is being saved
		 * in order to convert each {@link Property} into a format that can be written.
		 * </p>
		 * <p>
		 * If this {@link Property} should not be written to the JSON data of the
		 * surrounding {@link PropertyObject}, this method should return
		 * {@link PropertyObject#NOT_WRITTEN}.
		 * </p>
		 * <p>
		 * Note that this method may return <code>null</code> to write the JSON
		 * <code>null</code> constant as this {@link Property}'s value.
		 * </p>
		 * 
		 * @param value The actual value of this property.
		 * @return The JSON value of this property. Can be <code>null</code> or
		 *         {@link PropertyObject#NOT_WRITTEN}.
		 */
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
