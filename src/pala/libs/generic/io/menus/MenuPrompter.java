package pala.libs.generic.io.menus;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MenuPrompter {
	private final Supplier<? extends String> in;
	private final Consumer<? super String> out;

	public MenuPrompter(Supplier<? extends String> in, Consumer<? super String> out) {
		this.in = in;
		this.out = out;
	}

	/**
	 * <p>
	 * Creates a new {@link MenuPrompter} with the provided {@link Scanner} to be
	 * used for input and the provided {@link PrintStream} for output. The
	 * {@link Scanner}'s {@link Scanner#nextLine() nextLine()} method is used for
	 * grabbing input and the {@link PrintStream}'s
	 * {@link PrintStream#println(String) println(String)} method is used for
	 * output.
	 * </p>
	 * <p>
	 * {@link System#in} and {@link System#out} can be used for constructing the
	 * {@link MenuPrompter} (with a caller-made {@link Scanner} constructed off of
	 * {@link System#in}). Such is equivalent to {@link #MenuPrompter()}, though in
	 * {@link #MenuPrompter()} the {@link Scanner} created is not accessible to
	 * calling code.
	 * </p>
	 * 
	 * @param in  The {@link Scanner} to use for input.
	 * @param out The {@link PrintStream} to use for output.
	 */
	public MenuPrompter(Scanner in, PrintStream out) {
		this(in::nextLine, out::println);
	}

	/**
	 * <p>
	 * Creates a new {@link MenuPrompter} with a newly created {@link Scanner}
	 * hooked to {@link System#in} for input, and {@link System#out} for output.
	 * </p>
	 * <p>
	 * <b>Note:</b> There is no way to acquire the created {@link Scanner} once this
	 * constructor is called. Subsequent use of {@link System#in} should entirely be
	 * done through this {@link MenuPrompter} if this constructor is used.
	 * </p>
	 */
	public MenuPrompter() {
		this(new Scanner(System.in), System.out);
	}

	/**
	 * <p>
	 * Prompts the user with the provided {@link String} prompt and provided
	 * {@link String} options, and returns the <code>int</code> value that the user
	 * responds with, that is, the index of the option selected plus one. This is in
	 * line with the default numbering scheme for each option, so to select option
	 * <code>2.</code>, the user will enter <code>2</code>, and the return value
	 * will therefore be <code>2</code>.
	 * </p>
	 * <p>
	 * The options shown have numeric prefixes generated with
	 * {@link #getNumberPrefix(int)}. This method does not return until the user
	 * enters a valid prompt choice. This method takes user input one line at a time
	 * and parses it as an <code>int</code>.
	 * </p>
	 * <p>
	 * It is recommended to provide a "back" or "exit" option, since the method
	 * provides no way for users to escape the prompt without selecting an option or
	 * an uncaught exception being raised.
	 * </p>
	 * 
	 * @param prompt  The prompt to provide to the user. This appears at the top of
	 *                the options list. It is often something like:
	 * 
	 *                <pre>
	 * Please select an option to continue...
	 *                </pre>
	 * 
	 * @param options An array containing the options to provide the user. These are
	 *                each displayed to the user on a separate line with their
	 *                number choice in front. The index of the option that the user
	 *                selected is returned by this method.
	 * @return The index of the selected option.
	 */
	public int prompt(String prompt, String... options) {
		if (options.length == 0)
			throw new IllegalArgumentException("No options provided.");
		out.accept(prompt);
		for (int i = 0; i < options.length; i++)
			out.accept(getNumberPrefix(i) + options[i]);
		while (true) {
			try {
				int selection = Integer.parseInt(in.get().trim());
				if (selection > 0 && selection < options.length)
					return selection;
			} catch (NumberFormatException e) {
			}
			out.accept("That's not a valid option. Please enter a number in the range: 1-" + options.length + '.');
		}
	}

	public static class Option {
		private final String value;
		/**
		 * Determines whether this {@link Option} is active. Inactive {@link Option}s do
		 * not show up when provided to {@link MenuPrompter#prompt(String, Option...)}.
		 * It is as if they were not specified.
		 */
		private boolean active;

		public boolean isActive() {
			return active;
		}

		/**
		 * Sets whether this {@link Option} is active or not and returns this
		 * {@link Option}.
		 * 
		 * @param active Whether this {@link Option} is active.
		 * @return This {@link Option} object.
		 */
		public Option setActive(boolean active) {
			this.active = active;
			return this;
		}

		public String getValue() {
			return value;
		}

		public Option(String value) {
			this.value = value;
		}

	}

	public int prompt(String prompt, Option... options) {
		List<String> res = new ArrayList<>();
		for (int i = 0; i < options.length; i++)
			if (options[i].active)
				res.add(options[i].value);
		if (res.isEmpty())
			throw new IllegalArgumentException("No options provided.");
		out.accept(prompt);
		for (int i = 0; i < res.size(); i++)
			out.accept(getNumberPrefix(i) + res.get(i));

		while (true) {
			try {
				int selection = Integer.parseInt(in.get().trim());
				if (selection > 0 && selection < res.size())
					return selection;
			} catch (NumberFormatException e) {
			}
			out.accept("That's not a valid option. Please enter a number in the range: 1-" + options.length + '.');
		}
	}

	protected String getNumberPrefix(int number) {
		return 1 + number + ". ";
	}

}
