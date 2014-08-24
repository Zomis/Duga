
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
    
    private long lastExecutedTime = 0L;
    
    public ThrottlingThreadExecutor(final int throttleTiming) {
        super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.throttleTiming = throttleTiming;
    }
        
    @Override
    protected void beforeExecute(final Thread thread, final Runnable runnable) {
        if (System.currentTimeMillis() < lastExecutedTime + throttleTiming) {
            try {
                TimeUnit.MILLISECONDS.sleep(lastExecutedTime + throttleTiming - System.currentTimeMillis());
            } catch (InterruptedException ex) {
                thread.interrupt();
            }
        }
        super.beforeExecute(thread, runnable);
    }

    @Override
    protected void afterExecute(final Runnable runnable, final Throwable throwable) {
        lastExecutedTime = System.currentTimeMillis();
        super.afterExecute(runnable, throwable);
    }
}
