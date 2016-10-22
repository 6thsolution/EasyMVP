package easymvp.boundary;

import easymvp.usecase.CompletableUseCase;
import easymvp.usecase.ObservableUseCase;
import easymvp.usecase.UseCase;
import rx.Completable;
import rx.Observable;

/**
 * A helper class for using {@link ObservableUseCase} and {@link CompletableUseCase}.
 * <p>
 * You can use {@code RxUseCase} inside your presenter classes to execute use-cases with a data mapper.
 * <p>
 * Here is an example:
 * <pre class="prettyprint">
 *  //for an ObservableUseCase
 *  RxUseCase.with(yourUseCase).execute(someValue).mapper(yourDataMapper).subscribe(()-&gt;{//do your stuff here});
 *  //for a CompletableUseCase
 *  RxUseCase.with(yourUseCase).execute(someValue).doOnComplete(()-&gt;{//do your stuff here});
 * </pre>
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class RxUseCase {

    /**
     * Entry point for executing an {@link ObservableUseCase}.
     *
     * @param useCase given {@link ObservableUseCase}
     * @param <P>     The response value emitted by the Observable.
     * @param <Q>     The request value.
     * @return a {@link ObservableUseCaseWrapper} to execute given use case.
     */
    public static <P, Q> ObservableUseCaseWrapper<P, Q> with(ObservableUseCase<P, Q> useCase) {
        return new ObservableUseCaseWrapper<>(useCase);
    }

    /**
     * Entry point for executing a {@link CompletableUseCase}.
     *
     * @param useCase given {@link CompletableUseCase}
     * @param <Q>     The request value.
     * @return a {@link CompletableUseCaseWrapper} to execute given use case.
     */
    public static <Q> CompletableUseCaseWrapper<Q> with(CompletableUseCase<Q> useCase) {
        return new CompletableUseCaseWrapper<>(useCase);
    }

    public static class ObservableUseCaseWrapper<P, Q> {

        private final ObservableUseCase<P, Q> useCase;

        public ObservableUseCaseWrapper(ObservableUseCase<P, Q> useCase) {
            this.useCase = useCase;
        }

        /**
         * Executes an {@link ObservableUseCase} without any request value.
         *
         * @return A {@code UseCaseMapper} to transform emitted item from {@link ObservableUseCase}.
         */
        public UseCaseMapper<P> execute() {
            return execute(null);
        }

        /**
         * Executes an {@link ObservableUseCase} with given request value.
         *
         * @param arg the request value.
         * @return A {@code UseCaseMapper} to transform emitted item from {@link ObservableUseCase}.
         */
        public UseCaseMapper<P> execute(Q arg) {
            return new UseCaseMapper<>(useCase, useCase.execute(arg));
        }
    }

    public static class UseCaseMapper<P> {
        private final ObservableUseCase useCase;
        private final Observable<P> observable;

        public UseCaseMapper(ObservableUseCase useCase, Observable<P> observable) {
            this.useCase = useCase;
            this.observable = observable;
        }

        /**
         * Transforms emitted items from an {@link ObservableUseCase}.
         *
         * @param mapper a data mapper.
         * @param <R>    the result type of {@link DataMapper}.
         * @return an new Observable that emits the result type.
         */
        public <R> Observable<R> mapper(DataMapper<? super P, ? extends R> mapper) {
            return observable.map(mapper);
        }

        /**
         * No {@link DataMapper} will be applied.
         *
         * @return the source observable.
         */
        public Observable<P> noMapper() {
            return observable;
        }

        /**
         * Similar to {@link #mapper(DataMapper)}, but executes the {@link DataMapper#call(Object)}
         * method in the {@link UseCase#getUseCaseExecutor()} thread.
         *
         * @param mapper a data mapper.
         * @param <R>    the result type of {@link DataMapper}.
         * @return an new Observable that emits the result type.
         */
        public <R> Observable<R> mapperOnBackgroundThread(DataMapper<? super P, R> mapper) {
            return observable.observeOn(useCase.getUseCaseExecutor()).map(mapper)
                    .observeOn(useCase.getPostExecutionThread());
        }
    }

    public static class CompletableUseCaseWrapper<Q> {

        private final CompletableUseCase<Q> useCase;

        public CompletableUseCaseWrapper(CompletableUseCase<Q> useCase) {
            this.useCase = useCase;
        }

        /**
         * Executes a {@link CompletableUseCase} without any request value.
         *
         * @return the Completable returned by the use case.
         */
        public Completable execute() {
            return execute(null);
        }

        /**
         * Executes a {@link CompletableUseCase} with given request value.
         *
         * @param arg the request value.
         * @return the Completable returned by the use case.
         */
        public Completable execute(Q arg) {
            return useCase.execute(arg);
        }
    }

}


