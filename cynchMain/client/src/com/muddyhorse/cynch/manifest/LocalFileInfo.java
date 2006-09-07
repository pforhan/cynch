package com.muddyhorse.cynch.manifest;

import java.io.File;

import com.muddyhorse.cynch.Constants;

/**
 * 
 * @author paf2009
 *
 */
public class LocalFileInfo extends FileInfo
{
    //
    // Instance Variables:
    //
    private String pathString;
    private File path;

    //
    // Constructors:
    //
    public LocalFileInfo(String fileID) {
        super(fileID);
    }

    public LocalFileInfo(String fileID, String csv) {
        super(fileID);
        loadFromString(csv);
    }

    //
    // Utility methods:
    //
    /**
     * Assumes csv is full-path,version,description
     * consider moving to LocalManifest if/when created
     */
    public void loadFromString(String csv) {
        String[] v = csv.split(Constants.PROPERTY_SEPARATOR);
        
        pathString = v[0];
        setPath(new File(pathString));
        setVersion(Double.parseDouble(v[1]));
        setDescription(v[2]);
    }

    public boolean exists() {
        File lpath = getPath();
        if (lpath != null) {
            return lpath.exists();

        } else {
            return false;
        } // endif
    }

    public boolean canWrite() {
        File lpath = getPath();
        if (lpath != null) {
            return lpath.canWrite();
            
        } else {
            return false;
        } // endif
    }

    /** returns the local file size, 0 if non-existant, or -1 if local file not specified (null)
     * 
     * @return
     */
    public long getSize() {
        File lpath = getPath();
        if (lpath != null) {
            return lpath.length();
            
        } else {
            return -1;
        } // endif
    }

    //
    // Data methods:
    //
    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    //
    // Overrides:
    //
    @Override
    public String toString() {
        return pathString + Constants.PROPERTY_SEPARATOR + getVersion() + Constants.PROPERTY_SEPARATOR + getDescription() + Constants.PROPERTY_SEPARATOR;
    }

}
