/**
 * 
 */
package com.muddyhorse.cynch;

public enum DownloadType {
    critical("Critical"), required("Required"), optional("Optional"), all("All");
    public static DownloadType getTypeFromChar(char t) {
        DownloadType type;

        switch (t) {
            case Constants.TYPE_CHAR_CRIT: // '!'
                type = DownloadType.critical;
            break;
            case Constants.TYPE_CHAR_OPT: // '?'
                type = DownloadType.optional;
            break;
            case Constants.TYPE_CHAR_REQUIRED: // '.'
            default:                       // '.'
                type = DownloadType.required;
        } // endswitch
        
        return type;
    }
    private final String description;
    private DownloadType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}