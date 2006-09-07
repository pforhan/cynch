package com.muddyhorse.table;

public interface TableColumnInfo<E>
{
    public Class<?> getColumnClass();
    public Object   getData(E dataValue);
    public String   getColumnName();
}
