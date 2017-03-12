package easymvp.executer;

import io.reactivex.Scheduler;

/**
 * Created by megrez on 2017/3/12.
 */
public interface UseCaseExecutor {
    Scheduler getScheduler();
}
