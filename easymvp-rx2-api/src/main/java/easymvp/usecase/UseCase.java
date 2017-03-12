package easymvp.usecase;

import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import io.reactivex.Scheduler;

import javax.annotation.Nullable;

/**
 * Created by megrez on 2017/3/12.
 */
public abstract class UseCase<P,Q> {
    private final UseCaseExecutor useCaseExecutor;
    private final PostExecutionThread postExecutionThread;

    public UseCase(UseCaseExecutor useCaseExecutor,
                   PostExecutionThread postExecutionThread) {
        this.useCaseExecutor = useCaseExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    /**
     * Executes use case. It should call {@link #interact(Object)} to get response value.
     */
    public abstract P execute(@Nullable Q param);

    /**
     * A hook for interacting with the given parameter(request value) and returning a response value for
     * each concrete implementation.
     * <p>
     * It should be called inside {@link #execute(Object)}.
     *
     * @param param The request value.
     * @return Returns the response value.
     */
    protected abstract P interact(@Nullable Q param);

    public Scheduler getUseCaseExecutor() {
        return useCaseExecutor.getScheduler();
    }

    public Scheduler getPostExecutionThread() {
        return postExecutionThread.getScheduler();
    }
}
