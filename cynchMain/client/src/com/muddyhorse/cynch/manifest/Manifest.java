package com.muddyhorse.cynch.manifest;

import java.util.SortedMap;

public interface Manifest<FI extends FileInfo>
{
    public SortedMap<String, FI> getAllFileInfo();
    public void load();
//    public void save();
}
