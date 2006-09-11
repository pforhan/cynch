package com.muddyhorse.cynch.manifest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.muddyhorse.cynch.UpdateUtils;

public class RemoteManifest implements Manifest<RemoteFileInfo>
{
    //
    // Instance Variables:
    //
    private SortedMap<String, RemoteFileInfo> remotes = new TreeMap<String, RemoteFileInfo>();
    private final String         iniName;
    private final URL            base;
    private boolean              gotRmtManifest;

    //
    // Constructors:
    //
    public RemoteManifest(URL base, String iniName) {
        this(base, iniName, true);
    }

    protected RemoteManifest(URL base, String iniName, boolean autoLoad) {
        this.base = base;
        this.iniName = iniName;
        if (autoLoad) {
            load();
        } // endif
    }
    
    private void parseManifest(Map<String, String> h) {
        Set<Entry<String, String>> entries = h.entrySet();

        for (Entry<String, String> entry : entries) {
            String fileID = entry.getKey();
            String csv = entry.getValue();
            RemoteFileInfo rfi = createFileInfo(fileID);

            try {
                rfi.loadFromString(base, csv);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                rfi.setDescription("error setting up URL: " + ex.getMessage());
            } // endtry

            remotes.put(fileID, rfi);
        } // endforeach
    }

    protected RemoteFileInfo createFileInfo(String fileID) {
        return new RemoteFileInfo(fileID);
    }


    protected Map<String, String> getRawManifest() {
        Map<String, String> remoteManifest;
        try {
            String s = UpdateUtils.getStringFromURL(new URL(base, iniName));
            remoteManifest = UpdateUtils.stringToHashtable(s);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            remoteManifest = null;
        } // endif

        return remoteManifest;
    }

    public boolean isLoaded() {
        return gotRmtManifest;
    }

    //
    // Implementation of Manifest methods:
    //
    public SortedMap<String, RemoteFileInfo> getAllFileInfo() {
        return remotes;
    }

    public void load() {
        if (base != null && iniName != null) {

            Map<String, String> remoteManifest = getRawManifest();

            if (remoteManifest != null) {
                gotRmtManifest = remoteManifest.size() > 0; // if >0 entries, got rmt cfg

                // process the map:
                parseManifest(remoteManifest);
            } // endif

        } // endif        
    }

    public RemoteFileInfo remove(String fileID) {
        return remotes.remove(fileID);
    }
}
