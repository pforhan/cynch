package com.muddyhorse.cynch;

import java.awt.Color;
import java.awt.event.WindowAdapter;

/**
 *
 */
public interface Constants
{
    //
    // Class variables:
    //
    public static final Color         CYNCH_GRAY             = Color.decode("0xD4D0C8"); //lightGray;//.darker();
    // button commands:
    //  used by buttons:
    public static final String        CMD_UPDATE             = "upd";
    public static final String        CMD_RUN                = "run";
    public static final String        CMD_EXIT               = "xit";
    public static final String        CMD_SELECT_OPTIONAL    = "sel";
    //  used by dlg:
    public static final String        CMD_CANCEL             = "can";

    // ini file settings:
    public static final String        INI_APP_DESC           = "AppDescription";
    public static final String        INI_APP_SHORT_NAME     = "AppShortName";
    public static final String        INI_LOCAL_BASE         = "LocalBase";
    public static final String        INI_LOCAL_MANIFEST     = "LocalManifestName";
    public static final String        INI_REMOTE_BASE        = "RemoteBase";
    public static final String        INI_REMOTE_MANIFEST    = "RemoteManifestName";
    public static final String        INI_CLASSPATH          = "ClassPath";
    public static final String        INI_MAIN_CLASS         = "MainClass";
    public static final String        INI_ARGS               = "Args";
    public static final String        INI_UPDATE_DESC        = "UpdateDesc";
    public static final String        INI_EXEC_ARGS          = "ExecArgs";
    public static final String        INI_EXEC_NAME          = "ExecName";
    public static final String        INI_EXEC_TYPE          = "ExecType";
    public static final String        INI_UPD_FRAME_TITLE    = "AppUpdFrameTitle";
    public static final String        INI_INI_NAME           = "ININame";
    public static final String        INI_START_EXEC         = "StartExec";
    public static final String        INI_START_EXEC_DL      = "StartExecDL";
    public static final String        INI_CYNCH_DIR          = "CynchDir";
    public static final String        INI_ACTION_TIMEOUT     = "ActionTimeout";

    // parameter settings:
    //     public static final String PARM_USER           = "user";
    //     public static final String PARM_PASSWORD       = "pass";
    public static final String        PARM_INI               = "ini";
    //    public static final String PARM_INSTALL         = "install";

    public static final String        DEFAULT_INI_NAME       = "/cynch.ini";
    public static final int           DEFAULT_ACTION_TIMEOUT = 10;
    public static final String        SECONDS_SUFFIX         = " second(s)";

    public static final WindowAdapter CLOSING_ADAPTER        = new WindowAdapter() {
                                                                 @Override
                                                                 public void windowClosing(
                                                                         java.awt.event.WindowEvent event) {
                                                                     System.exit(0);
                                                                 }
                                                             };
    public static final String        PROPERTY_SEPARATOR     = ",";

    public static final char          TYPE_CHAR_CRIT         = '!';
    public static final char          TYPE_CHAR_REQUIRED     = '.';
    public static final char          TYPE_CHAR_OPT          = '?';
    public static final char          TYPE_CHAR_DEL          = 'x';
}
