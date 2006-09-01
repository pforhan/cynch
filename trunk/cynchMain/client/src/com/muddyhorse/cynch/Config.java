package com.muddyhorse.cynch;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class Config
{
    //
    // Instance Variables:
    //
    private String    iniName;
    private Map<String, String> ini;
    private Set<Operation> operations;
    private Map<String, String> localManifest;
    private boolean   gotRmtCfg;

    //
    // Constructors:
    //
    public Config(String iniName) {
        this.iniName = iniName;
        reloadINI();
        reloadOperations();
    }

    //
    // Data Methods:
    //

    public String get(String parm) {
        return ini.get(parm);
    }

    public String getCommand() {
        return ini.get(Constants.INI_MAIN_CLASS);
    }

    public String getClasspath() {
        return ini.get(Constants.INI_CLASSPATH);
    }

    public String getArgs() {
        return ini.get(Constants.INI_ARGS);
    }

    public String getUpdateDescription() {
        return ini.get(Constants.INI_UPDATE_DESC);
    }

    public String getAppDescription() {
        return ini.get(Constants.INI_APP_DESC);
    }

    public String getAppShortName() {
        return ini.get(Constants.INI_APP_SHORT_NAME);
    }

    public String getRemoteConfigName() {
        return ini.get(Constants.INI_REMOTE_BASE) + ini.get(Constants.INI_REMOTE_CORE);
    }

    public String getLocalConfigName() {
        return ini.get(Constants.INI_LOCAL_BASE) + ini.get(Constants.INI_LOCAL_CORE);
    }

    public Set<Operation> getOperations() {
        return operations;
    }

    public Map<String, String> getLocalFiles() {
        return localManifest;
    }

    public boolean gotRemoteConfig() { // used by initial GUI to determine if connection success
        return gotRmtCfg;
    }

    //
    // Utility methods:
    //
    
    /**
     * Read in the INI from disk or classpath.  Use that INI to attempt to connect to remote INI.
     * If remote INI is found, write it to local and use its values.  If not found, use local INI values.
     */
    public void reloadINI() {
        String s = UpdateUtils.getStringFromFile(iniName);
        if (s == null || s.equals("")) {
            // try again, from classpath:
            s = UpdateUtils.getStringFromClasspath(getClass(), iniName);
        } // endif

        //        ini = DynamicUpdateUtils.stringToHashtable(s);
        Map<String, String> h = UpdateUtils.stringToHashtable(s);

        // extract information to try to find the remote INI:
        String iname = h.get(Constants.INI_INI_NAME);
        String rname = h.get(Constants.INI_REMOTE_BASE) + iname;
        // this should be in format remote-root/ini-name
        // note that remote-root should be terminated by a "/"

        URL yuri;
        try {
            yuri = new URL(rname);
            s = UpdateUtils.getStringFromURL(yuri);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            s = null;
            yuri = null;
        } // endtry

        if (s != null && !s.equals("")) {
            // if successful d/l, use this data instead:
            ini = UpdateUtils.stringToHashtable(s);
            // also, force the ini to be saved:
            // note that I could just use getFileFromURL to do this
            //  work.  That would mean a second download however, so
            //  I'll use the data we have.
            String lname = h.get(Constants.INI_LOCAL_BASE) + iname;
            File f = new File(lname);
            UpdateUtils.writeStringToFile(s, f);
            //            System.out.println("S is:\n"+s);
            //            System.exit(10);
        } else {
            // d/l not successful, it appears.  Use local data:
            ini = h;
        } // endif
    }

    public void reloadOperations() {
        try {
            localManifest = UpdateUtils.stringToHashtable(UpdateUtils
                    .getStringFromFile(getLocalConfigName()));
            UpdateUtils.removeNonExistantFiles(localManifest, get(Constants.INI_LOCAL_BASE));

            String s = UpdateUtils.getStringFromURL(getRemoteConfigName());
            //            gotRmtCfg = !("".equals(s)); // if not "", the we got rmt
            Hashtable remoteFiles = UpdateUtils.stringToHashtable(s);
            gotRmtCfg = remoteFiles.size() > 0; // if >0 entries, got rmt cfg
            //! process remote files here, for @includes or something!
            //! possibly add another parameter for each line detailing something like download directory (if different from rmt)
            operations = localManifest == null || remoteFiles == null ? null : UpdateUtils.compareHashtables(
                    localManifest, remoteFiles);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
