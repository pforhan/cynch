package com.muddyhorse.cynch.admin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;


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
 *  - ant target or at least command-line version to make changes without gui
 *
 * Fields present:
 *  - ini path / prep basedir
 *  - ini name
 *  - table of data
 *    - lists basic info
 *    - colored by things updated / needing updating
 *  - save / reload
 *  - auto-update
 *
 * @author pforhan
 */
public class CynchAdmin
{
    static AdminTableModel model;
    static File maniFile;

    private static JMenuBar getMenuBar() {
        JMenuBar rv = new JMenuBar();
        final JMenu fileMenu = new JMenu("File");
        Action a = new AbstractAction("Open") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File("C:\\pforhan\\java\\svn\\cynchProjects\\cynchMain\\client\\resources\\"));
                int rc = jfc.showOpenDialog(fileMenu);
                if (rc == JFileChooser.APPROVE_OPTION) {
                    maniFile = jfc.getSelectedFile();
                    model.setManifest(new AdminManifest(maniFile));
                } // endif
            }
        };
        fileMenu.add(a);
        a = new AbstractAction("Save") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                AdminManifest manifest = model.getManifest();
                if (manifest != null) {
                    manifest.save(maniFile);
                } // endif
            }
        };
        fileMenu.add(a);
        rv.add(fileMenu);

        return rv;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            JFrame jfr = new JFrame("Cynch admin");
            jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JMenuBar menu = getMenuBar();
            jfr.setJMenuBar(menu);
            model = new AdminTableModel();
            jfr.getContentPane().add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
            jfr.pack();
            jfr.setVisible(true);

        } else if (args.length == 2) {
            try {
                if ("create".equals(args[0])) {
                    AdminUtils.createManifest(args[1]);

                } else if ("update".equals(args[0])) {
                    AdminUtils.updateManifest(args[1]);
                } // endif

            } catch (IOException e) {
                e.printStackTrace();
            } // endtry
        } // endif
    }
    
}
