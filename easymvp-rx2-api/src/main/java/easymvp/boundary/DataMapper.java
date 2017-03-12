package easymvp.boundary;

import io.reactivex.functions.Function;

/**
 * {@code DataMapper} transforms entities from the format most convenient for the use cases, to the
 * format most convenient for the presentation layer.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class DataMapper<T, R> implements Function<T, R> {

}
