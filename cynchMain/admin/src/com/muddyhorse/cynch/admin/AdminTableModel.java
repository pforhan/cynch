package com.muddyhorse.cynch.admin;

import javax.swing.table.AbstractTableModel;

public class AdminTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    public int getColumnCount() {
        // TODO Actually implement getColumnCount
        System.out.println("reached getColumnCount within AdminTableModel");
        return 0;
    }

    public int getRowCount() {
        // TODO Actually implement getRowCount
        System.out.println("reached getRowCount within AdminTableModel");
        return 0;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        // TODO Actually implement getValueAt
        System.out.println("reached getValueAt within AdminTableModel");
        return null;
    }

}
