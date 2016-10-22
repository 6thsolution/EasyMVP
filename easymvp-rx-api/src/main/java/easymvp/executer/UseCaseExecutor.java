package easymvp.executer;

import rx.Scheduler;

/**
 * Represents an asynchronous execution for {@link easymvp.usecase.UseCase}. It's useful to execute use cases out of
 * the UI thread to prevent it from freezing.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface UseCaseExecutor {
    Scheduler getScheduler();
}
