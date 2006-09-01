package com.muddyhorse.cynch;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Use the following format for the tag:
 <APPLET code="com.muddyhorse.cynch.CynchBootstrap"
 codebase="cynch/"
 alt="If you enable Java support, this applet will install and run an application"
 name="CynchBootstrap"
 width=400
 height=120
 vspace=5
 hspace=5>
 <PARAM name="ini" value="http://example.com/cynch/cynch-example.ini">
 If you view this page with a browser capable of running Java, it would give you the ability to start and run an application.
 </applet>
 */
//! part of this install process could take down the user ID! (for logging purposes...)
public class CynchBootstrap extends java.applet.Applet implements java.awt.event.ActionListener
{
    /**
     * 
     */
    private static final long     serialVersionUID = 1L;
    //
    // Class variables:
    //
    /*
     private static final String[] roots = {
     "C:/Program Files/Netscape/Communicator/Program/java/classes/",
     "C:/WINDOWS/Java/TrustLib/"
     };
     //*/
    private static final String[] names            = {
    };   // note that Boostrap classes are not included (not needed)...

    private static boolean        stopOps;

    //
    // Instance variables:
    //
    private Map<String, String>   cfg;
    private TextArea              status;

    //
    // Display Methods:
    //
    public void setStatus(String statusStr) {
        status.setText(statusStr);
        showStatus(statusStr);
    }

    //
    // Utility methods:
    //
    public static void setStatus(CynchBootstrap ui, String status) {
        if (ui != null) {
            ui.setStatus(status);
        } // endif
    }

    public static void downloadFiles(CynchBootstrap ui, String base, String duSubDir, boolean createDirsAsNeeded,
            boolean overwriteExisting) throws SecurityException {
        for (String element : names) {
            if (stopOps) {
                return;
            }
            if (createDirsAsNeeded || (new File(base)).exists()) {
                File f = new File(base + duSubDir + element);
                try {
                    if (overwriteExisting || !f.exists()) {
                        // if allowed to overwrite, or the file doesn't exist, do it:
                        setStatus(ui, "Checking Path...");
                        UpdateUtils.insurePathToFileExists(f);
                        setStatus(ui, "Downloading file " + f.toString());
                        int size = UpdateUtils.getFileFromClasspath(CynchBootstrap.class, "/" + element, f);
                        if (size == -1) {
                            System.out.println("ui.m: Error downloading from CP! Name: " + element);
                        } // endif -- download OK
                    } // endif -- file exists
                } catch (FileNotFoundException ex) {
                    System.out.println(ex);
                } // endtry
            } // endif -- base exists
        } // endfor -- each name in names
    }

    public static void installAndRun(CynchBootstrap ui, Map<String, String> cfg) throws SecurityException {
        String base = cfg.get(Constants.INI_LOCAL_BASE);

        String dudir = cfg.get(Constants.INI_DU_DIR);

        // only install to app directory (since this could have been run from an applet)
        boolean overwrite = false; //!could get setting this from config file...
        downloadFiles(ui, base, dudir, true, overwrite);

        if (stopOps) {
            return;
        }

        // the following (INI, startApp) are split out for two reasons:
        //  - these only belong in the app dir
        //  - these come out of the data file (and can be differently named), not the constant list above...

        // save ini file here:
        String iname = base + cfg.get(Constants.INI_INI_NAME);
        File f = new File(iname);
        setStatus(ui, "Downloading file " + iname);
        if (!f.exists()) {
            int byteCount = UpdateUtils.getFileFromClasspath(CynchBootstrap.class,cfg.get(Constants.PARM_INI), f);
            // ...if f did not exist before, save it now...
            if (byteCount == -1) {
                // error occurred; try default INI name:
                UpdateUtils.getFileFromClasspath(CynchBootstrap.class,Constants.DEFAULT_INI_NAME, f);
            } // endif
        }

        if (stopOps) {
            return;
        }
        // save the starter file here:
        String starter = cfg.get(Constants.INI_START_EXEC_DL);
        String sname = base + starter;
        f = new File(sname);
        setStatus(ui, "Downloading file " + sname);
        if (!f.exists()) {
            UpdateUtils.getFileFromClasspath(CynchBootstrap.class, "/" + starter, f);
            // ...if f did not exist before, save it now...
        }

        //! perhaps install a shortcut here...
        //  but note that running this installer a second time
        //  will simply start things...
        if (stopOps) {
            return;
        }

        // start the DU proper...
        //        DynamicUpdate.main(new String[] {iname}); // updates (to the DU classes) will not apply...
        String startExec = cfg.get(Constants.INI_START_EXEC);
        UpdateUtils.startApplication(startExec + " " + sname, iname, base);

        /*
         java.awt.Frame fr = new java.awt.Frame("Quick, Dirty test!");
         java.awt.event.WindowAdapter winA = new java.awt.event.WindowAdapter() { public void windowClosing(java.awt.event.WindowEvent event) { System.exit(0); } };
         fr.addWindowListener(winA);
         fr.setLayout(new java.awt.FlowLayout());
         fr.add(new java.awt.Label("starter = "+starter));
         fr.add(new java.awt.Label("startExec = "+startExec));
         fr.add(new java.awt.Label("iname = "+iname));
         fr.add(new java.awt.Label("base = "+base));
         fr.add(new java.awt.Label("cmd line = "+startExec+" "+sname+" "+base+" "+iname));
         fr.add(new java.awt.Label("class-path = "+System.getProperty("java.class.path")));
         fr.pack();
         fr.setVisible(true);
         //*/
    }

    //
    // Applet Override methods:
    //
    @Override
    public String getAppletInfo() {
        return "This applet installs and runs the DynamicUpdate utility according to a configuration file.";
    }

    @Override
    public String[][] getParameterInfo() {
        String[][] s = {
            {
                Constants.PARM_INI, "URL", "Initialization and settings file"
            }
        };
        /*
         ,
         {PARM_USER     ,"String","TCS Username
         {PARM_PASSWORD ,"String","
         //*/
        return s;
    }

    @Override
    public void init() {
        // obtain parameters here...
        Map<String, String> parms = new HashMap<String, String>();
        //System.out.println("Getting parameter "+PARM_INI);
        parms.put(Constants.PARM_INI, getParameter(Constants.PARM_INI));
        //System.out.println("parameter "+PARM_INI+" is "+getParameter(PARM_INI));
//         parms.put(PARM_USER,     getParameter(PARM_USER     ));
//         parms.put(PARM_PASSWORD, getParameter(PARM_PASSWORD ));
        //System.out.println("Setting parameters");
//        UpdateUtils.setParameters(parms);

        // GUI Layout:
        //System.out.println("GUI 1");
        setLayout(new BorderLayout());
        status = new TextArea("Initializing...", 50, 3, TextArea.SCROLLBARS_VERTICAL_ONLY);
        status.setEditable(false);
        add(status, BorderLayout.CENTER);

        // get app INI:
        //System.out.println("Getting cfg");
        try {
            setStatus("Obtaining configuration...");
            cfg = UpdateUtils.stringToHashtable(UpdateUtils.getStringFromURL(parms.get(Constants.PARM_INI)));
            //System.out.println("got cfg success! is "+cfg);
        } catch (Exception ex) {
            setStatus("Error obtaining configuration:\n" + ex.toString());
            ex.printStackTrace();
            //System.out.println("got cfg fail!");
            return;
        } // endtry

        // GUI Layout, cont'd:
        //System.out.println("GUI 2");
        Button btn = new Button("Start " + cfg.get(Constants.INI_APP_SHORT_NAME));
        //        btn.setEnabled(false);
        btn.setActionCommand(Constants.CMD_RUN);
        btn.addActionListener(this);
        add(btn, BorderLayout.NORTH);
    }

    @Override
    public void start() {
        if (cfg != null) {
            setStatus("Please press the button to launch the application");
            // if local, do install call
            // if not local, ask to download and dbl-click
            //  ? or just dl to certain dir.
        }
    }

    @Override
    public void stop() {
        // pause operations here...
        stopOps = true;
    }

    @Override
    public void destroy() {
        // eliminate any resources here...
    }

    //
    // Implementation of the ActionListener interface:
    //
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if (Constants.CMD_RUN.equals(ac)) {
            try {
                stopOps = false;
                installAndRun(this, cfg);
            } catch (Exception ex) {
                // assume SecurityEx or some other problem...
                setStatus("Encountered problem:\n" + ex.toString()
                        + "\n\nPlease insure that all components are installed properly.");
            } // endtry
        } // endif
    }

    //
    // Testing/main methods:
    //
    public static void main(String[] args) {
        String iniName = Constants.DEFAULT_INI_NAME;
//        Map<String, String> parms = new HashMap<String, String>();
//        parms.put(PARM_INI, iniName);
//        UpdateUtils.setParameters(parms);

        Map<String, String> cfg = UpdateUtils.stringToHashtable(UpdateUtils.getStringFromClasspath(
                CynchBootstrap.class, iniName));

        /*
         // do the install into the browser directories...
         for (int j=0; j<roots.length; ++j) {
         downloadFiles(null,roots[j],"",false);
         // don't want an extra subdirectory...
         // don't create these dirs if they don't already exist...
         } // endfor -- each base in bases
         //*/

        installAndRun(null, cfg);
    }
}
