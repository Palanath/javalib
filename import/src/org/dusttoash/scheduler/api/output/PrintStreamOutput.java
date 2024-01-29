package org.dusttoash.scheduler.api.output;

import java.io.PrintStream;

public class PrintStreamOutput implements Output {

	private final PrintStream out;

	public PrintStreamOutput(PrintStream out) {
		this.out = out;
	}

	public void flush() {
		out.flush();
	}

	public void print(boolean b) {
		out.print(b);
	}

	public void print(char c) {
		out.print(c);
	}

	public void print(int i) {
		out.print(i);
	}

	public void print(long l) {
		out.print(l);
	}

	public void print(float f) {
		out.print(f);
	}

	public void print(double d) {
		out.print(d);
	}

	public void print(char... s) {
		out.print(s);
	}

	public void print(String s) {
		out.print(s);
	}

	public void print(Object obj) {
		out.print(obj);
	}

	public void println() {
		out.println();
	}

	public void println(boolean x) {
		out.println(x);
	}

	public void println(char x) {
		out.println(x);
	}

	public void println(int x) {
		out.println(x);
	}

	public void println(long x) {
		out.println(x);
	}

	public void println(float x) {
		out.println(x);
	}

	public void println(double x) {
		out.println(x);
	}

	public void println(char[] x) {
		out.println(x);
	}

	public void println(String x) {
		out.println(x);
	}

	public void println(Object x) {
		out.println(x);
	}

	@Override
	public void ensureSize(int size) {
	}

}
