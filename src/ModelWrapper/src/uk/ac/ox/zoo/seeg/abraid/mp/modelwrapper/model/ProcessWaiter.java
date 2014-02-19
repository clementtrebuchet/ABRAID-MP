package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

/**
 * Created by zool1112 on 13/02/14.
 */
public interface ProcessWaiter {
    /**
     * Causes the current thread to wait, if necessary, until the owned process has terminated.
     * @return The exit code of the process.
     * @throws InterruptedException Thrown if the current thread is interrupted by another thread while it is waiting.
     */
    int waitForProcess() throws InterruptedException;
}
