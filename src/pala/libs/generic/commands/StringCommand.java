package pala.libs.generic.commands;

public class StringCommand {
	public final String command, inputText;
	public final String[] args;

	public StringCommand(final String command, final String inputText, final String... args) {
		this.command = command;
		this.inputText = inputText;
		this.args = args;
	}

}
