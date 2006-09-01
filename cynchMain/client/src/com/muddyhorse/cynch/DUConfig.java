package com.muddyhorse.cynch;

// java core imports:
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

// Common imports:
// Localized imports:

// GTCS Imports:

/**
  *
  */
public class DUConfig implements DUConstants
{
    //
    // Instance Variables:
    //
    private String    iniName;
    private Hashtable ini;
    private Hashtable operations;
    private Hashtable localFiles;
    private boolean   gotRmtCfg;

    //
    // Constructors:
    //
    public DUConfig(String iniName) {
        this.iniName = iniName;
        reloadINI();
        reloadOperations();
    }

    //
    // Data Methods:
    //

    public String get(String parm) {
        return (String)ini.get(parm);
    }

    public String getCommand() {
        return (String)ini.get(INI_MAIN_CLASS);
    }

    public String getClasspath() {
        return (String)ini.get(INI_CLASSPATH);
    }

    public String getArgs() {
        return (String)ini.get(INI_ARGS);
    }

    public String getUpdateDescription() {
        return (String)ini.get(INI_UPDATE_DESC);
    }

    public String getAppDescription() {
        return (String)ini.get(INI_APP_DESC);
    }

    public String getAppShortName() {
        return (String)ini.get(INI_APP_SHORT_NAME);
    }
    
    public String getRemoteConfigName() {
        return (String)ini.get(INI_REMOTE_BASE) + (String)ini.get(INI_REMOTE_CORE);
    }
    
    public String getLocalConfigName() {
        return (String)ini.get(INI_LOCAL_BASE) + (String)ini.get(INI_LOCAL_CORE);
    }

    public Hashtable getOperations() {
        return operations;
    }

    public Hashtable getLocalFiles() {
        return localFiles;
    }
    
    public boolean gotRemoteConfig() { // used by initial GUI to determine if connection success
        return gotRmtCfg;
    }

    //
    // Utility methods:
    //
    public void reloadINI() {
        String s = DynamicUpdateUtils.getStringFromFile(iniName);
        if (s==null
         || s.equals("")) {
            // try again, from classpath:
            s = DynamicUpdateUtils.getStringFromClasspath(getClass(),iniName);
        } // endif

//        ini = DynamicUpdateUtils.stringToHashtable(s);
        Hashtable h = DynamicUpdateUtils.stringToHashtable(s);

        // extract information to try to find the remote INI:
        String iname = (String)h.get(DUConstants.INI_INI_NAME);
        String rname = (String)h.get(DUConstants.INI_REMOTE_BASE) + iname;
         // this should be in format remote-root/ini-name
         // note that remote-root should be terminated by a "/"

        URL yuri;
        try {
            yuri = new URL(rname);
            s = DynamicUpdateUtils.getStringFromURL(yuri);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            s = null;
            yuri = null;
        } // endtry

        if (s!=null
         &&!s.equals("")) {
            // if successful d/l, use this data instead:
            ini = DynamicUpdateUtils.stringToHashtable(s);
            // also, force the ini to be saved:
            // note that I could just use getFileFromURL to do this
            //  work.  That would mean a second download however, so
            //  I'll use the data we have.
            String lname = (String)h.get(DUConstants.INI_LOCAL_BASE) + iname;
            File f = new File(lname);
            DynamicUpdateUtils.getFileFromString(s, f);
//            System.out.println("S is:\n"+s);
//            System.exit(10);
        } else {
            // d/l not successful, it appears.  Use local data
            ini = h;
        } // endif
    }

    public void reloadOperations() {
        try {
            localFiles     = DynamicUpdateUtils.stringToHashtable(DynamicUpdateUtils.getStringFromFile(getLocalConfigName()));
            DynamicUpdateUtils.removeNonExistantFiles(localFiles, get(INI_LOCAL_BASE));
            
            String s = DynamicUpdateUtils.getStringFromURL(getRemoteConfigName());
//            gotRmtCfg = !("".equals(s)); // if not "", the we got rmt
            Hashtable remoteFiles    = DynamicUpdateUtils.stringToHashtable(s);
            gotRmtCfg = remoteFiles.size() > 0; // if >0 entries, got rmt cfg
//! process remote files here, for @includes or something!
//! possibly add another parameter for each line detailing something like download directory (if different from rmt)
            operations = (localFiles == null || remoteFiles == null) ? null :
            	DynamicUpdateUtils.compareHashtables(localFiles, remoteFiles);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
