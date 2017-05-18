package easymvp.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * If you want to use multiple instances of a {@code Fragment}, {@code View} or a conductor {@code
 * Controller} class in a single {@code Activity}, {@code Fragment} or whatever, You will encounter
 * in an easyMVP limitation which will inject same instance of Presenter. To solve this you can use
 * {@code @PresenterId} annotation in your {@code Fragment} ,{@code View} or your conductor {@code
 * Controller} and pass a unique Id for each instantiated view.
 *
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
@Retention(value = RUNTIME)
@Target(value = {METHOD, FIELD})
public @interface PresenterId {
}
