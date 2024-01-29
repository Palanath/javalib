package org.dusttoash.scheduler.api.output;

public class StringBufferOutput implements Output {
	protected final StringBuffer sb;

	public StringBuffer getStringBuffer() {
		return sb;
	}

	public StringBufferOutput(StringBuffer sb) {
		this.sb = sb;
	}

	public StringBufferOutput() {
		this(new StringBuffer());
	}

	@Override
	public void print(boolean b) {
		sb.append(b);
	}

	@Override
	public void print(char c) {
		sb.append(c);
	}

	@Override
	public void print(int i) {
		sb.append(i);
	}

	@Override
	public void print(long l) {
		sb.append(l);
	}

	@Override
	public void print(float f) {
		sb.append(f);
	}

	@Override
	public void print(double d) {
		sb.append(d);
	}

	@Override
	public void print(char... s) {
		sb.append(s);
	}

	@Override
	public void print(String s) {
		sb.append(s);
	}

	@Override
	public void print(Object obj) {
		sb.append(obj);
	}

	@Override
	public void println() {
		print('\n');
	}

	@Override
	public void println(boolean b) {
		print(b);
		println();
	}

	@Override
	public void println(char c) {
		print(c);
		println();
	}

	@Override
	public void println(int i) {
		print(i);
		println();
	}

	@Override
	public void println(long l) {
		print(l);
		println();
	}

	@Override
	public void println(float f) {
		print(f);
		println();
	}

	@Override
	public void println(double d) {
		print(d);
		println();
	}

	@Override
	public void println(char... s) {
		print(s);
		println();
	}

	@Override
	public void println(String s) {
		print(s);
		println();
	}

	@Override
	public void println(Object obj) {
		print(obj);
		println();
	}

	@Override
	public void flush() {
	}

	@Override
	public void ensureSize(int size) {
		sb.ensureCapacity(size);
	}

}
