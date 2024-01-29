package org.dusttoash.scheduler.api.output;

public class BufferedOutput extends StringBufferOutput {
	private final Output out;

	public BufferedOutput(StringBuffer sb, Output out) {
		super(sb);
		this.out = out;
	}

	public BufferedOutput(Output out) {
		this.out = out;
	}

	@Override
	public void flush() {
		out.print(sb);
		sb.setLength(0);
	}
}
