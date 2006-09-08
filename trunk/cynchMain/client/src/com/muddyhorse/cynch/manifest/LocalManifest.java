package com.muddyhorse.cynch.manifest;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.muddyhorse.cynch.UpdateUtils;

public class LocalManifest implements Manifest<LocalFileInfo>
{
    //
    // Instance Variables:
    //
    private SortedMap<String, LocalFileInfo> locals = new TreeMap<String, LocalFileInfo>();
    private final String iniName;
    private final File base;

    //
    // Constructors:
    //
    public LocalManifest(File base, String iniName) {
        this.base = base;
        this.iniName = iniName;
        load();
    }

    //
    //  methods:
    //
    private void parseManifest(Map<String, String> h) {
        Set<Entry<String, String>> entries = h.entrySet();

        for (Entry<String, String> entry : entries) {
            String fileID = entry.getKey();
            String csv    = entry.getValue();
            LocalFileInfo lfi = new LocalFileInfo(fileID, csv);
            locals.put(fileID, lfi);
        } // endforeach
    }

    public void removeNonExistantFiles() {
        Iterator<LocalFileInfo> itor = locals.values().iterator();
        while (itor.hasNext()) {
            LocalFileInfo lfi = itor.next();
            
            if (!lfi.getPath().exists()) {
                System.out.println("LocalManifest.rNEF:  removing non-existant file from manifest:" + lfi.getPath());
                itor.remove();
            } // endif
        } // endwhile
    }

    public void save() {
        File saveFile = new File(base, iniName);
        // convert from manifest to string-string map:
        Map<String, String> output = new TreeMap<String, String>();
        for (LocalFileInfo lfi : locals.values()) {
            output.put(lfi.getFileID(), lfi.toString());
        } // endforeach

        // write to file:
        UpdateUtils.writeHashtable(saveFile.getPath(),output);
    }

    public SortedMap<String, LocalFileInfo> getAllFileInfo() {
        return locals;
    }

    public void load() {
        if (base != null
                && iniName != null) {
            String s = UpdateUtils.getStringFromFile(base.getPath() + iniName);
            if (s == null || s.equals("")) {
                // try again, just iniName:
                s = UpdateUtils.getStringFromFile(iniName);

                if (s == null || s.equals("")) {
                    // try again, from classpath:
                    s = UpdateUtils.getStringFromClasspath(getClass(), iniName);
                } // endif
            } // endif

            //        ini = DynamicUpdateUtils.stringToHashtable(s);
            Map<String, String> h = UpdateUtils.stringToHashtable(s);
            parseManifest(h);
        } // endif        
    }
}
