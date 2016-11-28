package com.sixthsolution.easymvp.test.usecase;


import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import easymvp.usecase.ObservableUseCase;
import rx.Observable;
import rx.functions.Func0;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

public class IsNumberOdd extends ObservableUseCase<Boolean, Integer> {

    public IsNumberOdd(UseCaseExecutor useCaseExecutor,
                       PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
    }

    @Override
    protected Observable<Boolean> interact(final Integer number) {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                return Observable.just(number % 2 != 0);
            }
        });
    }
}
