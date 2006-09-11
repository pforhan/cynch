package com.muddyhorse.cynch.manifest;

import java.math.BigDecimal;


public abstract class FileInfo
{
    //
    // Instance Variables:
    //
    private final String fileID;
    private BigDecimal version;
    private String description;

    //
    // Constructors:
    //
    protected FileInfo(String fileID) {
        this.fileID = fileID;
    }

    //
    // Data methods:
    //
    public String getFileID() {
        return fileID;
    }

    public BigDecimal getVersion() {
        return version;
    }

    public void setVersion(BigDecimal version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
