package com.muddyhorse.cynch.admin;


import java.util.List;

import com.muddyhorse.cynch.manifest.RemoteFileInfo;
import com.muddyhorse.table.AbstractTCITableModel;
import com.muddyhorse.table.TableColumnInfo;

public class AdminTableModel extends AbstractTCITableModel<RemoteFileInfo>
{
    //
    // Class Constants:
    //
    private static final long serialVersionUID = 1L;

    //
    // Instance Variables:
    //
    private AdminManifest manifest;

    //
    // Constructors:
    //
    public AdminTableModel() {
        this(AdminColumns.values());
    }

    public AdminTableModel(TableColumnInfo<RemoteFileInfo>[] columns) {
        super(columns);
    }
    
    //
    // Data methods:
    //
    public AdminManifest getManifest() {
        return manifest;
    }
    
    public void setManifest(AdminManifest config) {
        this.manifest = config;
        List<RemoteFileInfo> d = getData();
        d.addAll(config.getAllFileInfo().values());
        fireTableDataChanged();
    }
}
