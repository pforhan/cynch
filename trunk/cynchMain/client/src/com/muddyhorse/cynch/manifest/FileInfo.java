package com.muddyhorse.cynch.manifest;


public abstract class FileInfo
{
    //
    // Instance Variables:
    //
    private final String fileID;
    private double version;
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

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
