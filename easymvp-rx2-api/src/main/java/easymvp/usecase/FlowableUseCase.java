package easymvp.usecase;

import javax.annotation.Nullable;

import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public abstract class FlowableUseCase<R, Q> extends UseCase<Flowable, Q> {
    private final FlowableTransformer<? super R, ? extends R> schedulersTransformer;

    public FlowableUseCase(final UseCaseExecutor useCaseExecutor,
                           final PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
        schedulersTransformer = new FlowableTransformer<R, R>() {
            @Override
            public Flowable<R> apply(Flowable<R> rObservable) {
                return rObservable.subscribeOn(useCaseExecutor.getScheduler())
                        .observeOn(postExecutionThread.getScheduler());
            }
        };
    }

    @Override
    public Flowable<R> execute(@Nullable Q param) {
        return interact(param).compose(getSchedulersTransformer());
    }

    @Override
    protected abstract Flowable<R> interact(@Nullable Q param);

    private FlowableTransformer<? super R, ? extends R> getSchedulersTransformer() {
        return schedulersTransformer;
    }
}
