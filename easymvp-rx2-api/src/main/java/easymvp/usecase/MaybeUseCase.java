package easymvp.usecase;

import javax.annotation.Nullable;

import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import io.reactivex.Maybe;
import io.reactivex.MaybeTransformer;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
public abstract class MaybeUseCase<R, Q> extends UseCase<Maybe, Q> {
    private final MaybeTransformer<? super R, ? extends R> schedulersTransformer;

    public MaybeUseCase(final UseCaseExecutor useCaseExecutor,
                        final PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
        schedulersTransformer = new MaybeTransformer<R, R>() {
            @Override
            public Maybe<R> apply(Maybe<R> single) {
                return single.subscribeOn(useCaseExecutor.getScheduler())
                        .observeOn(postExecutionThread.getScheduler());
            }
        };
    }

    @Override
    public Maybe<R> execute(@Nullable Q param) {
        return interact(param).compose(getSchedulersTransformer());
    }

    @Override
    protected abstract Maybe<R> interact(@Nullable Q param);

    private MaybeTransformer<? super R, ? extends R> getSchedulersTransformer() {
        return schedulersTransformer;
    }
}
