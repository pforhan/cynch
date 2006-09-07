package com.muddyhorse.cynch.admin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.muddyhorse.cynch.Config;
import com.muddyhorse.cynch.manifest.Operation;

public class AdminTableModel extends AbstractTableModel
{
    //
    // Class Constants:
    //
    private static final long serialVersionUID = 1L;

    //
    // Instance Variables:
    //
    private Config config;
    private List<Operation> ops = new ArrayList<Operation>();
    private AdminColumns[] columns;

    //
    // Constructors:
    //
    public AdminTableModel() {
        // no-arg constructor
    }

    public AdminTableModel(Config config) {
        setConfig(config);
    }

    //
    // Data methods:
    //
    public Config getConfig() {
        return config;
    }
    
    public void setConfig(Config config) {
        this.config = config;
        ops.clear();
        ops.addAll(config.getOperations().values());
        fireTableDataChanged();
    }

    public AdminColumns[] getColumns() {
        return columns;
    }
    
    public void setColumns(AdminColumns[] columns) {
        this.columns = columns;
        fireTableStructureChanged();
    }

    //
    // Implementation of TableModel methods:
    //
    public int getColumnCount() {
        return AdminColumns.values().length;
    }
    
    @Override
    public String getColumnName(int column) {
        return AdminColumns.values()[column].getDescription();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return AdminColumns.values()[columnIndex].getFieldClass();
    }

    public int getRowCount() {
        if (config != null) {
            return config.getOperations().size();
        } else {
            return 0;
        } // endif
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (config != null) {
            Operation op = ops.get(rowIndex);

            return "something";

        } else {
            return null;
        } // endif
    }
}
