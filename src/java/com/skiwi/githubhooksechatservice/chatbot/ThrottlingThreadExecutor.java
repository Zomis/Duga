
package com.skiwi.githubhooksechatservice.chatbot;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Frank van Heeswijk
 */
public class ThrottlingThreadExecutor extends ThreadPoolExecutor {
    private final int throttleTiming;
    private final int maxBurst;
    private final int minimumDelay;
    
    private long lastExecutedTime = 0L;
    private int currentBurst = 0;
    
    public ThrottlingThreadExecutor(final int throttleTiming, final int maxBurst, final int minimumDelay) {
        super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.throttleTiming = throttleTiming;
        this.maxBurst = maxBurst;
        this.minimumDelay = minimumDelay;
    }
        
    @Override
    protected void beforeExecute(final Thread thread, final Runnable runnable) {
        currentBurst++;
        try {
            TimeUnit.MICROSECONDS.sleep(minimumDelay);
        } catch (InterruptedException ex) {
            thread.interrupt();
        }
        if (System.currentTimeMillis() < lastExecutedTime + throttleTiming) {
            if (currentBurst == maxBurst) {
                try {
                    TimeUnit.MILLISECONDS.sleep(lastExecutedTime + throttleTiming - System.currentTimeMillis());
                    currentBurst = 0;
                } catch (InterruptedException ex) {
                    thread.interrupt();
                }
            }
        }
        else {
            currentBurst = 0;
        }
        super.beforeExecute(thread, runnable);
    }

    @Override
    protected void afterExecute(final Runnable runnable, final Throwable throwable) {
        lastExecutedTime = System.currentTimeMillis();
        super.afterExecute(runnable, throwable);
    }
}
