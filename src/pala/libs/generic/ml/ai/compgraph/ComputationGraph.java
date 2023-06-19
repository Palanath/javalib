package pala.libs.generic.ml.ai.compgraph;

import java.util.ArrayList;
import java.util.List;

public class ComputationGraph {
	private final List<Node> inputs = new ArrayList<>();
	private final Node output;

	ComputationGraph(Node output, Node... inputs) {
		this.output = output;
	}
}
