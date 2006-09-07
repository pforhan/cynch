/**
 * 
 */
package com.muddyhorse.cynch.manifest;

public enum OperationType {
    nothing("Current Version Installed"),
    delete("Delete (no longer needed)"),
    update("Update"),
    download("Download/Install");

    //
    // Instance Variables:
    //
    private final String description;

    //
    // Constructors:
    //
    private OperationType(String description) {
        this.description = description;
    }

    //
    // Data methods:
    //
    public String getDescription() {
        return description;
    }
}