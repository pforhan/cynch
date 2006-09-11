package com.muddyhorse.cynch;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.muddyhorse.cynch.gui.StandardButtonPanel;
import com.muddyhorse.cynch.gui.UpdateTablePanel;
import com.muddyhorse.cynch.gui.SelectedOps;
import com.muddyhorse.cynch.manifest.DownloadType;

/**
 *
 */
public class Cynch implements java.lang.Runnable, java.awt.event.ActionListener, SelectedOps.Listener
{
    //
    // Instance Variables:
    //
    private Config              cfg;
    private int                 countDownValue;
    private TextField           txf;
    private volatile boolean    stopped;
    private Thread              myThread;
    private StandardButtonPanel buttonPanel;

    //
    // Constructors:
    //
    public Cynch(Config config, int countDown, TextField tf) {
        cfg = config;
        countDownValue = countDown;
        txf = tf;
    }

    //
    // View methods:
    //
    public static void showFullGUI(Config cfg) {
        Frame f = new Frame(cfg.get(Constants.INI_UPD_FRAME_TITLE));
        UpdateUtils.setMainFrame(f);
        f.addWindowListener(Constants.CLOSING_ADAPTER);

        // build gui:
        UpdateTablePanel updateTable = new UpdateTablePanel(cfg);
        SelectedOps selOps = updateTable.getSelectedOperations();
        StandardButtonPanel sbp = new StandardButtonPanel(cfg, selOps);

        // add timeout:
        Panel timeoutPanel = new Panel(new GridBagLayout());
        timeoutPanel.setBackground(Constants.CYNCH_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        Cynch cynch = addTimeoutText(cfg, timeoutPanel, gbc);

        // put it together:
        f.add(timeoutPanel, BorderLayout.NORTH);
        f.add(updateTable, BorderLayout.CENTER);
        f.add(sbp, BorderLayout.SOUTH);

        // set up listeners
        cynch.setButtonPanel(sbp);
        sbp.addActionListener(cynch);
        selOps.addListener(cynch);

        f.setLocation(200, 100);
        f.pack();
        f.setVisible(true);
    }

    public static void showTimeoutDialog(Config cfg) {
        // build dialog:
        Frame f = new Frame(cfg.get(Constants.INI_UPD_FRAME_TITLE));
        f.addWindowListener(Constants.CLOSING_ADAPTER);
        UpdateUtils.setMainFrame(f);
        f.setResizable(false);
        f.setLayout(new GridBagLayout());
        f.setBackground(Constants.CYNCH_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;

        // add label:
        String s = cfg.getAppShortName();
        Label lbl = new Label(s + " (" + cfg.getAppDescription() + ")");
        f.add(lbl, gbc);

        lbl = new Label("There are no critical or required updates available.");
        gbc.gridy = 5;
        f.add(lbl, gbc);

        Cynch cy = addTimeoutText(cfg, f, gbc);

        // add start upd button:
        Button b = new Button("Select optional updates...");
        b.setActionCommand(Constants.CMD_SELECT_OPTIONAL);
        b.addActionListener(cy);
        gbc.insets.top = 15;
        gbc.insets.bottom = 5;
        gbc.insets.left = 4;
        gbc.insets.right = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gbc.gridy = 15;
        gbc.gridx = 0;
        f.add(b, gbc);

        // the next two are intentionally out of order, so that I can do a requestFocus...
        if (cfg.isExitAllowed()) {
            // add exit button:
            b = new Button("Exit");
            b.setActionCommand(Constants.CMD_EXIT);
            b.addActionListener(cy);
            gbc.gridx = 2;
            f.add(b, gbc);
        } // endif        

        // add start app button:
        b = new Button("Run " + s);
        b.setActionCommand(Constants.CMD_RUN);
        b.addActionListener(cy);
        gbc.gridx = 1;
        f.add(b, gbc);

        f.pack();
        f.setLocation(200, 100);
        f.setVisible(true);
        b.requestFocus();
    }

    public static void showConnectErrorDialog(Config cfg) {
        // build dialog:
        Frame f = new Frame(cfg.get(Constants.INI_UPD_FRAME_TITLE));
        f.addWindowListener(Constants.CLOSING_ADAPTER);
        UpdateUtils.setMainFrame(f);
        f.setResizable(false);
        f.setLayout(new GridBagLayout());
        f.setBackground(Constants.CYNCH_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;

        // add label:
        String s = cfg.getAppShortName();
        Label lbl = new Label(s + " (" + cfg.getAppDescription() + ")");
        f.add(lbl, gbc);

        lbl = new Label("Unable to connect to update server(s).");
        gbc.gridy = 5;
        f.add(lbl, gbc);

        lbl = new Label("(Server addresses: " + cfg.get(Constants.INI_REMOTE_BASES) + ")");
        gbc.gridy = 6;
        f.add(lbl, gbc);

        Cynch cy = addTimeoutText(cfg, f, gbc);

        // the next two are intentionally out of order, so that I can do a requestFocus (with the same ref)...
        // add exit button:
        gbc.insets.top = 15;
        gbc.insets.bottom = 5;
        gbc.insets.left = 4;
        gbc.insets.right = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.gridy = 15;
        gbc.gridx = 2;
        Button b;
        if (cfg.isExitAllowed()) {
            b = new Button("Exit");
            b.setActionCommand(Constants.CMD_EXIT);
            b.addActionListener(cy);
            f.add(b, gbc);
        } // endif

        // add start app button:
        b = new Button("Run " + s);
        b.setActionCommand(Constants.CMD_RUN);
        b.addActionListener(cy);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        f.add(b, gbc);

        f.pack();
        f.setLocation(200, 100);
        f.setVisible(true);
        b.requestFocus();
    }

    private static Cynch addTimeoutText(Config cfg, Container parent, GridBagConstraints gbc) {
        // add timeout label:
        Label lbl = new Label("The application will be started in");
        gbc.gridy = 10;
        gbc.insets.top = 5;
        gbc.gridwidth = 1;
        parent.add(lbl, gbc);

        // add timeout text:
        int actionTimeout = cfg.getActionTimeout();
        final TextField tf = new TextField(actionTimeout + Constants.SECONDS_SUFFIX);
        tf.setEditable(false);
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        parent.add(tf, gbc);

        final Cynch cy = new Cynch(cfg, actionTimeout, tf); // this allows listener access...
        if (cfg.isTimeoutAbortAllowed()) {
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cy.stop();
                    tf.removeMouseListener(this);
                }
            };
            parent.addMouseListener(mouseAdapter);
            tf.addMouseListener(mouseAdapter);
        } // endif        
        cy.start();

        return cy;
    }

    //
    // Data Methods:
    //
    public void setButtonPanel(StandardButtonPanel buttonPanel) {
        this.buttonPanel = buttonPanel;
    }

    //
    // Utility methods:
    //
    public void stop() {
        if (cfg.isTimeoutAbortAllowed()) {
            forceStop();
        } // endif        
    }

    private void forceStop() {
        stopped = true;
        if (myThread != null) {
            myThread.interrupt();
        } // endif
        txf.setText("(Timer Disabled)");
    }

    public Thread start() {
        forceStop();

        // prepare running thread (to do the countdown):
        myThread = new Thread(this);
        myThread.setName("Countdown thread");

        // give init an extra bit of time to finish up:
        try {
            Thread.sleep(500);
            stopped = false;
            myThread.start();

        } catch (InterruptedException ex) {
            //System.out.println("cynch.r: caught InterruptedException!");
            stopped = true;
        } // endtry

        return myThread;
    }

    public static void runApplicationAndExit(Config cfg) {
        UpdateUtils.startApplication(cfg);

        Frame f = UpdateUtils.getMainFrame();
        if (f != null) {
            f.setVisible(false);
            f.dispose();
        } // endif

        System.exit(0); // ? why was this commented out?
    }

    //
    // Implementation of the Runnable interface:
    //
    public void run() {
        // main countdown loop:
        while (!stopped && countDownValue > 0) {
            txf.setText(Integer.toString(countDownValue) + Constants.SECONDS_SUFFIX);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                //System.out.println("cynch.r: caught InterruptedException!");
                stopped = true;
            } // endtry
            --countDownValue;
        } // endwhile

        // only do action if countdown wasn't aborted:
        if (!stopped) {
            // pick post-countdown action:
            if (buttonPanel != null && buttonPanel.isUpdateAvailable()) {
                // we have a buttonpanel with updates, use it to update:
                buttonPanel.updateAndRun();

            } else {
                // no button panel, or no updates, just run:
                runApplicationAndExit(cfg);
            } // endif
        } // endif
    }

    //
    // Implementation of the ActionListener interface:
    //
    public void actionPerformed(ActionEvent e) {
        // any action coming in should mean we stop:
        stop();
        String ac = e.getActionCommand();

        if (Constants.CMD_EXIT.equals(ac)
                && cfg.isExitAllowed()) {
            System.exit(0);

        } else if (Constants.CMD_RUN.equals(ac)) {
            runApplicationAndExit(cfg);

        } else if (Constants.CMD_SELECT_OPTIONAL.equals(ac)) {
            UpdateUtils.getMainFrame().setVisible(false);
            showFullGUI(cfg);
        } // endif
    }

    //
    // Implementation of SelOps.Listener method:
    //
    public void selectedIDsChanged(boolean anySelected) {
        // any change to table will stop the timer:
        stop();
    }

    //
    // Main method:
    //
    public static void main(String args[]) {
        try {
            // get args here, eg, name of frame, etc
            String ini;
            if (args.length == 0) {
                ini = Constants.DEFAULT_INI_NAME;

            } else {
                ini = args[0];
            } // endif

            Config cfg = new Config(ini, true);
            UpdateUtils.setMainConfig(cfg);

            /* investigate here what to display --
             * Critical ops      -- display full DU GUI
             * Required ops      -- display full DU GUI w/ timeout (!not done yet!)
             * Optional ops only -- display panel with button (to bring up DU GUI) and timeout
             * no ops avail      -- start application
             */
            // calculate size/availability of types of ops...:
            long critSize = UpdateUtils.countDownloadSize(cfg, DownloadType.critical, null);
            long reqSize = UpdateUtils.countDownloadSize(cfg, DownloadType.required, null);
            long optSize = UpdateUtils.countDownloadSize(cfg, DownloadType.optional, null);
            /*
             java.awt.Frame fr = new java.awt.Frame("Quick, Dirty test!");
             java.awt.event.WindowAdapter winA = new java.awt.event.WindowAdapter() { public void windowClosing(java.awt.event.WindowEvent event) { System.exit(0); } };
             fr.addWindowListener(winA);
             fr.setLayout(new java.awt.FlowLayout());
             //*/
            if (critSize > 0 || reqSize > 0) {
                // critical or required ops available; force interaction
                /*
                 fr.add(new java.awt.Label("trying to show full GUI"));
                 fr.pack();
                 fr.setVisible(true);
                 //*/
                showFullGUI(cfg);

            } else if (optSize > 0) {
                // optional ops available; show dialog with timeout...
                /*
                 fr.add(new java.awt.Label("trying to show Timeout Dlg"));
                 fr.pack();
                 fr.setVisible(true);
                 //*/
                showTimeoutDialog(cfg);

            } else if (!cfg.gotRemoteManifest()) {
                // must have been an error in connecting...
                /*
                 fr.add(new java.awt.Label("couldn't connect to upd server..."));
                 fr.pack();
                 fr.setVisible(true);
                 //*/
                showConnectErrorDialog(cfg);

            } else {
                /*
                 fr.add(new java.awt.Label("trying to run application"));
                 fr.pack();
                 fr.setVisible(true);
                 //*/
                runApplicationAndExit(cfg);
            } // endif

        } catch (Throwable t) {
            // System.err.println(t);
            t.printStackTrace();
            //Ensure the application exits with an error condition.
            System.exit(1);
        } // endtry
    }
}
