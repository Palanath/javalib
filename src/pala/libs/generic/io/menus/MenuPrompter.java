package pala.libs.generic.io.menus;

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
	 * Prompts the user with the provided {@link String} prompt and provided
	 * {@link String} options and returns the <b>index</b> of the option selected.
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
			out.accept((i + 1) + ". " + options[i]);
		while (true)
			try {
				return Integer.parseInt(in.get().trim()) - 1;
			} catch (NumberFormatException e) {
				out.accept("That's not a valid option. Please enter a number in the range: 1-" + options.length + '.');
			}
	}

	public String getNumberPrefix(int number) {
		return number + ". ";
	}
}
