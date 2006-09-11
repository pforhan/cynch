package com.muddyhorse.cynch.admin;


public enum AdminAction {
    unchanged("-"), updated, added, removed;

    //
    // Instance Variables:
    //
    private final String displayString;

    //
    // Constructors:
    //
    private AdminAction() {
        this(null);
    }

    private AdminAction(String displayString) {
        this.displayString = displayString;
    }

    public String getDisplayString() {
        if (displayString != null) {
            return displayString;

        } else {
            return name();
        } // endif
    }

    //
    // Static Utility methods:
    //
//    public static Color getColor(FileInfoStatus status) {
//        Color rv;
//
//        switch (status) {
//            case added:
//                rv = Color.blue;
//            break;
//            case removed:
//                rv = Color.red;
//            break;
//            case updated:
//                rv = Color.green;
//            break;
//            case unchanged:
//            default:
//                rv = Color.darkGray;
//        } // endswitch
//        
//        return rv;
//    }
}
