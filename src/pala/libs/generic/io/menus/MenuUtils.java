package pala.libs.generic.io.menus;

import java.util.Scanner;

public class MenuUtils {
	private static MenuPrompter prompter;
	private static Scanner defaultScanner;

	public static MenuPrompter getDefaultPrompter() {
		if (prompter == null)
			createDefaultObjects();
		return prompter;
	}

	public static Scanner getDefaultScanner() {
		if (defaultScanner == null)
			createDefaultObjects();
		return defaultScanner;
	}

	private static void createDefaultObjects() {
		prompter = new MenuPrompter(defaultScanner = new Scanner(System.in), System.out);
	}

	/**
	 * <p>
	 * Prompts the user with the provided {@link String} prompt and provided
	 * {@link String} options and returns the <b>index</b> of the option selected.
	 * This method does not return until the user enters a valid prompt choice. This
	 * method takes user input one line at a time and parses it as an
	 * <code>int</code>.
	 * </p>
	 * <p>
	 * This method uses the {@link #getDefaultPrompter() default prompter}, creating
	 * it if it does not already exist.
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
	public static int prompt(String prompt, String... options) {
		return getDefaultPrompter().prompt(prompt, options);
	}

	/**
	 * Prompts the user for input by printing the provided {@link String} (without a
	 * trailing line-break) and then returning the next line parsed.
	 * 
	 * @param prompt The {@link String} to prompt the user with.
	 * @return The line that the user entered.
	 */
	public static String input(String prompt) {
		System.out.print(prompt);
		return defaultScanner.nextLine();
	}

}
