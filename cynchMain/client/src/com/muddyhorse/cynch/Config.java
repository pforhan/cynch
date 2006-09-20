package com.muddyhorse.cynch;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.muddyhorse.cynch.manifest.LocalManifest;
import com.muddyhorse.cynch.manifest.Operation;
import com.muddyhorse.cynch.manifest.RemoteManifest;

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
    private SortedMap<String, Operation> operations = new TreeMap<String, Operation>();
    private LocalManifest localManifest;
    private RemoteManifest remoteManifest;

    //
    // Constructors:
    //
    public Config(String iniName, boolean loadRemote) {
        this.iniName = iniName;
        reloadINI();
        reloadOperations(loadRemote);
    }

    //
    // Data Methods:
    //

    public String get(String parm) {
        return ini.get(parm);
    }

    public int getActionTimeout() {
        try {
            return Integer.parseInt(ini.get(Constants.INI_ACTION_TIMEOUT));
        } catch (Exception ex) {
            // ignore exception; use default
            return Constants.DEFAULT_ACTION_TIMEOUT;
        }
    }

    public boolean isTimeoutAbortAllowed() {
        return Boolean.valueOf(ini.get(Constants.INI_ALLOW_TIMEOUT_ABORT));
    }

    public boolean isExitAllowed() {
        return Boolean.valueOf(ini.get(Constants.INI_ALLOW_EXIT));
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

    public String getRemoteManifestName() {
        return ini.get(Constants.INI_REMOTE_MANIFEST);
    }

    public String getLocalManifestName() {
        return ini.get(Constants.INI_LOCAL_MANIFEST);
    }

    public String[] getRemoteBases() {
        return ini.get(Constants.INI_REMOTE_BASES).split(Constants.PROPERTY_SEPARATOR);
    }
    
    public String getLocalBase() {
        return ini.get(Constants.INI_LOCAL_BASE);
    }
    
    public SortedMap<String, Operation> getOperations() {
        return operations;
    }

    public LocalManifest getLocalManifest() {
        return localManifest;
    }

    public RemoteManifest getRemoteManifest() {
        return remoteManifest;
    }
    
    public boolean gotRemoteManifest() {
        return remoteManifest.isLoaded();
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
        String rname = h.get(Constants.INI_REMOTE_BASES) + iname;
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

    public void reloadOperations(boolean loadRemote) {
        try {
            localManifest = new LocalManifest(new File(getLocalBase()), getLocalManifestName());
            localManifest.removeNonExistantFiles();
            String[] remoteBases = getRemoteBases();
            for (String base : remoteBases) {
                URL rmtBaseURL = new URL(base);
                remoteManifest = new RemoteManifest(rmtBaseURL, getRemoteManifestName());
                if (!remoteManifest.isLoaded()) {
                    operations.clear();

                } else {
                    // got a hit; stop checking URLs
                    operations.putAll(UpdateUtils.compareManifests(localManifest, remoteManifest));
                    break;
                } // endif
            } // endforeach            

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
