package com.muddyhorse.cynch.manifest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.muddyhorse.cynch.Constants;

public class RemoteFileInfo extends FileInfo
{
    //
    // Instance Variables:
    //
    private URL                    path;
    private long                   size;
    private DownloadType           downloadType = DownloadType.optional;
    private PostDownloadActionType action       = PostDownloadActionType.nothing;
    private String                 rawURL;
    private File                   localFile;

    //
    // Constructors:
    //
    public RemoteFileInfo(String fileID) {
        super(fileID);
    }

    //
    // Utility methods:
    //
    /**
     * Assumes csv is dltype,URL,version,size,description,postaction,localPath
     * consider moving to RemoteManifest if/when created
     * @throws MalformedURLException 
     */
    public void loadFromString(URL base, String csv) throws MalformedURLException {
        String[] v = csv.split(Constants.PROPERTY_SEPARATOR);

        setDownloadType(DownloadType.getTypeFromChar(v[0].charAt(0)));
        rawURL = v[1];
        if (rawURL.contains("://")) {
            // this is an absolute URL, don't use the base
            setPath(new URL(rawURL));

        } else {
            // base must be non-null for us to construct a relative url
            if (base != null) {
                setPath(new URL(base, rawURL));

            } else {
                throw new IllegalStateException("Unable to construct remote path!");
            } // endif
        } // endif

        setVersion(Double.parseDouble(v[2]));
        setSize(Long.parseLong(v[3]));
        setDescription(v[4]);
        setAction(PostDownloadActionType.valueOf(v[5]));

        // TODO need some way to say "same as relative path" -- may need extra var
        localFile = new File(v[6]);

    }

    //
    // Data methods:
    //
    public DownloadType getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(DownloadType downloadType) {
        this.downloadType = downloadType;
    }

    /**
     * Get the full URL to the resource
     */
    public URL getPath() {
        return path;
    }

    public void setPath(URL remotePath) {
        this.path = remotePath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public PostDownloadActionType getAction() {
        return action;
    }

    public void setAction(PostDownloadActionType action) {
        this.action = action;
    }

    public LocalFileInfo getLocalInfo(File base) {
        LocalFileInfo rv = new LocalFileInfo(getFileID());
        rv.setDescription(getDescription());
        rv.setVersion(getVersion());
        if (localFile.isAbsolute()) {
            // use the absolute fella, ignore the base:
            rv.setPath(localFile);

        } else {
            // relative, go from the base:
            rv.setPath(new File(base, localFile.getPath()));
        } // endif

        return rv;
    }

    public String getRawURL() {
        return rawURL;
    }

    public void setRawURL(String rawURL) {
        this.rawURL = rawURL;
    }

    public String getRawLocalPath() {
        if (localFile != null) {
            return localFile.getPath();
        } // endif

        return null;
    }
    
    public void setRawLocalPath(String path) {
        if (path != null) {
            localFile = new File(path);
        } // endif
    }

    //
    // Overrides:
    //
    @Override
    public String toString() {
        return rawURL + Constants.PROPERTY_SEPARATOR 
        + getVersion() + Constants.PROPERTY_SEPARATOR 
        + getSize() + Constants.PROPERTY_SEPARATOR 
        + getDescription() + Constants.PROPERTY_SEPARATOR
        + getAction() + Constants.PROPERTY_SEPARATOR
        + getRawLocalPath() + Constants.PROPERTY_SEPARATOR;
    }
}
