package org.dusttoash.scheduler.api.output;

public interface Output {
	public void print(boolean b);

	public void print(char c);

	public void print(int i);

	public void print(long l);

	public void print(float f);

	public void print(double d);

	public void print(char... s);

	public void print(String s);

	public void print(Object obj);

	public void println();

	public void println(boolean b);

	public void println(char c);

	public void println(int i);

	public void println(long l);

	public void println(float f);

	public void println(double d);

	public void println(char... s);

	public void println(String s);

	public void println(Object obj);

	public void flush();
	
	public void ensureSize(int size);
}
