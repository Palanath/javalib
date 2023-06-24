package pala.libs.generic.ml.ai.neuralnets.api;

public class ContainerImpl implements Container {

	private Object value;
	private boolean modifyMode = true;

	public boolean isModifyMode() {
		return modifyMode;
	}

	public ContainerImpl setModifyMode(boolean modifiable) {
		this.modifyMode = modifiable;
		return this;
	}

	public ContainerImpl disableModification() {
		return setModifyMode(false);
	}

	@Override
	public void set(Object v) {
		if (!isModifyMode())
			throw new IllegalStateException();
		value = v;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> O get() {
		if (isModifyMode())
			throw new IllegalStateException();
		return (O) value;
	}

}
