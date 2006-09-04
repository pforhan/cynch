package com.muddyhorse.cynch.gui;

import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

/** hashes from dl-id to DUOperation
 *
 */
public class SelectedOps implements java.awt.event.ItemListener
{
    //
    // Instance Variables:
    //
//    private Config cfg;
    private List<String> ids = new ArrayList<String>();
    private List<Listener> listeners;

    //
    // Constructors:
    //
    public SelectedOps() {
//        cfg = config;
    }

    //
    // Data Methods:
    //
    public List<String> getSelectedIDs() {
        return ids;
    }

    //
    // Utility methods:
    //
    public void addListener(Listener l) {
        if (listeners == null) {
            listeners = new ArrayList<Listener>();
        } // endif

        listeners.add(l);
    }

    public void removeListener(Listener l) {
        if (listeners == null) {
            listeners = new ArrayList<Listener>();

        } else {
            listeners.remove(l);
        } // endif
    }

    private void fireEvent(boolean anySel) {
        if (listeners != null) {
            for (Listener list : listeners) {
                list.selectedIDsChanged(anySel);
            } // endforeach
        } // endif
    }

    //
    // Implementation of the ItemListener interface:
    //
    public void itemStateChanged(ItemEvent e) {
        Checkbox check = (Checkbox) e.getSource();
        String id = check.getName();
        //System.out.println("item name/id is "+id);
        if (check.getState()) {
            // isSelected:
            ids.add(id);
        } else {
            // not selected:
            ids.remove(id);
        } // endif
        fireEvent(ids.size() != 0);
        //        put(id,(check.getState())?Boolean.TRUE:Boolean.FALSE);
    }

    //
    // Inner classes:
    //
    public interface Listener
    {
        public void selectedIDsChanged(boolean anySelected);
    }
}
