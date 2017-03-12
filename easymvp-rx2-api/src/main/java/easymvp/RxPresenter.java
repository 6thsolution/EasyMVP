package easymvp;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by megrez on 2017/3/12.
 */
public class RxPresenter<V> extends AbstractPresenter<V> {
    private CompositeDisposable subscriptions = new CompositeDisposable();

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        subscriptions.dispose();
    }

    public void addSubscription(Disposable subscription) {
        subscriptions.add(subscription);
    }

    public void removeSubscription(Disposable subscription) {
        subscriptions.remove(subscription);
    }
}
