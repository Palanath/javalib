package pala.libs.generic.data.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import pala.libs.generic.json.JSONConstant;
import pala.libs.generic.json.JSONObject;
import pala.libs.generic.json.JSONSavable;
import pala.libs.generic.json.JSONString;
import pala.libs.generic.json.JSONValue;

public abstract class PropertyObject implements JSONSavable {

	public class Edit {
		private final Map<Property<?>, Object> changes = new HashMap<>(4);

		public Edit commit() {
			commitWithoutDirtying();
			markDirty();
			return this;
		}

		@SuppressWarnings("unchecked")
		public Edit commitWithoutDirtying() {
			for (Entry<Property<?>, Object> e : changes.entrySet())
				((Property<Object>) e.getKey()).value = e.getValue();
			return this;
		}

		public <V> Edit set(Property<? super V> property, V value) {
			changes.put(property, value);
			return this;
		}

		public Edit undo(Property<?> property) {
			changes.remove(property);
			return this;
		}

		/**
		 * Clears all the changes in this {@link Edit}.
		 * 
		 * @return This {@link Edit} object.
		 */
		public Edit clear() {
			changes.clear();
			return this;
		}

	}

	public abstract class Property<V> {
		{
			properties.add(this);
		}
		private V value;

		protected final void setValue(V value) {
			this.value = value;
		}

		protected final V getValue() {
			return value;
		}

		private final String name;

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

		public V get() {
			return value;
		}

		public String getName() {
			return name;
		}

		/**
		 * Updates the value of this {@link PropertyObject} to be as signified by the
		 * provided {@link JSONValue}. This method is called when the surrounding
		 * {@link PropertyObject} is {@link PropertyObject#load(JSONObject) loaded}.
		 * 
		 * @param json The {@link JSONValue} contained in the {@link PropertyObject}'s
		 *             JSON data, possibly equal to <code>null</code> or
		 *             {@link PropertyObject#NOT_WRITTEN}.
		 * @throws PropertyException If a property-related exception occurs while
		 *                           loading the data.
		 */
		protected abstract void load(JSONValue json) throws PropertyException;

		/**
		 * Converts the value of this {@link PropertyObject} to JSON data and returns
		 * it. This method may return <code>null</code> (to signifiy the JSON null
		 * constant) or {@link PropertyObject#NOT_WRITTEN} to signify that data should
		 * not be written to the file. If {@link PropertyObject#NOT_WRITTEN} is
		 * returned, it will be provided to {@link #load(JSONValue)} when the JSON data
		 * is correspondingly read and loaded back.
		 * 
		 * @return What JSON data to write to store this {@link Property}.
		 */
		protected abstract JSONValue save();

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

	}

	public abstract class SimpleProperty<V> extends Property<V> {
		private final V defaultValue;
		private final boolean overwrite, def;

		/**
		 * <p>
		 * Creates a required {@link SimpleProperty} (i.e., a property without a default
		 * value).
		 * </p>
		 * <p>
		 * This {@link SimpleProperty} always writes its value to and reads its value
		 * from JSON. If an attempt is made to load a {@link PropertyObject} from JSON
		 * data which does not have an entry for this {@link SimpleProperty}, a
		 * {@link PropertyRequiredException} may be thrown by
		 * {@link #fromJSON(JSONValue)} (though this is up to the implementation, as
		 * some implementations may interpret this as a certain value and return that
		 * instead).
		 * </p>
		 * 
		 * @param name the name of the property.
		 */
		public SimpleProperty(String name) {
			super(name);
			overwrite = def = false;
			defaultValue = null;
		}

		/**
		 * <p>
		 * Creates a {@link SimpleProperty} with a default value and overwriting
		 * enabled.
		 * </p>
		 * <p>
		 * This {@link SimpleProperty} writes its value to JSON data only when its value
		 * is not equal to the specified <code>defaultValue</code>. When reading from
		 * JSON data, this {@link SimpleProperty} always updates its value: If the JSON
		 * data contains an entry for this property, that entry is used, otherwise, this
		 * {@link SimpleProperty} is updated with the {@link #defaultValue} specified.
		 * </p>
		 * 
		 * @param name         The name of the property.
		 * @param defaultValue The default value for the property, used to avoid
		 *                     unnecessary writes and when reading.
		 */
		public SimpleProperty(String name, V defaultValue) {
			this(name, defaultValue, true);
		}

		/**
		 * <p>
		 * Creates a {@link SimpleProperty} with a default value and overwriting enabled
		 * as specified.
		 * </p>
		 * <p>
		 * This {@link SimpleProperty} writes its value to JOSN data only when its value
		 * is not equal to the specified <code>defaultValue</code>. When reading from
		 * JSON data, this {@link SimpleProperty} updates its value if the JSON data
		 * contains an entry for this property or if {@link #overwrite} is enabled. If
		 * the JSON data does not contain an entry for this property but
		 * {@link #overwrite} is enabled, this {@link SimpleProperty} is updated so that
		 * its value is the specified <code>defaultValue</code>.
		 * </p>
		 * 
		 * @param name         The name of the property.
		 * @param defaultValue The default value for the property.
		 * @param overwrite    Whether loading from JSON data which does not contain an
		 *                     entry for this property should cause this property's
		 *                     value to become the default value. This essentially
		 *                     determines whether to interpret missing data in loaded
		 *                     JSON data as the provided <code>defaultValue</code> or
		 *                     ignorable data that should not affect the
		 *                     {@link PropertyObject}.
		 */
		public SimpleProperty(String name, V defaultValue, boolean overwrite) {
			super(defaultValue, name);
			def = true;
			this.defaultValue = defaultValue;
			this.overwrite = overwrite;
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
		protected abstract V fromJSON(JSONValue json) throws PropertyException;

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
		protected abstract JSONValue toJSON(V value);

		@Override
		protected void load(JSONValue json) throws PropertyException {
			if (def) {
				if (json != NOT_WRITTEN)
					((Property<V>) this).value = fromJSON(json);
				else if (overwrite)
					((Property<V>) this).value = defaultValue;
			} else
				((Property<V>) this).value = fromJSON(json);
		}

		@Override
		protected JSONValue save() {
			return def && Objects.equals(((Property<V>) this).value, defaultValue) ? NOT_WRITTEN
					: toJSON(((Property<V>) this).value);
		}
	}

	public interface PropertyConverter<V> {
		/**
		 * <p>
		 * Converts from JSON to a value indicated by the type of this
		 * {@link PropertyConverter}.
		 * </p>
		 * <p>
		 * This method may be called with <code>null</code> or with
		 * {@link PropertyObject#NOT_WRITTEN}.
		 * </p>
		 * 
		 * @param json The JSON data to convert.
		 * @return The converted value.
		 * @throws PropertyException An exception if the JSON cannot be converted.
		 */
		V fromJSON(JSONValue json) throws PropertyException;

		/**
		 * Converts the provided property value to JSON data that can be saved. This
		 * method is free to return <code>null</code> or
		 * {@link PropertyObject#NOT_WRITTEN}.
		 * 
		 * @param value The value to convert to JSON.
		 * @return The JSON data, possibly <code>null</code> or
		 *         {@link PropertyObject#NOT_WRITTEN}.
		 */
		JSONValue toJSON(V value);
	}

	/**
	 * A {@link SimpleProperty} which stores a simple object and uses a
	 * {@link PropertyConverter} to convert to and from JSON.
	 * 
	 * @author Palanath
	 *
	 * @param <V> The type of object stored by the property.
	 */
	public class ObjectProperty<V> extends SimpleProperty<V> {
		private final PropertyConverter<V> converter;

		@Override
		protected V fromJSON(JSONValue json) throws PropertyException {
			return converter.fromJSON(json);
		}

		public ObjectProperty(String name, V defaultValue, boolean overwrite, PropertyConverter<V> converter) {
			super(name, defaultValue, overwrite);
			this.converter = converter;
		}

		public ObjectProperty(String name, V defaultValue, PropertyConverter<V> converter) {
			super(name, defaultValue);
			this.converter = converter;
		}

		public ObjectProperty(String name, PropertyConverter<V> converter) {
			super(name);
			this.converter = converter;
		}

		@Override
		protected JSONValue toJSON(V value) {
			return converter.toJSON(value);
		}

	}

	/**
	 * <p>
	 * A non-nullable boolean property converter. This is the property converter for
	 * the official primitive boolean type.
	 * </p>
	 * <p>
	 * This converter throws a {@link PropertyRequiredException} if an attempt is
	 * made to convert {@link PropertyObject#NOT_WRITTEN} to a {@link Boolean} using
	 * it.
	 * </p>
	 */
	public static final PropertyConverter<Boolean> BOOLEAN_PROPERTY_CONVERTER = new PropertyConverter<Boolean>() {
		@Override
		public Boolean fromJSON(JSONValue json) throws PropertyException {
			if (json == NOT_WRITTEN)
				throw new PropertyRequiredException(null);
			else if (!(json instanceof JSONConstant))
				throw new InvalidJSONException(null, json);
			else
				return (JSONConstant) json == JSONConstant.TRUE;
		}

		@Override
		public JSONValue toJSON(Boolean value) {
			return value ? JSONConstant.TRUE : JSONConstant.FALSE;
		}
	};

	public class BooleanProperty extends ObjectProperty<Boolean> {
		public BooleanProperty(String name, Boolean defaultValue, boolean overwrite) {
			super(name, defaultValue, overwrite, BOOLEAN_PROPERTY_CONVERTER);
		}

		public BooleanProperty(String name, Boolean defaultValue) {
			super(name, defaultValue, BOOLEAN_PROPERTY_CONVERTER);
		}

		public BooleanProperty(String name) {
			super(name, BOOLEAN_PROPERTY_CONVERTER);
		}
	}

	public static final PropertyConverter<String> STRING_PROPERTY_CONVERTER = new PropertyConverter<String>() {
		@Override
		public String fromJSON(JSONValue json) throws PropertyException {
			if (json == NOT_WRITTEN)
				throw new PropertyRequiredException(null);
			if (!(json instanceof JSONString))
				throw new InvalidJSONException(null, json);
			return ((JSONString) json).getValue();
		}

		@Override
		public JSONValue toJSON(String value) {
			return new JSONString(value);
		}
	};

	public class StringProperty extends ObjectProperty<String> {
		public StringProperty(String name) {
			super(name, STRING_PROPERTY_CONVERTER);
		}

		public StringProperty(String name, String defaultValue, boolean overwrite) {
			super(name, defaultValue, overwrite, STRING_PROPERTY_CONVERTER);
		}

		public StringProperty(String name, String defaultValue) {
			super(name, defaultValue, STRING_PROPERTY_CONVERTER);
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

	private final List<Property<?>> properties = new ArrayList<>();

	public void load(JSONObject json) throws PropertyException {
		for (Property<?> p : properties)
			p.load(json.containsKey(p.name) ? json : NOT_WRITTEN);
	}

	/**
	 * Called whenever this {@link PropertyObject} has been modified and needs to be
	 * re-saved.
	 */
	protected abstract void markDirty();

	public void save(JSONObject json) {
		for (Property<?> p : properties) {
			JSONValue r = p.save();
			if (r != NOT_WRITTEN)
				json.put(p.name, r);
		}
	}

	@Override
	public JSONObject toJSON() {
		JSONObject res = new JSONObject();
		save(res);
		return res;
	}

}
