package com.sixthsolution.easymvp.test.usecase;

import org.junit.Before;
import org.junit.Test;

import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

public class UseCaseTest {


    private UseCaseExecutor useCaseExecutor;
    private PostExecutionThread postExecutionThread;

    @Before
    public void init() {
        useCaseExecutor = new UseCaseExecutor() {
            @Override
            public Scheduler getScheduler() {
                return Schedulers.immediate();
            }
        };
        postExecutionThread = new PostExecutionThread() {
            @Override
            public Scheduler getScheduler() {
                return Schedulers.immediate();
            }
        };
    }

    @Test
    public void test_use_case_execution() {
        TestSubscriber<Boolean> subscriber = new TestSubscriber<>();
        IsNumberOdd isNumberOdd = new IsNumberOdd(useCaseExecutor, postExecutionThread);
        isNumberOdd.execute(10).subscribe(subscriber);
        subscriber.assertValue(false);
        subscriber.assertCompleted();
    }
}
