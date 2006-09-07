package com.muddyhorse.cynch.manifest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.muddyhorse.cynch.UpdateUtils;

public class RemoteManifest implements Manifest<RemoteFileInfo>
{
    //
    // Instance Variables:
    //
    private List<RemoteFileInfo> remotes = new ArrayList<RemoteFileInfo>();
    private final String         iniName;
    private final URL            base;
    private boolean              gotRmtManifest;

    //
    // Constructors:
    //
    public RemoteManifest(URL base, String iniName) {
        this.base = base;
        this.iniName = iniName;
        load();
    }

    private void parseManifest(Map<String, String> h) {
        Set<Entry<String, String>> entries = h.entrySet();

        for (Entry<String, String> entry : entries) {
            String fileID = entry.getKey();
            String csv = entry.getValue();
            RemoteFileInfo rfi = new RemoteFileInfo(fileID);

            try {
                rfi.loadFromString(base, csv);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                rfi.setDescription("error setting up URL: " + ex.getMessage());
            } // endtry

            remotes.add(rfi);
        } // endforeach
    }

    public List<RemoteFileInfo> getAllFileInfo() {
        return remotes;
    }

    public boolean isLoaded() {
        return gotRmtManifest;
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
}
