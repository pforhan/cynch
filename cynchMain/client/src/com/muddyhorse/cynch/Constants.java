package com.muddyhorse.cynch;

/**
 *
 */
public interface Constants
{
    //
    // Class variables:
    //

    // button commands:
    //  used by buttons:
    public static final String   CMD_UPDATE          = "upd";
    public static final String   CMD_RUN             = "run";
    public static final String   CMD_EXIT            = "xit";
    //  used by dlg:
    public static final String   CMD_CANCEL          = "can";

    // ini file settings:
    public static final String   INI_APP_DESC        = "AppDescription";
    public static final String   INI_APP_SHORT_NAME  = "AppShortName";
    public static final String   INI_LOCAL_BASE      = "LocalBase";
    public static final String   INI_LOCAL_CORE      = "LocalCoreName";
    public static final String   INI_REMOTE_BASE     = "RemoteBase";
    public static final String   INI_REMOTE_CORE     = "RemoteCoreName";
    public static final String   INI_CLASSPATH       = "ClassPath";
    public static final String   INI_MAIN_CLASS      = "MainClass";
    public static final String   INI_ARGS            = "Args";
    public static final String   INI_UPDATE_DESC     = "UpdateDesc";
    public static final String   INI_EXEC_ARGS       = "ExecArgs";
    public static final String   INI_EXEC_NAME       = "ExecName";
    public static final String   INI_EXEC_TYPE       = "ExecType";
    public static final String   INI_UPD_FRAME_TITLE = "AppUpdFrameTitle";
    public static final String   INI_INI_NAME        = "ININame";
    public static final String   INI_START_EXEC      = "StartExec";
    public static final String   INI_START_EXEC_DL   = "StartExecDL";
    public static final String   INI_DU_DIR          = "DUDir";

    // parameter settings:
//     public static final String PARM_USER           = "user";
//     public static final String PARM_PASSWORD       = "pass";
    public static final String   PARM_INI            = "ini";
//    public static final String PARM_INSTALL        = "install";
    public static final String DEFAULT_INI_NAME    = "/cynch.ini";

    // Operation-specific stuff:
    public static enum DownloadType {
        all, critical, core, optional;
    }
//    public static final int      TYPE_ALL            = -1;
//    public static final int      TYPE_CRITICAL       = 0;
//    public static final int      TYPE_CORE           = 1;
//    public static final int      TYPE_OPTIONAL       = 2;

    public static enum OperationType {
        nothing("Current Version Installed"), delete("Delete (no longer needed)"), update("Update"), download("Download/Install");
        private final String description;
        private OperationType(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }
    }
//    public static final int      OP_NOTHING          = 0;
//    public static final int      OP_DELETE           = 1;
//    public static final int      OP_UPDATE           = 2;
//    public static final int      OP_DOWNLOAD         = 3;

//    public static final String[] OP_DESCRIPTIONS     = {
//            "Current Version Installed", "Delete (no longer needed)", "Update", "Download/Install"
//                                                     };

}
