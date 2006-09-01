package com.muddyhorse.cynch;

// java core imports:
// java swing imports:

// Common imports:
// Localized imports:

// GTCS Imports:

/**
  *
  */
public interface DUProgressListener
{
    public void starting(DUOperation op);
    public void progress(String name, String desc, int amount, int total) throws InterruptedException;
    public void finished(DUOperation op, boolean success);
}
