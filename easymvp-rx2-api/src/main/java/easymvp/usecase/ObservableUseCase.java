package easymvp.usecase;

import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

import javax.annotation.Nullable;

/**
 * Created by megrez on 2017/3/12.
 */
public abstract class ObservableUseCase<R, Q> extends UseCase<Observable, Q> {

    private final ObservableTransformer<? super R, ? extends R> schedulersTransformer;

    public ObservableUseCase(final UseCaseExecutor useCaseExecutor,
                             final PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
        schedulersTransformer = new ObservableTransformer<R, R>() {
            @Override
            public Observable<R> apply(Observable<R> rObservable) {
                return rObservable.subscribeOn(useCaseExecutor.getScheduler())
                        .observeOn(postExecutionThread.getScheduler());
            }
        };
    }

    @Override
    public Observable<R> execute(@Nullable Q param) {
        return interact(param).compose(getSchedulersTransformer());
    }

    @Override
    protected abstract Observable<R> interact(@Nullable Q param);

    private ObservableTransformer<? super R, ? extends R> getSchedulersTransformer() {
        return schedulersTransformer;
    }
}
