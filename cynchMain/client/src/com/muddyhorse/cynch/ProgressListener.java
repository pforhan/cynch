package com.muddyhorse.cynch;

// java core imports:
// java swing imports:

// Common imports:
// Localized imports:

// GTCS Imports:

/**
 *
 */
public interface ProgressListener
{
    public void starting(Operation op);

    public void progress(String name, String desc, int amount, int total) throws InterruptedException;

    public void finished(Operation op, boolean success);
}
