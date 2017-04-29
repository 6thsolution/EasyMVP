package easymvp.annotation.conductor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates a {@code com.bluelinelabs.conductor.Controller} class to binds {@link
 * easymvp.Presenter} lifecycle.
 *
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
@Retention(value = RUNTIME)
@Target(value = TYPE)
public @interface Controller {

    /**
     * @return the presenter class.
     */
    Class<? extends easymvp.Presenter> presenter();
}
