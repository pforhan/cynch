package com.muddyhorse.cynch;

// java core imports:
import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.util.Vector;

// Common imports:
// Localized imports:

// GTCS Imports:

/** hashes from dl-id to DUOperation
  *
  */
public class DUSelectedOps implements java.awt.event.ItemListener
{
    //
    // Instance Variables:
    //
    private DUConfig cfg;
    private Vector ids = new Vector();
    private Vector listeners;

    //
    // Constructors:
    //
    public DUSelectedOps(DUConfig config) {
        cfg = config;
    }

    //
    // Data Methods:
    //
    public Vector getSelectedIDs() {
        return ids;
    }

    //
    // Utility methods:
    //
    public void addListener(Listener l) {
        if (listeners == null) {
            listeners = new Vector();
        } // endif
        listeners.addElement(l);
    }

    public void removeListener(Listener l) {
        if (listeners == null) {
            listeners = new Vector();
        } // endif
        listeners.removeElement(l);
    }

    private void fireEvent(boolean anySel) {
        if (listeners == null) {
            listeners = new Vector();
        } // endif
        int size = listeners.size();
        for (int i=0; i<size; ++i) {
            ((Listener)listeners.elementAt(i)).selectedIDsChanged(anySel);
        } // endfor
    }
    //
    // Implementation of the ItemListener interface:
    //
    public void itemStateChanged(ItemEvent e) {
        Checkbox check = (Checkbox)e.getSource();
        String id = check.getName();
//System.out.println("item name/id is "+id);
        if (check.getState()) {
            // isSelected:
            ids.addElement(id);
        } else {
            // not selected:
            ids.removeElement(id);
        } // endif
        fireEvent(ids.size()!=0);
//        put(id,(check.getState())?Boolean.TRUE:Boolean.FALSE);
    }
    
    //
    // Inner classes:
    //
    public interface Listener {
        public void selectedIDsChanged(boolean anySelected);
    }
}
