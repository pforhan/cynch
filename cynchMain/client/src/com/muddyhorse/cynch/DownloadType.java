/**
 * 
 */
package com.muddyhorse.cynch;

public enum DownloadType {
    critical("Critical"), required("Required"), optional("Optional"), all("All");
    private final String description;
    private DownloadType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}