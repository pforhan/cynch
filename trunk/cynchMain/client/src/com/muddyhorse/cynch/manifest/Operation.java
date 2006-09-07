package com.muddyhorse.cynch.manifest;

/**
 *
 */
public class Operation
{
    private final String   fileID;
    private OperationType  operation;
    private LocalFileInfo  local;
    private RemoteFileInfo remote;

    public Operation(String key) {
        operation = OperationType.nothing;
        fileID = key;
    }

    //
    // Utility methods:
    //
//    public void loadVector(String csv, boolean local) {
//        String[] v = csv.split(",");
//        if (v.length != 5) {
//            throw new IllegalArgumentException("For key: " + fileID + ", Wrong number of values (5 expected, found "
//                    + v.length + ") in Manifest entry:\n" + csv);
//        } // endif
//
//        if (local) {
//            // do not read type from local.
//            localPath = v[OperationField.path.ordinal()];
//            localVersion = Double.parseDouble(v[OperationField.version.ordinal()]);
//            localSize = (long) Double.parseDouble(v[OperationField.size.ordinal()]);
//            localDescription = v[OperationField.description.ordinal()];
//        } else {
//            downloadType = DownloadType.getTypeFromChar(v[OperationField.downloadType.ordinal()].charAt(0));
//            remotePath = v[OperationField.path.ordinal()];
//            remoteVersion = Double.parseDouble(v[OperationField.version.ordinal()]);
//            remoteSize = (long) Double.parseDouble(v[OperationField.size.ordinal()]);
//            remoteDescription = v[OperationField.description.ordinal()];
//        } // endif
//    }
//
//    public String toVector(boolean local) {
//        StringBuilder csv = new StringBuilder();
//        csv.append(downloadType.getTypeAsChar()).append(Constants.PROPERTY_SEPARATOR_CHAR);
//
//        if (local) {
//            csv.append(localPath).append(Constants.PROPERTY_SEPARATOR_CHAR);
//            csv.append(localVersion).append(Constants.PROPERTY_SEPARATOR_CHAR);
//            csv.append(localSize).append(Constants.PROPERTY_SEPARATOR_CHAR);
//            csv.append(localDescription).append(Constants.PROPERTY_SEPARATOR_CHAR);
//        } else {
//            csv.append(remotePath).append(Constants.PROPERTY_SEPARATOR_CHAR);
//            csv.append(remoteVersion).append(Constants.PROPERTY_SEPARATOR_CHAR);
//            csv.append(remoteSize).append(Constants.PROPERTY_SEPARATOR_CHAR);
//            csv.append(remoteDescription).append(Constants.PROPERTY_SEPARATOR_CHAR);
//        } // endif
//
//        return csv.toString();
//    }

    //
    // Data methods:
    //
    public String getFileID() {
        return fileID;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public LocalFileInfo getLocal() {
        return local;
    }

    public void setLocal(LocalFileInfo local) {
        this.local = local;
    }

    public RemoteFileInfo getRemote() {
        return remote;
    }

    public void setRemote(RemoteFileInfo remote) {
        this.remote = remote;
    }

    //
    // Overrides:
    //
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{").append(fileID).append(",").append(operation.getDescription()).append(",").append(
                local).append(",").append(remote).append(",").append("}");
        return sb.toString();
    }

//    /**
//     * copies path, size, and description from remote to local.  Does not copy version or type.
//     *
//     */
//    public void copyRemoteToLocal() {
//        // copy remote info to local info:
//        //  localPath = redirectPath;
//        setLocalPath(getPath());
//        setLocalSize(getRemoteSize());
//        setLocalDescription(getRemoteDescription());
//    }
}
