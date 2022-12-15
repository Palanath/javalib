package pala.libs.generic.javafx.properties;

import javafx.beans.property.SimpleObjectProperty;

public class NonNullSimpleObjectProperty<T> extends SimpleObjectProperty<T> {

	public NonNullSimpleObjectProperty(final Object bean, final String name) {
		super(bean, name);
	}

	public NonNullSimpleObjectProperty(final Object bean, final String name, final T initialValue) {
		super(bean, name, initialValue);
	}

	public NonNullSimpleObjectProperty(final T initialValue) {
		super(initialValue);
	}

	@Override
	public void set(final T newValue) throws IllegalArgumentException {
		if (newValue == null)
			throw new IllegalArgumentException("Property value cannot be null.");
		super.set(newValue);
	}

}
