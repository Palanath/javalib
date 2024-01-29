package org.dusttoash.scheduler.api.console;

import org.dusttoash.scheduler.api.output.Output;

public class ANSIConsoleToolkit extends ConsoleToolkit {

	private static final char ESCAPE = 27;

	public String esc(String code) {
		return ESCAPE + "[" + code;
	}

	public ANSIConsoleToolkit() {
	}

	public ANSIConsoleToolkit(Output output) {
		super(output);
	}

	public ANSIConsoleToolkit clearConsole() {
		out.print("\033[2J\033[H");
		return this;
	}

	public ANSIConsoleToolkit jumpTo(int x, int y) {
		out.print("\033[" + y + ';' + x + 'f');
		return this;
	}

	public ANSIConsoleToolkit setForeground(Colors color) {
		out.print(color.foreground());
		return this;
	}

	public ANSIConsoleToolkit setBackground(Colors color) {
		out.print(color.background());
		return this;
	}

	public ANSIConsoleToolkit italicize() {
		out.print("\033[3m");
		return this;
	}

	public ANSIConsoleToolkit unitalicize() {
		out.print("\033[23m");
		return this;
	}

	public enum Colors {
		BLACK(30), RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE, DEFAULT(39), RESET(0, 0);

		private final int foreground, background;

		private static int getBlackPos() {
			return BLACK.foreground;
		}

		private Colors() {
			this.background = (this.foreground = getBlackPos() + ordinal()) + 10;
		}

		private Colors(int foreground) {
			this(foreground, foreground + 10);
		}

		private Colors(int foreground, int background) {
			this.foreground = foreground;
			this.background = background;
		}

		public int getForeground() {
			return foreground;
		}

		public int getBackground() {
			return background;
		}

		public String foreground() {
			return "\033[" + getForeground() + 'm';
		}

		public String background() {
			return "\033[" + getBackground() + 'm';
		}

	}

}
