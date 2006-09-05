package com.muddyhorse.cynch.admin;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Admin for cynch manifest file.
 * Required functionality:
 *  - create, edit, update manifest
 *  - ability to change any manifest field at will
 *  - auto-assign versions
 * Bonus (nice-to-have) functionality:
 *  - import/update older manifest formats
 *  - allow version numbers from date/time (perhaps different "versioning" mode).
 *  - export installer jar
 *  - upload managed files to server?
 * @author paf2009
 *
 */
public class CynchAdmin
{
    

    private static AdminTableModel model;

    private static JMenuBar getMenuBar() {
        // TODO Actually implement getMenuBar
        System.out.println("reached getMenuBar within CynchAdmin");
        return null;
    }

    public static void main(String[] args) {
        JFrame jfr = new JFrame("Cynch admin");
        JMenuBar menu = getMenuBar();
        jfr.setJMenuBar(menu);
        
        model = new AdminTableModel();
        jfr.getContentPane().add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
    }

}
