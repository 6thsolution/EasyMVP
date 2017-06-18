package easymvp;

import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

/**
 * The base class for implementing a {@link Presenter}.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class AbstractPresenter<V> implements Presenter<V> {

    private WeakReference<V> view;

    @Override
    public void onViewAttached(V view) {
        this.view = new WeakReference<>(view);
    }

    @Override
    public void onViewDetached() {
        if (view != null) view.clear();
    }

    @Override
    public void onDestroyed() {
        view = null;
    }

    @Nullable
    @Override
    public V getView() {
        return view == null ? null : view.get();
    }

    @Override
    public boolean isViewAttached() {
        return view != null && view.get() != null;
    }
}
