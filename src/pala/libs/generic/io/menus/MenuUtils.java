package pala.libs.generic.io.menus;

import java.util.Scanner;

public class MenuUtils {
	private static Scanner in;

	public static Scanner getSystemIn() {
		return in == null ? in = new Scanner(System.in) : in;
	}

	public static int prompt(String prompt, String... options) {
		System.out.println(prompt);
		for (int i = 0; i < options.length; i++)
			System.out.println((i + 1) + ". " + options[i]);
		while (true)
			try {
				return Integer.parseInt(nextLine()) - 1;
			} catch (NumberFormatException e) {
				System.err.println("That's not a valid option. Please enter a number from 1-" + options.length + '.');
			}
	}

	public static String nextLine() {
		return in.nextLine();
	}
}
