/**
 * 
 */
package com.muddyhorse.cynch.manifest;

import com.muddyhorse.cynch.Constants;

public enum DownloadType {
    critical("Critical", Constants.TYPE_CHAR_CRIT),
    required("Required", Constants.TYPE_CHAR_REQUIRED),
    optional("Optional", Constants.TYPE_CHAR_OPT),
//    delete  ("Delete"  , Constants.TYPE_CHAR_DEL),
    all     ("All"     , (char)-1);

    //
    // Static Utility methods:
    //
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

    //
    // Instance Variables:
    //
    private final String description;
    private final char typeChar;

    //
    // Constructors:
    //
    private DownloadType(String description, char typeCode) {
        this.description = description;
        typeChar = typeCode;
    }
    
    //
    // Data methods:
    //
    public String getDescription() {
        return description;
    }
    public char getTypeAsChar() {
        return typeChar;
    }
}