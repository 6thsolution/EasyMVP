package easymvp.usecase;


import javax.annotation.Nullable;

import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import rx.Completable;

/**
 * Reactive version of a {@link UseCase}.
 * <p>
 * It's useful for use-cases without any response value.
 *
 * @param <Q> The request value.
 * @author Saeed Masoumi (saeed@6thsolution.com)
 * @see Completable
 */
public abstract class CompletableUseCase<Q> extends UseCase<Completable, Q> {

    private final Completable.Transformer schedulersTransformer;

    public CompletableUseCase(final UseCaseExecutor useCaseExecutor,
                              final PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
        schedulersTransformer = new Completable.Transformer() {
            @Override
            public Completable call(Completable completable) {
                return completable.subscribeOn(useCaseExecutor.getScheduler())
                        .observeOn(postExecutionThread.getScheduler());
            }
        };
    }

    @Override
    public Completable execute(@Nullable Q param) {
        return interact(param).compose(getSchedulersTransformer());
    }

    private Completable.Transformer getSchedulersTransformer() {
        return schedulersTransformer;
    }
}
