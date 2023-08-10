package pala.libs.generic.data.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import pala.libs.generic.json.JSONConstant;
import pala.libs.generic.json.JSONNumber;
import pala.libs.generic.json.JSONObject;
import pala.libs.generic.json.JSONParser;
import pala.libs.generic.json.JSONSavable;
import pala.libs.generic.json.JSONString;
import pala.libs.generic.json.JSONValue;
import pala.libs.generic.streams.CharacterStream;

public abstract class PropertyObject implements JSONSavable {

	protected BooleanProperty booleanProperty(String name) {
		return new BooleanProperty(name);
	}

	protected BooleanProperty booleanProperty(String name, boolean defaultValue) {
		return new BooleanProperty(name, defaultValue);
	}

	protected BooleanProperty booleanProperty(String name, boolean defaultValue, boolean overwrite) {
		return new BooleanProperty(name, defaultValue, overwrite);
	}

	protected StringProperty stringProperty(String name) {
		return new StringProperty(name);
	}

	protected StringProperty stringProperty(String name, String defaultValue) {
		return new StringProperty(name, defaultValue);
	}

	protected StringProperty stringProperty(String name, String defaultValue, boolean overwrite) {
		return new StringProperty(name, defaultValue, overwrite);
	}

	protected InstantProperty instantProperty(String name) {
		return new InstantProperty(name);
	}

	protected InstantProperty instantProperty(String name, Instant defaultValue) {
		return new InstantProperty(name, defaultValue);
	}

	protected InstantProperty instantProperty(String name, Instant defaultValue, boolean overwrite) {
		return new InstantProperty(name, defaultValue, overwrite);
	}

	public class BooleanProperty extends ObjectProperty<Boolean> {
		public BooleanProperty(final String name) {
			super(name, PropertyConverter.BOOLEAN_PROPERTY_CONVERTER);
		}

		public BooleanProperty(final String name, final boolean defaultValue) {
			super(name, defaultValue, PropertyConverter.BOOLEAN_PROPERTY_CONVERTER);
		}

		public BooleanProperty(final String name, final boolean defaultValue, final boolean overwrite) {
			super(name, defaultValue, overwrite, PropertyConverter.BOOLEAN_PROPERTY_CONVERTER);
		}
	}

	public class Edit {
		private final Map<Property<?>, Object> changes = new HashMap<>(4);

		/**
		 * Clears all the changes in this {@link Edit}.
		 *
		 * @return This {@link Edit} object.
		 */
		public Edit clear() {
			changes.clear();
			return this;
		}

		public Edit commit() {
			commitWithoutDirtying();
			markDirty();
			return this;
		}

		@SuppressWarnings("unchecked")
		public Edit commitWithoutDirtying() {
			for (final Entry<Property<?>, Object> e : changes.entrySet())
				((Property<Object>) e.getKey()).value = e.getValue();
			return this;
		}

		public <V> Edit set(final Property<? super V> property, final V value) {
			changes.put(property, value);
			return this;
		}

		public Edit undo(final Property<?> property) {
			changes.remove(property);
			return this;
		}

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

		/**
		 * <p>
		 * Creates an {@link ObjectProperty} with the provided name, no default value,
		 * and the provided {@link PropertyConverter}. The {@link PropertyConverter}
		 * will need to handle normal JSON values, <code>null</code>, and
		 * {@link PropertyObject#NOT_WRITTEN} when loading a value from JSON for this
		 * property.
		 * </p>
		 * <p>
		 * This constructor is useful for required properties, since the
		 * {@link PropertyConverter} can throw a {@link PropertyRequiredException} if it
		 * is given {@link PropertyObject#NOT_WRITTEN} when loading. All other
		 * constructors in this class set up the {@link ObjectProperty} with a default
		 * value, which includes automatic handling of the
		 * {@link PropertyObject#NOT_WRITTEN} constant.
		 * </p>
		 *
		 * @param name      The name of the property (the key to write for this
		 *                  property's entry in th emitted JSON).
		 * @param converter A {@link PropertyConverter} used to convert the value of
		 *                  this property to and from JSON.
		 */
		public ObjectProperty(final String name, final PropertyConverter<V> converter) {
			super(name);
			this.converter = converter;
		}

		/**
		 * <p>
		 * Creates an {@link ObjectProperty} with the specified key-<code>name</code>
		 * and default value. To create an {@link ObjectProperty} without a default
		 * value (i.e., with a required value), use the
		 * {@link #PropertyObject(String, PropertyConverter)} constructor.
		 * </p>
		 * <h2>Default Value</h2>
		 * <p>
		 * If this {@link ObjectProperty}'s value is equal to the provided
		 * <code>defaultValue</code> when the {@link PropertyObject} is being saved,
		 * then no entry for this {@link PropertyObject} is written to the file, and the
		 * {@link PropertyConverter} is not invoked at all. This can be useful for
		 * saving file space or network bandwidth when writing large
		 * {@link PropertyObject}s that have many properties which rarely deviate in
		 * value from some default.
		 * </p>
		 * <p>
		 * The <code>defaultValue</code> also has an effect when loading this
		 * {@link Property} if <code>overwrite</code> is <code>true</code>. See below
		 * for details.
		 * </p>
		 * <h2>Overwriting</h2>
		 * <p>
		 * The <code>overwrite</code> setting only takes effect when loading a
		 * {@link SimpleProperty}. If <code>overwrite</code> is <code>true</code> and
		 * there is no entry contained for this {@link ObjectProperty} when loading from
		 * JSON data, the value of this {@link ObjectProperty} is set to the
		 * <code>defaultValue</code> provided.
		 * </p>
		 * <h2>Property Converter</h2>
		 * <p>
		 * The provided {@link PropertyConverter} is used to convert the value held by
		 * this property to and from JSON and time of writing and reading. This
		 * constructor creates this {@link ObjectProperty} with a default value, so the
		 * provided {@link PropertyConverter} will not be invoked with the
		 * {@link PropertyObject#NOT_WRITTEN} constant. {@link PropertyConverter}s only
		 * need to handle that constant if the {@link ObjectProperty} does not have a
		 * default value.
		 * </p>
		 * <p>
		 * The {@link PropertyConverter} can throw a {@link PropertyException} if
		 * there's issue with the value being provided to the property. For example, if
		 * this property only stores {@link JSONString}s, but the provided JSON data is
		 * a {@link JSONConstant} or a {@link JSONNumber}.
		 * </p>
		 * <p>
		 * Provided {@link PropertyConverter}s may safely throw the
		 * {@link PropertyRequiredException} if they receive the
		 * {@link PropertyObject#NOT_WRITTEN} value, so as to be generic enough to be
		 * used for {@link ObjectProperty ObjectProperties} which do not have a default
		 * value. Such {@link PropertyConverter}s will not be called by this
		 * {@link ObjectProperty} with {@link PropertyObject#NOT_WRITTEN}.
		 * </p>
		 * <p>
		 * Note that the {@link PropertyConverter} may still <i>return</i> the
		 * {@link PropertyObject#NOT_WRITTEN} constant for a given value input, to
		 * indicate that a value should not be written, if desired. The "not-written"
		 * value will still represent the default value of the property, though, so when
		 * reading, the {@link PropertyConverter} will have no way of losslessly
		 * converting back from JSON.
		 * </p>
		 *
		 * @param name         The name (or key) of this property.
		 * @param defaultValue The default value of this property.
		 * @param overwrite    Whether overwriting should be enabled when loading from
		 *                     JSON.
		 * @param converter    A {@link PropertyConverter} used to convert the JSON data
		 *                     into a value and back. The {@link PropertyConverter} will
		 *                     not be called to convert a value equal to the provided
		 *                     <code>defaultValue</code> to JSON. Additionally, the
		 *                     {@link PropertyConverter} will not be called to convert
		 *                     the {@link PropertyObject#NOT_WRITTEN} value back from
		 *                     JSON.
		 */
		public ObjectProperty(final String name, final V defaultValue, final boolean overwrite,
				final PropertyConverter<V> converter) {
			super(name, defaultValue, overwrite);
			this.converter = converter;
		}

		public ObjectProperty(final String name, final V defaultValue, final PropertyConverter<V> converter) {
			super(name, defaultValue);
			this.converter = converter;
		}

		@Override
		protected V fromJSON(final JSONValue json) throws PropertyException {
			return converter.fromJSON(json);
		}

		@Override
		protected JSONValue toJSON(final V value) {
			return converter.toJSON(value);
		}

	}

	public abstract class Property<V> {
		{
			properties.add(this);
		}
		private V value;

		private final String name;

		/**
		 * Constructs a {@link Property} with the provided name. The property's initial
		 * value is <code>null</code>.
		 *
		 * @param name The name of the property. Must be unique against all other
		 *             properties in thi {@link PropertyObject}. Used to uniquely
		 *             identify the data in the file.
		 */
		public Property(final String name) {
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
		public Property(final V value, final String name) {
			this.value = value;
			this.name = name;
		}

		public V get() {
			return value;
		}

		public String getName() {
			return name;
		}

		protected final V getPropertyValue() {
			return value;
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

		protected final void setPropertyValue(final V value) {
			this.value = value;
		}

		/**
		 * Updates the value of this {@link Property}, causing the
		 * {@link PropertyObject} it belongs to be {@link PropertyObject#markDirty()
		 * marked dirty}.
		 *
		 * @param value The value to update the {@link Property} to.
		 */
		public void update(final V value) {
			this.value = value;
			markDirty();
		}

	}

	public abstract class SimpleProperty<V> extends Property<V> {
		/**
		 * <p>
		 * Stores the default value for this property. If this is assigned a value
		 * during construction, {@link #def} will be <code>true</code>. This is the case
		 * even if <code>null</code> is assigned to this field.
		 * </p>
		 * <h2>Usage</h2>
		 * <p>
		 * This field's primary use follows from how it affects saving behavior. If
		 * {@link #overwrite} is <code>true</code>, this field will also affect loading
		 * behavior.
		 * </p>
		 * <ul>
		 * <li>During a saving pass, if {@link #def} is <code>true</code> and this
		 * {@link SimpleProperty}'s value is equal to this {@link #defaultValue}, as
		 * determined by {@link Objects#equals(Object, Object)}, the
		 * {@link SimpleProperty} is not written out to the JSON output.</li>
		 * <li>During a loading pass, if the JSON data being loaded from does not
		 * contain an entry for this {@link Property} and {@link #overwrite} (and
		 * {@link #def}) is/are <code>true</code>, the value of this
		 * {@link SimpleProperty} becomes this default value.</li>
		 * </ul>
		 * <p>
		 * Since a default value is not written out to JSON when saving a
		 * {@link PropertyObject}, a {@link Property} that usually has the same value
		 * need not show up in JSON output, unless it is different from its default
		 * value.
		 * </p>
		 *
		 */
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
		public SimpleProperty(final String name) {
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
		public SimpleProperty(final String name, final V defaultValue) {
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
		public SimpleProperty(final String name, final V defaultValue, final boolean overwrite) {
			super(defaultValue, name);
			def = true;
			this.defaultValue = defaultValue;
			this.overwrite = overwrite;
		}

		/**
		 * <p>
		 * Converts the provided {@link JSONValue} into a value that will be stored in
		 * this {@link Property} during a loading pass. If {@link #def} is
		 * <code>false</code>, this method should also handle the
		 * {@link PropertyObject#NOT_WRITTEN} special value, used to indicate that no
		 * value for this {@link Property} was contained within the {@link JSONObject}
		 * that the surrounding {@link PropertyObject} was loaded from.
		 * </p>
		 * <h2>Example</h2>
		 * <p>
		 * For properties that are <b>required</b>, a typical implementation will check
		 * if the provided {@link JSONValue} is {@link PropertyObject#NOT_WRITTEN} and
		 * throw a {@link PropertyRequiredException}. Otherwise the method throws an
		 * {@link InvalidJSONException} or, if the {@link JSONValue} provided is instead
		 * of the appropriate type, returns a value accordingly. For example, an
		 * implementation may be as follows:
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
		 * when this method is called. There is no class or constant in the JSON API to
		 * represent the JSON <code>null</code> constant (as there is for
		 * {@link JSONConstant#TRUE} or {@link JSONConstant#FALSE}).
		 * </p>
		 * <p>
		 * <b>Note</b> that this {@link SimpleProperty} class automatically handles the
		 * {@link PropertyObject#NOT_WRITTEN} value whenever there is a default value
		 * set for this {@link SimpleProperty} (i.e. when {@link #def} is
		 * <code>true</code>), so if {@link #def} is <code>true</code>, this method will
		 * not get called with {@link PropertyObject#NOT_WRITTEN}.
		 * </p>
		 * <h2>Input</h2>
		 * <p>
		 * This method can receive any one of the following values:
		 * </p>
		 * <ol>
		 * <li>An instance of {@link JSONValue}, of any of the normal JSON
		 * sub-types,</li>
		 * <li><code>null</code>, or</li>
		 * <li>{@link PropertyObject#NOT_WRITTEN}, only if there is no default value set
		 * for this {@link PropertyObject}.</li>
		 * </ol>
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
		 * Loads this {@link PropertyObject} from the file, handling
		 * <code>default</code> values automatically.
		 * </p>
		 * <p>
		 * If the provided {@link JSONValue} is not the
		 * {@link PropertyObject#NOT_WRITTEN} constant, this method will invoke
		 * {@link #fromJSON(JSONValue)} and set this {@link SimpleProperty}'s value to
		 * be the result. Otherwise, if {@link #def} and {@link #overwrite} are both
		 * set, this method will set this {@link SimpleProperty}'s value to be
		 * {@link #defaultValue}.
		 * </p>
		 * <p>
		 * Note that if the provided {@link JSONValue} is
		 * {@link PropertyObject#NOT_WRITTEN}, {@link #def} is <code>true</code>, and
		 * {@link #overwrite} is <code>false</code>, this method does nothing, since it
		 * will not overwrite the current value with the default value if
		 * {@link #overwrite} is <code>false</code>.
		 * </p>
		 * <p>
		 * Also note that {@link #fromJSON(JSONValue)} can be called with any value that
		 * this method can be called with, unless {@link #def} is <code>true</code>, in
		 * which case {@link #fromJSON(JSONValue)} will not be called with
		 * {@link PropertyObject#NOT_WRITTEN}; that value is entirely handled by this
		 * method if {@link #def} is <code>true</code>.
		 * </p>
		 */
		@Override
		protected void load(final JSONValue json) throws PropertyException {
			if (json != NOT_WRITTEN)
				((Property<V>) this).value = fromJSON(json);
			else if (def && overwrite)
				((Property<V>) this).value = defaultValue;
		}

		@Override
		protected JSONValue save() {
			// If there is a default value and the current value is equal to it, don't write
			// anything. Otherwise, write the current value.
			return def && Objects.equals(((Property<V>) this).value, defaultValue) ? NOT_WRITTEN
					: toJSON(((Property<V>) this).value);
		}

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
		 * <h2>Return Value</h2>
		 * <p>
		 * This method may return any one of the following values:
		 * </p>
		 * <ol>
		 * <li>An <b>instance of {@link JSONValue}</b>, in which case the value is saved
		 * as normal.</li>
		 * <li><b><code>null</code></b>, in which case the JSON null constant is
		 * saved.</li>
		 * <li><b>{@link PropertyObject#NOT_WRITTEN}</b>, in which case nothing is saved
		 * in place of this {@link Property}.</li>
		 * </ol>
		 * <p>
		 * Note that this method will not be called with the {@link #defaultValue} if
		 * one is set.
		 * </p>
		 *
		 * @param value The actual value of this property.
		 * @return The JSON value of this property. Can be <code>null</code> or
		 *         {@link PropertyObject#NOT_WRITTEN}.
		 */
		protected abstract JSONValue toJSON(V value);
	}

	/**
	 * An {@link ObjectProperty} using
	 * {@link PropertyConverter#STRING_PROPERTY_CONVERTER} for the converter.
	 * {@link StringProperty StringProperties} is <i>required</i> (unless a default
	 * value is specified through an appropriate constructor) and
	 * non-<code>null</code>able.
	 * 
	 * @author Palanath
	 *
	 */
	public class StringProperty extends ObjectProperty<String> {
		public StringProperty(final String name) {
			super(name, PropertyConverter.STRING_PROPERTY_CONVERTER);
		}

		public StringProperty(final String name, final String defaultValue) {
			super(name, defaultValue, PropertyConverter.STRING_PROPERTY_CONVERTER);
		}

		public StringProperty(final String name, final String defaultValue, final boolean overwrite) {
			super(name, defaultValue, overwrite, PropertyConverter.STRING_PROPERTY_CONVERTER);
		}
	}

	/**
	 * An {@link ObjectProperty} using
	 * {@link PropertyConverter#INSTANT_PROPERTY_CONVERTER} for the converter.
	 * {@link InstantProperty InstantProperties} are <i>required</i> (unless a
	 * default value is specified through an appropriate constructor) and
	 * non-<code>null</code>able.
	 * 
	 * @author Palanath
	 *
	 */
	public class InstantProperty extends ObjectProperty<Instant> {

		public InstantProperty(String name, Instant defaultValue, boolean overwrite) {
			super(name, defaultValue, overwrite, PropertyConverter.INSTANT_PROPERTY_CONVERTER);
		}

		public InstantProperty(String name, Instant defaultValue) {
			super(name, defaultValue, PropertyConverter.INSTANT_PROPERTY_CONVERTER);
		}

		public InstantProperty(String name) {
			super(name, PropertyConverter.INSTANT_PROPERTY_CONVERTER);
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
		public String toString(final String indentation) {
			return null;
		}
	};

	private final List<Property<?>> properties = new ArrayList<>();

	public void load(final File file) throws PropertyException, FileNotFoundException, IOException {
		try (var reader = new FileReader(file)) {
			load((JSONObject) new JSONParser().parse(CharacterStream.from(reader)));
		}
	}

	public void load(final JSONObject json) throws PropertyException {
		for (final Property<?> p : properties)
			p.load(json.containsKey(p.name) ? json : NOT_WRITTEN);
	}

	/**
	 * Called whenever this {@link PropertyObject} has been modified and needs to be
	 * re-saved.
	 */
	protected abstract void markDirty();

	public void save(final JSONObject json) {
		for (final Property<?> p : properties) {
			final var r = p.save();
			if (r != NOT_WRITTEN)
				json.put(p.name, r);
		}
	}

	@Override
	public JSONObject toJSON() {
		final var res = new JSONObject();
		save(res);
		return res;
	}

}
