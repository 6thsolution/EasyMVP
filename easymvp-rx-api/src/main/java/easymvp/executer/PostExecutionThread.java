package easymvp.executer;

import rx.Scheduler;

/**
 * When the use case execution is done in its executor thread, It's time to update UI on the Event
 * Dispatch thread.
 * <p>
 * An implementation of this interface will change the execution context and update the UI.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface PostExecutionThread {
    Scheduler getScheduler();
}
