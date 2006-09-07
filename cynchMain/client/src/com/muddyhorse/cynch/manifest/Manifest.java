package com.muddyhorse.cynch.manifest;

import java.util.List;

public interface Manifest<FI extends FileInfo>
{
    public List<FI> getAllFileInfo();
    public void load();
//    public void save();
}
