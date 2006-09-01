package com.muddyhorse.cynch;

import java.util.Vector;

/**
 *
 */
public class Operation implements Constants
{
    private static final int  OFS_TYPE       = 0;  // where ofs == offset...
    private static final int  OFS_PATH       = 1;
    private static final int  OFS_VERSION    = 2;
    private static final int  OFS_SIZE       = 3;
    private static final int  OFS_DESC       = 4;
    //    private static final int OFS_REDIR    = 5;

    private static final char TYPE_CHAR_CRIT = '!';
    private static final char TYPE_CHAR_CORE = '.';
    private static final char TYPE_CHAR_OPT  = '?';

    public String             fileID;
    public int                operation;
    public int                type;
    public String             localDescription;
    public String             localPath;
    public Double             localVersion;
    public Double             localSize;
    public String             remoteDescription;
    public String             remotePath;
    public Double             remoteVersion;
    public Double             remoteSize;

    //    public String   redirectPath;

    public Operation(String key) {
        operation = OP_NOTHING;
        fileID = key;
        type = TYPE_OPTIONAL;
    }

    //
    // Utility methods:
    //
    private void setTypeChar(char t) {
        switch (t) {
            case TYPE_CHAR_CRIT: // '!'
                type = TYPE_CRITICAL;
            break;
            case TYPE_CHAR_OPT: // '?'
                type = TYPE_OPTIONAL;
            break;
            default: // '.'
                type = TYPE_CORE;
        } // endswitch
    }

    private char getTypeChar() {
        switch (type) {
            case TYPE_CRITICAL: // '!'
                return TYPE_CHAR_CRIT;
            case TYPE_OPTIONAL: // '?'
                return TYPE_CHAR_OPT;
            default: // '.'
                return TYPE_CHAR_CORE;
        } // endswitch
    }

    public void loadVector(Vector v, boolean local) {
        if (local) {
            // do not read type from local.
            localPath = (String) v.elementAt(OFS_PATH);
            localVersion = (Double) v.elementAt(OFS_VERSION);
            localSize = (Double) v.elementAt(OFS_SIZE);
            localDescription = (String) v.elementAt(OFS_DESC);
        } else {
            setTypeChar(((String) v.elementAt(OFS_TYPE)).charAt(0));
            remotePath = (String) v.elementAt(OFS_PATH);
            remoteVersion = (Double) v.elementAt(OFS_VERSION);
            remoteSize = (Double) v.elementAt(OFS_SIZE);
            remoteDescription = (String) v.elementAt(OFS_DESC);
        } // endif
    }

    public Vector toVector(boolean local) {
        Vector v = new Vector(5);
        v.setSize(5);
        v.setElementAt("" + getTypeChar(), OFS_TYPE); // always set type...
        if (local) {
            v.setElementAt(localPath, OFS_PATH);
            v.setElementAt(localVersion, OFS_VERSION);
            v.setElementAt(localSize, OFS_SIZE);
            v.setElementAt(localDescription, OFS_DESC);
        } else {
            v.setElementAt(remotePath, OFS_PATH);
            v.setElementAt(remoteVersion, OFS_VERSION);
            v.setElementAt(remoteSize, OFS_SIZE);
            v.setElementAt(remoteDescription, OFS_DESC);
        } // endif
        return v;
    }

    //
    // Overrides:
    //
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{").append(fileID).append(",").append(OP_DESCRIPTIONS[operation]).append(",").append(
                localDescription).append(",").append(localPath).append(",").append(localSize).append(",").append(
                localVersion).append(",").append(remoteVersion).append(",").append(remotePath).append(",").append(
                remoteSize).append(",").append(remoteDescription).append("}");
        return sb.toString();
    }
}
