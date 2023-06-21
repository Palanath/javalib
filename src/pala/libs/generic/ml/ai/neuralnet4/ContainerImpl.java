package pala.libs.generic.ml.ai.neuralnet4;

class ContainerImpl implements Container {

	private Object value;
	private boolean modifyMode = true;

	public boolean isModifyMode() {
		return modifyMode;
	}

	public void setModifyMode(boolean modifiable) {
		this.modifyMode = modifiable;
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
