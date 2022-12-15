package pala.libs.generic.commands;

public interface GenericCommandConsumer<D> {
	void act(D data);
}
