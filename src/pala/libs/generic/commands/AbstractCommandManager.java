package pala.libs.generic.commands;

public interface AbstractCommandManager<D> {
	void addCommand(GenericCommand<? super D> cmd);

	void addConsumer(GenericCommandConsumer<? super D> cnsm);

	default void addConsumer(final OptionalGenericCommandConsumer<? super D> optnCnsm) {
		addOptionalConsumer(optnCnsm);
	}

	void addOptionalConsumer(OptionalGenericCommandConsumer<? super D> optnCnsm);

	void removeCommand(GenericCommand<? super D> cmd);

	void removeConsumer(GenericCommandConsumer<? super D> cnsm);

	default void removeConsumer(final OptionalGenericCommandConsumer<? super D> optnCnsm) {
		removeOptionalConsumer(optnCnsm);
	}

	void removeOptionalConsumer(OptionalGenericCommandConsumer<? super D> optnCnsm);

	boolean run(D data);
}
