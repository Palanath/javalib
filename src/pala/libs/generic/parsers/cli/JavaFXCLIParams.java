package pala.libs.generic.parsers.cli;

import javafx.application.Application.Parameters;

public class JavaFXCLIParams extends CLIParams {
	public JavaFXCLIParams(Parameters params) {
		super(params.getNamed(), params.getUnnamed());
	}
}
