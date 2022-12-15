package pala.libs.generic.commands;

public interface OptionalGenericCommandConsumer<D> {
	boolean act(D data);
}
