package com.muddyhorse.cynch;

/**
 *
 */
public interface ProgressListener
{
    public void starting(Operation op);

    public void progress(String name, String desc, int amount, int total) throws InterruptedException;

    public void finished(Operation op, boolean success);
}
