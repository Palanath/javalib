package pala.libs.generic.commands;

public interface QuickGenericCommand<D> extends GenericCommand<D> {
	@Override
	default void act(final D data) {
	}

	@Override
	default boolean match(final D data) {
		return run(data);
	}

	boolean run(D data);
}
