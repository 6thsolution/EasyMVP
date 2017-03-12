package easymvp.usecase;

import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;

import javax.annotation.Nullable;

/**
 * Created by megrez on 2017/3/12.
 */
public abstract class CompletableUseCase<Q> extends UseCase<Completable, Q> {

    private final CompletableTransformer schedulersTransformer;

    public CompletableUseCase(final UseCaseExecutor useCaseExecutor,
                              final PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
        schedulersTransformer = new CompletableTransformer() {
            @Override
            public Completable apply(Completable completable) {
                return completable.subscribeOn(useCaseExecutor.getScheduler())
                        .observeOn(postExecutionThread.getScheduler());
            }
        };
    }

    @Override
    public Completable execute(@Nullable Q param) {
        return interact(param).compose(getSchedulersTransformer());
    }

    private CompletableTransformer getSchedulersTransformer() {
        return schedulersTransformer;
    }
}
