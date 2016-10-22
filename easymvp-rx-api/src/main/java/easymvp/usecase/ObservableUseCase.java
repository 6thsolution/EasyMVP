package easymvp.usecase;

import javax.annotation.ParametersAreNullableByDefault;

import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import rx.Observable;

/**
 * Reactive version of a {@link UseCase}.
 * <p>
 * The presenter simply subscribes to {@link Observable} returned by the {@link #execute(Object)} method.
 *
 * @param <R> The response value emitted by the Observable.
 * @param <Q> The request value.
 * @author Saeed Masoumi (saeed@6thsolution.com)
 * @see  Observable
 */
public abstract class ObservableUseCase<R, Q> extends UseCase<Observable, Q> {

    private final Observable.Transformer<? super R, ? extends R> schedulersTransformer;

    public ObservableUseCase(final UseCaseExecutor useCaseExecutor,
                             final PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
        schedulersTransformer = new Observable.Transformer<R, R>() {
            @Override
            public Observable<R> call(Observable<R> rObservable) {
                return rObservable.subscribeOn(useCaseExecutor.getScheduler())
                        .observeOn(postExecutionThread.getScheduler());
            }
        };
    }

    @Override
    public Observable<R> execute(@ParametersAreNullableByDefault Q param) {
        return interact(param).compose(getSchedulersTransformer());
    }

    @Override
    protected abstract Observable<R> interact(@ParametersAreNullableByDefault Q param);

    private Observable.Transformer<? super R, ? extends R> getSchedulersTransformer() {
        return schedulersTransformer;
    }
}
