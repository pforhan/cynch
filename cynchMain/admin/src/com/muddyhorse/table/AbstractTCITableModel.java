package com.muddyhorse.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public abstract class AbstractTCITableModel<E> extends AbstractTableModel
{
    //
    // Instance Variables:
    //
    private List<E> data = new ArrayList<E>();
    private TableColumnInfo<E>[] columns;
    private TableModelContext<E> context = new TableModelContext<E>();

    //
    // Constructors:
    //
    public AbstractTCITableModel(TableColumnInfo<E>[] columns) {
        this.columns = columns;
        
    }

    //
    // Data methods:
    //
    public TableColumnInfo<E>[] getColumns() {
        return columns;
    }

    public void setColumns(TableColumnInfo<E>[] columns) {
        this.columns = columns;
        fireTableStructureChanged();
    }

    protected List<E> getData() {
        return data;
    }

    protected void setData(List<E> data) {
        this.data = data;
    }

    //
    // TableModel methods:
    //
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column].getColumnName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns[columnIndex].getColumnClass();
    }

    public int getRowCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        } // endif
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data != null) {
            E rfi = data.get(rowIndex);
    
            return columns[columnIndex].getData(rfi, context);
    
        } else {
            return null;
        } // endif
    }
}
