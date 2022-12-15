package pala.libs.generic.commands;

public interface GenericCommand<D> extends GenericCommandConsumer<D> {
	boolean match(D data);
}
