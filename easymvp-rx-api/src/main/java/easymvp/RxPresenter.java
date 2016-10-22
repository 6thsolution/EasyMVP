package easymvp;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


/**
 * A base class for implementing a {@link Presenter} with RxJava functionality.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class RxPresenter<V> extends AbstractPresenter<V> {

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        subscriptions.unsubscribe();
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public void removeSubscription(Subscription subscription) {
        subscriptions.remove(subscription);
    }
}
