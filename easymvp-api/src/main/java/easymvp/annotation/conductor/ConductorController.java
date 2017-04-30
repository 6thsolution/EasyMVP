package easymvp.annotation.conductor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates a {@code com.bluelinelabs.conductor.ConductorController} class to binds {@link
 * easymvp.Presenter} lifecycle.
 *
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
@Retention(value = RUNTIME)
@Target(value = TYPE)
public @interface ConductorController {

    /**
     * @return the presenter class.
     */
    Class<? extends easymvp.Presenter> presenter();
}
