package pala.libs.generic.util.functions;

public interface TriDoubleFunction<F, S, T> {
	double run(F first, S second, T third);
}
