package com.muddyhorse.cynch;

/**
 *
 */
public interface ProgressListener
{
    public void starting(Operation op);

    public void progress(String name, String desc, long amount, long total) throws InterruptedException;

    public void finished(Operation op, boolean success);
}
