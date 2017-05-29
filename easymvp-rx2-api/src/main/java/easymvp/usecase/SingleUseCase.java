package easymvp.usecase;

import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.annotations.Nullable;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public abstract class SingleUseCase<R, Q> extends UseCase<Single, Q> {
    private final SingleTransformer<? super R, ? extends R> schedulersTransformer;

    public SingleUseCase(final UseCaseExecutor useCaseExecutor,
                         final PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
        schedulersTransformer = new SingleTransformer<R, R>() {
            @Override
            public Single<R> apply(Single<R> single) {
                return single.subscribeOn(useCaseExecutor.getScheduler())
                        .observeOn(postExecutionThread.getScheduler());
            }
        };
    }

    @Override
    public Single<R> execute(@Nullable Q param) {
        return interact(param).compose(getSchedulersTransformer());
    }

    @Override
    protected abstract Single<R> interact(@Nullable Q param);

    private SingleTransformer<? super R, ? extends R> getSchedulersTransformer() {
        return schedulersTransformer;
    }
}
