/**
 * 
 */
package com.muddyhorse.cynch;

public enum OperationType {
    nothing("Current Version Installed"), delete("Delete (no longer needed)"), update("Update"), download("Download/Install");
    private final String description;
    private OperationType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}