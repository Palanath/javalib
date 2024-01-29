package org.dusttoash.scheduler.api.console;

import java.util.Arrays;

import org.dusttoash.scheduler.api.output.Output;

import pala.libs.generic.JavaTools;
import pala.libs.generic.util.functions.IntToCharFunction;

public class ConsoleToolkit {

	protected Output out;

	public ConsoleToolkit(Output output) {
		this.out = output;
	}

	public ConsoleToolkit() {
	}

	public Output getOutput() {
		return out;
	}

	public void setOutput(Output output, ANSIConsoleToolkit ansiConsoleToolkit) {
		this.out = output;
	}

	public ConsoleToolkit printBoxes(int count, int spacing, int height) {
		printBoxes(count, spacing, height, '*');
		return this;
	}

	public ConsoleToolkit printBoxes(int count, int spacing, int height, char character) {
		printBoxes(count, spacing, height, character, a -> ' ');
		return this;
	}

	public ConsoleToolkit printBoxes(int count, int spacing, int height, char character, IntToCharFunction callback) {
		int boxwidth = spacing * count + count + 1;
		out.ensureSize(boxwidth * height);
		out.println(JavaTools.fill(character, boxwidth));
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < count; i++) {
				out.print(character);
				for (int k = 0; k < spacing; k++)
					out.print(callback.run(k + i * (spacing + 1)));
			}
			out.println(character);
		}
		out.println(JavaTools.fill(character, boxwidth));
		return this;
	}

	public ConsoleToolkit printBoxes(int count, int spacing, int height, char character, char spacingChar) {
		int boxwidth = spacing * count + count + 1;
		out.ensureSize(boxwidth * height);
		out.println(JavaTools.fill(character, boxwidth));

		char[] arr = new char[spacing];
		Arrays.fill(arr, spacingChar);
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < count; i++) {
				out.print(character);
				out.print(arr);
			}
			out.println(character);
		}
		out.println(JavaTools.fill(character, boxwidth));
		return this;
	}

}