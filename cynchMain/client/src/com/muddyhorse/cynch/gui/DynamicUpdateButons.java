package com.muddyhorse.cynch.gui;

// java core imports:
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import com.muddyhorse.cynch.Cynch;
import com.muddyhorse.cynch.DUConfig;
import com.muddyhorse.cynch.DUConstants;
import com.muddyhorse.cynch.DUProgressDialog;
import com.muddyhorse.cynch.DUSelectedOps;
import com.muddyhorse.cynch.DynamicUpdateUtils;
import com.muddyhorse.cynch.DUSelectedOps.Listener;

// Common imports:
// Localized imports:

// GTCS Imports:

/**
  *
  */
public class DynamicUpdateButons extends java.awt.Panel
                              implements java.awt.event.ActionListener,
                                         java.lang.Runnable,
                                         com.muddyhorse.cynch.DUSelectedOps.Listener,
                                         com.muddyhorse.cynch.DUConstants
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    //
    // Instance Variables:
    //
    private DUConfig      cfg;
    private DUSelectedOps selOps;
    private Button        upd;
    private DUProgressDialog d;

    //
    // Constructors:
    //
    public DynamicUpdateButons(DUConfig config, DUSelectedOps selected) {
        super(new GridBagLayout());
        cfg    = config;
        selOps = selected;
        selOps.addListener(this);
        buildGUI();
    }

    //
    // View methods:
    //
    private void buildGUI() {
        setBackground(Color.lightGray);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill         = GridBagConstraints.BOTH;
        gbc.insets.top   = 5;
        gbc.insets.bottom= 5;
        gbc.insets.left  = 5;
        gbc.insets.right = 5;
        gbc.gridy   = 0;
        gbc.weightx = 0.5;
        gbc.gridx   = 0;
        String s = cfg.getAppShortName();

        upd = new Button("Update and Run "+s);
        upd.setActionCommand(CMD_UPDATE);
        upd.addActionListener(this);
        upd.setEnabled(getUpdateState(false)); // first time through, no optz are sel'd
        add(upd,gbc);

        Button b = new Button("Run "+s+" without Updates");
        b.setActionCommand(CMD_RUN);
        b.setEnabled(getRunState());
        b.addActionListener(this);
        gbc.gridx   = 1;
        add(b,gbc);

        b = new Button("Exit");
        b.setActionCommand(CMD_EXIT);
        b.addActionListener(this);
        gbc.gridx   = 2;
        add(b,gbc);
    }

    //
    // Data Methods:
    //

    //
    // Utility methods:
    //
    private boolean getUpdateState(boolean anyOptional) {
        // if there are any downloads available and selected, allow run (with updates)
        int ttl = DynamicUpdateUtils.countDownloadSize(cfg,TYPE_CRITICAL,null)
                + DynamicUpdateUtils.countDownloadSize(cfg,TYPE_CORE,null)
                + DynamicUpdateUtils.countDownloadSize(cfg,TYPE_ALL,selOps.getSelectedIDs());
        return anyOptional || ttl > 0;
    }

    private boolean getRunState() {
        // if there are no critical operations, allow run(no updates)
        return DynamicUpdateUtils.countDownloadSize(cfg,TYPE_CRITICAL,null) == 0;
    }

    private void runApplication() {
        if (d!=null) {
            d.setVisible(false);
            d.dispose();
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
            errTot  = DynamicUpdateUtils.performAllOperations(cfg, TYPE_CRITICAL, null, d);
System.out.println("dub.r: error total (crit) was: "+errTot);
            errTot += DynamicUpdateUtils.performAllOperations(cfg, TYPE_CORE, null, d);
System.out.println("dub.r: error total (+req) was: "+errTot);
            // do optional:
            // do "ALL" types because deletes are always optional.
            errTot += DynamicUpdateUtils.performAllOperations(cfg, TYPE_ALL, selOps.getSelectedIDs(), d);
System.out.println("dub.r: error total (+opt) was: "+errTot);
       
            // need to check errTot here to see if we should proceed...
            runApplication();
        } catch (InterruptedException ex) {
System.out.println("dub.r: caught InterruptedException... @"+System.currentTimeMillis());
            d.setVisible(false);
            d.dispose();
            
            // obtain correct info for next download attempt:
            cfg.reloadINI();
            cfg.reloadOperations();
        } // endtry
    }

    //
    // Implementation of the DUSelectedOps.Listener interface:
    //
    public void selectedIDsChanged(boolean anySelected) {
        upd.setEnabled(getUpdateState(anySelected));
    }

    //
    // Implementation of the ActionListener interface:
    //
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if (CMD_EXIT.equals(ac)) {
            System.exit(0);
        } else if (CMD_RUN.equals(ac)) {
            runApplication();
        } else if (CMD_UPDATE.equals(ac)) {
            // do required/critical:
            Frame f = DynamicUpdateUtils.getMainFrame();
            
            int ttlSize = DynamicUpdateUtils.countDownloadSize(cfg,TYPE_CRITICAL,null);
            ttlSize    += DynamicUpdateUtils.countDownloadSize(cfg,TYPE_CORE,null);
            ttlSize    += DynamicUpdateUtils.countDownloadSize(cfg,TYPE_ALL,selOps.getSelectedIDs());
            d = new DUProgressDialog(f, ttlSize);
            d.setLocationRelativeTo(f);

            Thread t = new Thread(this);
//            t.setDaemon(true); // would this work?
            t.setName("Operations thread");
            t.start();
            d.setVisible(true);
        } // endif
    }
}
