package com.muddyhorse.cynch;

/**
 *
 */
public class Operation
{
    private static final int        OFS_DL_TYPE       = 0;  // where ofs == offset...
    private static final int        OFS_PATH       = 1;
    private static final int        OFS_VERSION    = 2;
    private static final int        OFS_SIZE       = 3;
    private static final int        OFS_DESC       = 4;
    //    private static final int OFS_REDIR    = 5;

    private final String            fileID;
    private OperationType operation;
    private DownloadType  type;
    private String                  localDescription;
    private String                  localPath;
    private double                  localVersion;
    private long                    localSize;
    private String                  remoteDescription;
    private String                  remotePath;
    private double                  remoteVersion;
    private long                    remoteSize;

    //    private String   redirectPath;

    public Operation(String key) {
        operation = OperationType.nothing;
        fileID = key;
        type = DownloadType.optional;
    }

    //
    // Utility methods:
    //
    private void setDLTypeChar(char t) {
    }

    private char getDLTypeChar() {
        switch (type) {
            case critical: // '!'
                return Constants.TYPE_CHAR_CRIT;
            case optional: // '?'
                return Constants.TYPE_CHAR_OPT;
            default: // '.'
                return Constants.TYPE_CHAR_REQUIRED;
        } // endswitch
    }

    public void loadVector(String csv, boolean local) {
        String[] v = csv.split(",");
        if (v.length != 5) {
            throw new IllegalArgumentException("For key: " + fileID + ", Wrong number of values (5 expected, found "
                    + v.length + ") in Manifest entry:\n" + csv);
        } // endif

        if (local) {
            // do not read type from local.
            localPath = v[OFS_PATH];
            localVersion = Double.parseDouble(v[OFS_VERSION]);
            localSize = (long) Double.parseDouble(v[OFS_SIZE]);
            localDescription = v[OFS_DESC];
        } else {
            setDLTypeChar(v[OFS_DL_TYPE].charAt(0));
            remotePath = v[OFS_PATH];
            remoteVersion = Double.parseDouble(v[OFS_VERSION]);
            remoteSize = (long) Double.parseDouble(v[OFS_SIZE]);
            remoteDescription = v[OFS_DESC];
        } // endif
    }

    public String toVector(boolean local) {
        StringBuilder csv = new StringBuilder();
        csv.append(getDLTypeChar()).append(Constants.PROPERTY_SEPARATOR_CHAR);

        if (local) {
            csv.append(localPath).append(Constants.PROPERTY_SEPARATOR_CHAR);
            csv.append(localVersion).append(Constants.PROPERTY_SEPARATOR_CHAR);
            csv.append(localSize).append(Constants.PROPERTY_SEPARATOR_CHAR);
            csv.append(localDescription).append(Constants.PROPERTY_SEPARATOR_CHAR);
        } else {
            csv.append(remotePath).append(Constants.PROPERTY_SEPARATOR_CHAR);
            csv.append(remoteVersion).append(Constants.PROPERTY_SEPARATOR_CHAR);
            csv.append(remoteSize).append(Constants.PROPERTY_SEPARATOR_CHAR);
            csv.append(remoteDescription).append(Constants.PROPERTY_SEPARATOR_CHAR);
        } // endif

        return csv.toString();
    }

    //
    // Data methods:
    //
    public String getFileID() {
        return fileID;
    }

    public String getLocalDescription() {
        return localDescription;
    }

    public void setLocalDescription(String localDescription) {
        this.localDescription = localDescription;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public double getLocalSize() {
        return localSize;
    }

    public void setLocalSize(long localSize) {
        this.localSize = localSize;
    }

    public double getLocalVersion() {
        return localVersion;
    }

    public void setLocalVersion(double localVersion) {
        this.localVersion = localVersion;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public String getRemoteDescription() {
        return remoteDescription;
    }

    public void setRemoteDescription(String remoteDescription) {
        this.remoteDescription = remoteDescription;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public long getRemoteSize() {
        return remoteSize;
    }

    public void setRemoteSize(long remoteSize) {
        this.remoteSize = remoteSize;
    }

    public double getRemoteVersion() {
        return remoteVersion;
    }

    public void setRemoteVersion(double remoteVersion) {
        this.remoteVersion = remoteVersion;
    }

    public DownloadType getDownloadType() {
        return type;
    }

    public void setDownloadType(DownloadType type) {
        this.type = type;
    }

    //
    // Overrides:
    //
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{").append(fileID).append(",").append(operation.getDescription()).append(",").append(
                localDescription).append(",").append(localPath).append(",").append(localSize).append(",").append(
                localVersion).append(",").append(remoteVersion).append(",").append(remotePath).append(",").append(
                remoteSize).append(",").append(remoteDescription).append("}");
        return sb.toString();
    }

    /**
     * copies path, size, and description from remote to local.  Does not copy version or type.
     *
     */
    public void copyRemoteToLocal() {
        // copy remote info to local info:
        //  localPath = redirectPath; // TODO implement redirected paths one day...
        setLocalPath(getRemotePath());
        setLocalSize(getRemoteSize());
        setLocalDescription(getRemoteDescription());
    }
}
