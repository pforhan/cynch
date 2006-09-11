package com.muddyhorse.cynch.gui;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import com.muddyhorse.cynch.Config;
import com.muddyhorse.cynch.Constants;
import com.muddyhorse.cynch.Cynch;
import com.muddyhorse.cynch.UpdateUtils;
import com.muddyhorse.cynch.manifest.DownloadType;

/**
 *
 */
public class StandardButtonPanel extends java.awt.Panel implements java.awt.event.ActionListener, java.lang.Runnable,
        com.muddyhorse.cynch.gui.SelectedOps.Listener

{
    /**
     * 
     */
    private static final long   serialVersionUID = 1L;
    //
    // Instance Variables:
    //
    private Config              cfg;
    private SelectedOps         selOps;
    private Button              upd;
    private ProgressDialog      progressDlg;
    private Set<ActionListener> listeners        = new HashSet<ActionListener>();

    //
    // Constructors:
    //
    public StandardButtonPanel(Config config, SelectedOps selected) {
        super(new GridBagLayout());
        cfg = config;
        selOps = selected;
        selOps.addListener(this);
        buildGUI();
    }

    //
    // View methods:
    //
    private void buildGUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = 5;
        gbc.insets.bottom = 5;
        gbc.insets.left = 5;
        gbc.insets.right = 5;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        String shortName = cfg.getAppShortName();

        upd = new Button("Update and Run " + shortName);
        upd.setActionCommand(Constants.CMD_UPDATE);
        upd.addActionListener(this);
        upd.setEnabled(getUpdateState(false)); // first time through, no optionals are sel'd
        add(upd, gbc);

        Button b = new Button("Run " + shortName + " without Updates");
        b.setActionCommand(Constants.CMD_RUN);
        b.setEnabled(getRunState());
        b.addActionListener(this);
        gbc.gridx = 1;
        add(b, gbc);

        b = new Button("Exit");
        b.setActionCommand(Constants.CMD_EXIT);
        b.addActionListener(this);
        gbc.gridx = 2;
        add(b, gbc);

        setBackground(Constants.CYNCH_GRAY);
    }

    //
    // Data Methods:
    //

    //
    // Utility methods:
    //
    public boolean isUpdateAvailable() {
        return getUpdateState(false);
    }

    private boolean getUpdateState(boolean anyOptional) {
        // if there are any downloads available and selected, allow run (with updates)
        long ttl = UpdateUtils.countDownloadSize(cfg, DownloadType.critical, null)
                + UpdateUtils.countDownloadSize(cfg, DownloadType.required, null)
                + UpdateUtils.countDownloadSize(cfg, DownloadType.all, selOps.getSelectedIDs());
        return anyOptional || ttl > 0;
    }

    private boolean getRunState() {
        // if there are no critical operations, allow run(no updates)
        return UpdateUtils.countDownloadSize(cfg, DownloadType.critical, null) == 0;
    }

    private void runApplication() {
        if (progressDlg != null) {
            progressDlg.setVisible(false);
            progressDlg.dispose();
        } // endif
        Cynch.runApplicationAndExit(cfg);
    }

    //
    // Overrides:
    //

    //
    // Implementation of the Runnable interface:
    //
    public void run() {
        try {
            Thread.sleep(150);
            int errTot;
            errTot = UpdateUtils.performAllOperations(cfg, DownloadType.critical, null, progressDlg);
            System.out.println("sbp.r: error total (crit) was: " + errTot);
            errTot += UpdateUtils.performAllOperations(cfg, DownloadType.required, null, progressDlg);
            System.out.println("sbp.r: error total (+req) was: " + errTot);
            // do optional:
            // do "ALL" types because deletes are always optional.
            errTot += UpdateUtils.performAllOperations(cfg, DownloadType.all, selOps.getSelectedIDs(), progressDlg);
            System.out.println("sbp.r: error total (+opt) was: " + errTot);

            cfg.getLocalManifest().save();

            // need to check errTot here to see if we should proceed...
            runApplication();
        } catch (InterruptedException ex) {
            System.out.println("sbp.r: caught InterruptedException... @" + System.currentTimeMillis());
            progressDlg.setVisible(false);
            progressDlg.dispose();

            // obtain correct info for next download attempt:
            cfg.reloadINI();
            cfg.reloadOperations(true);
        } // endtry
    }

    //
    // Implementation of the DUSelectedOps.Listener interface:
    //
    public void selectedIDsChanged(boolean anySelected) {
        upd.setEnabled(getUpdateState(anySelected));
    }

    //
    // Action source methods:
    //
    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    public void removeActionListener(ActionListener l) {
        listeners.remove(l);
    }
    
    private void fireActionEvent(String cmd) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, cmd);
        for (ActionListener l : listeners) {
            l.actionPerformed(e);
        } // endforeach
    }

    //
    // Implementation of the ActionListener interface:
    //
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if (Constants.CMD_EXIT.equals(ac)) {
            System.exit(0);
        } else if (Constants.CMD_RUN.equals(ac)) {
            runApplication();
        } else if (Constants.CMD_UPDATE.equals(ac)) {
            updateAndRun();
        } // endif
    }

    public void updateAndRun() {
        // notify listeners:
        fireActionEvent(Constants.CMD_UPDATE);

        // do required/critical:
        Frame f = UpdateUtils.getMainFrame();

        long ttlSize = UpdateUtils.countDownloadSize(cfg, DownloadType.critical, null);
        ttlSize += UpdateUtils.countDownloadSize(cfg, DownloadType.required, null);
        ttlSize += UpdateUtils.countDownloadSize(cfg, DownloadType.all, selOps.getSelectedIDs());
        progressDlg = new ProgressDialog(f, ttlSize);
        progressDlg.setLocationRelativeTo(f);

        Thread t = new Thread(this);
        t.setName("Operations thread");
        t.start();
        progressDlg.setVisible(true);
    }
}
