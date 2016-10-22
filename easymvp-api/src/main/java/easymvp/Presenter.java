package easymvp;

import javax.annotation.Nullable;

/**
 * Base interface for all presenters in Model-View-Presenter pattern.
 *<p>
 * The {@code Presenter} is responsible for orchestrating all the application’s use cases,
 * So it acts as a middle man that retrieves model from data-layer and shows it in the view.
 * <p>
 * In MVP pattern, view is made completely passive and is no longer responsible for updating itself from the model.
 * So it routes all user actions to the presenter and the presenter decides the action to take.
 *
 * @param <V> Generic type of view that the presenter interacts with. To make presenter
 *            testable, the view should be implemented with an interface and presenter refers to it instead of the view implementation.
 *            This will allow to write unit tests without any android SDK dependency.
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface Presenter<V> {

    /**
     * Called when the view attached to the screen.
     * <p>
     * This method will be invoked during {@link android.app.Activity#onStart()}, {@link android.app.Fragment#onResume()}
     * and {@link android.view.View#onAttachedToWindow()}.
     * @param view the view that the presenter interacts with
     */
    void onViewAttached(V view);

    /**
     * Called when the view detached from the screen.
     * <p>
     * This method will be invoked during {@link android.app.Activity#onStop()}, {@link android.app.Fragment#onPause()}
     * and {@link android.view.View#onDetachedFromWindow()}.
     */
    void onViewDetached();

    /**
     * Called when a user leaves the view completely. After the invocation of this method, presenter instance will be destroyed.
     * <p>
     * Note that on configuration changes like rotation, presenter instance will be alive.
     */
    void onDestroyed();

    /**
     * @return Returns true if the view is currently attached to the presenter, otherwise returns false.
     **/
    boolean isViewAttached();

    /**
     * @return Returns the attached view. If view is already detached, null will be returned.
     */
    @Nullable
    V getView();
}
