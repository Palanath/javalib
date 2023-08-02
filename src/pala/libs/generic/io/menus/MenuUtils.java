package pala.libs.generic.io.menus;

public class MenuUtils {
	private static MenuPrompter prompter;

	public static MenuPrompter getSystemIn() {
		return prompter == null ? prompter = new MenuPrompter() : prompter;
	}

	/**
	 * Prompts the user with the provided {@link String} prompt and provided
	 * {@link String} options and returns the <b>index</b> of the option selected.
	 * This method does not return until the user enters a valid prompt choice. This
	 * method takes user input one line at a time and parses it as an
	 * <code>int</code>.
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
		return prompter.prompt(prompt, options);
	}

}
