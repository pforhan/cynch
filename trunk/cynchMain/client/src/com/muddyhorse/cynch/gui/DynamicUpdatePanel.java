package com.muddyhorse.cynch.gui;

import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.muddyhorse.cynch.DUConfig;
import com.muddyhorse.cynch.DUConstants;
import com.muddyhorse.cynch.DUOperation;
import com.muddyhorse.cynch.DUSelectedOps;
import com.muddyhorse.cynch.DynamicUpdateUtils;
import com.muddyhorse.cynch.DUSelectedOps.Listener;

public class DynamicUpdatePanel extends java.awt.Panel
                             implements //common.du.DUProgressListener,
                                        com.muddyhorse.cynch.DUSelectedOps.Listener,
                                        com.muddyhorse.cynch.DUConstants
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    //
    // Class variables:
    //
    static NumberFormat snf   = new DecimalFormat("######K");
    //
    // Instance Variables:
    //
    private DUSelectedOps selOps;
    private TextField     txtTtl;
    private DUConfig      config;
    private ScrollPane    spn;
    private Panel         opnl;

    //
    // Constructors:
    //
    public DynamicUpdatePanel(DUConfig cfg) {
        config = cfg;
        selOps = new DUSelectedOps(cfg);
        selOps.addListener(this);

        buildGUI(cfg);
        // DynamicUpdateUtils.writeHashtable(coreFile, remoteCore);
    }

    //
    // Data methods:
    //
    public DUSelectedOps getSelectedOperations() {
        return selOps;
    }

    //
    // View methods:
    //
    private void buildGUI(DUConfig cfg) {
        setLayout(new GridBagLayout());
        setBackground(java.awt.Color.lightGray);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,0,0,0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        Label lbl = new Label(cfg.getAppDescription());
        add(lbl, gbc);

        lbl = new Label("Updatable Components:");
        lbl.setFont(new Font("Dialog", Font.BOLD, 16));
        gbc.gridy = 5;
        add(lbl, gbc);

        opnl = getOpsGUI(cfg, selOps);

        spn = new ScrollPane();
        spn.getHAdjustable().setUnitIncrement(10);
        spn.getVAdjustable().setUnitIncrement(21);
        spn.add(opnl);
        gbc.gridy = 10;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.insets.left  = 32;
        gbc.insets.right = 10;
        add(spn, gbc);

        // add text description:
        gbc.insets.top   = 0;
        gbc.insets.bottom= 0;
        gbc.insets.left  = 0;
        gbc.insets.right = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        String allDesc = cfg.getUpdateDescription().replace('+',',');
        int offs = -1;
        int last = 0;

        for (gbc.gridy=40;
             gbc.gridy<50;
             ++gbc.gridy) {
            offs = allDesc.indexOf("\\",last);
            if (offs == -1) {
                break;
            }
            Label desc = new Label(allDesc.substring(last,offs));
            add(desc,gbc);
            last = offs+1;
        } // endfor

        // build button panel:
        gbc.gridy   = 50;
        Panel pnl = new Panel(new GridBagLayout());
        gbc.insets.top    = 10;
        gbc.insets.bottom = 6;
        add(pnl,gbc);

        gbc.insets.top   = 5;
        gbc.insets.bottom= 5;
        gbc.insets.left  = 5;
        gbc.insets.right = 5;
        gbc.gridy     = 0;
        Label lblTtl = new Label("Total amount to download:",Label.RIGHT);
        gbc.gridx = 1;
        pnl.add(lblTtl,gbc);
        txtTtl = new TextField("0K",8);
        txtTtl.setEditable(false);
        gbc.gridx = 2;
        pnl.add(txtTtl,gbc);
        refreshTotal();
    }

    private static Panel getOpsGUI(DUConfig cfg, ItemListener l) {
        Panel        pnlGUI= new Panel();
        String[]     names = new String[] {"Operation","Type","File","Description","Size"};
        Font         f     = new Font("Dialog", Font.PLAIN, 10);

        pnlGUI.setBackground(Color.white);
        pnlGUI.setLayout(new GridBagLayout());
        pnlGUI.setFont(f);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,0,0,0);

        // place column headers:
        f = new Font("Dialog", Font.BOLD, 11);
        buildOpsRow(names, f, gbc, pnlGUI, null, TYPE_OPTIONAL);
        gbc.gridy++;

        // get table(s):
        buildOpsTable(cfg.getOperations(),gbc,pnlGUI,l);

        gbc.gridy++;
        gbc.weighty = 1.0;
        pnlGUI.add(new Canvas(), gbc);

        return pnlGUI;
    }

    private static void buildOpsTable(Hashtable opsTable, GridBagConstraints gbc, Panel pnl, ItemListener l) {
/*
        Vector[] opSets  = DynamicUpdateUtils.sortOperationsByOp(opsTable, null);
        buildOpsTable(opSets[OP_UPDATE  ],gbc,pnl,l);
        buildOpsTable(opSets[OP_DOWNLOAD],gbc,pnl,l);
        buildOpsTable(opSets[OP_DELETE  ],gbc,pnl,l);
        buildOpsTable(opSets[OP_NOTHING ],gbc,pnl,l);
//*/
        Vector[] opSets  = DynamicUpdateUtils.sortOperationsByType(opsTable, null);
        buildOpsTable(opSets[TYPE_CRITICAL],gbc,pnl,l);
        buildOpsTable(opSets[TYPE_CORE    ],gbc,pnl,l);
        buildOpsTable(opSets[TYPE_OPTIONAL],gbc,pnl,l);
    }

    private static void buildOpsTable(Vector ops, GridBagConstraints gbc, Panel pnl, ItemListener l) {
        NumberFormat vnf   = new DecimalFormat("#0.00");
        String[]     names = new String[5];
        for (Enumeration e=ops.elements(); e.hasMoreElements(); ) {
            DUOperation op = (DUOperation)e.nextElement();
            boolean useCheck = true;

            String locVer = (op.localVersion==null)  ? "" : vnf.format(op.localVersion);
            String rmtVer = (op.remoteVersion==null) ? "" : vnf.format(op.remoteVersion);

            switch (op.operation) {
              case DUConstants.OP_NOTHING:
                if (op.type != TYPE_OPTIONAL) {
                    // only display optional nothings
                    continue;
                } // endif
                useCheck = false;
                names[0] = OP_DESCRIPTIONS[op.operation] + " (v"+locVer+")";
              break;
              case DUConstants.OP_DELETE:
                names[0] = OP_DESCRIPTIONS[op.operation] +" v"+locVer;
              break;
              case DUConstants.OP_UPDATE:
                names[0] = OP_DESCRIPTIONS[op.operation] +" from v"+locVer+" to v"+rmtVer;
              break;
              case DUConstants.OP_DOWNLOAD:
                names[0] = OP_DESCRIPTIONS[op.operation] +" v"+rmtVer;
              break;
              default:
                // do what here?
            } // endswitch

            switch (op.type) {
              case TYPE_CRITICAL:
                names[1] = "Critical";
              break;
              case TYPE_OPTIONAL:
                names[1] = "Optional";
              break;
              case TYPE_CORE:
                names[1] = "Required";
              break;
            } // endswitch
            names[2] = op.fileID;
            names[3] = (op.remoteDescription == null) ?
                            op.localDescription : op.remoteDescription;
            names[4] = (op.remoteSize == null)  ?
                            snf.format(op.localSize.intValue()/1024) : snf.format(op.remoteSize.intValue()/1024);

            buildOpsRow(names, null, gbc, pnl, (useCheck)?l:null, op.type);

            gbc.gridy++;
        } // endfor

    }

    private static void buildOpsRow(String[] names, Font f, GridBagConstraints gbc, Panel pnl, ItemListener l, int type) {
        //! eventually: first col, always checkbox
        //  if nothing, skip chk
        //  if modifyable, span = 2 and add text
        //  if not modify, chk in col 1 (disabled) and text (lbl) in col 2
        // why? removes dependency on chk width, allows normal colors for text.
        Checkbox check = null;
        Label lblOp = null;

        if (l!=null) { // indicates whether to use checkbox...
            gbc.gridx = 0;
            check = new Checkbox();
            check.setState(type != TYPE_OPTIONAL);
            check.setFont(f);
            if (type != TYPE_OPTIONAL) {
                lblOp = new Label(names[0]);
                lblOp.setFont(f);
                check.setEnabled(false);
                pnl.add(check,gbc);
                gbc.gridx++;
                pnl.add(lblOp,gbc);
            } else {
                // not core, need to add listener:
                check.setLabel(names[0]);
                check.addItemListener(l);
                check.setName(names[2]);
                gbc.gridwidth = 2;
                pnl.add(check,gbc);
                gbc.gridwidth = 1;
                gbc.gridx++;
            } // endif
        } else {
            // a nothing column
            lblOp = new Label(names[0]);
            lblOp.setFont(f);
//            gbc.gridwidth = 2;
            gbc.gridx = 1;
            pnl.add(lblOp,gbc);
//            gbc.gridwidth = 1;
//            gbc.gridx++;
        } // endif

        Label lblType = new Label(names[1]);
        lblType.setFont(f);

        if (type != TYPE_OPTIONAL) {
//            compOp.setBackground(Color.black);
            lblType.setBackground(Color.lightGray);
        } // endif

        gbc.gridx++;
        pnl.add(lblType,gbc);

/*
        Label lblItem = new Label(names[2]);
        lblItem.setFont(f);
        gbc.gridx++;
        pnl.add(lblItem,gbc);
//*/

        Label lblDesc = new Label(names[3]);
        lblDesc.setFont(f);
        gbc.gridx++;
        pnl.add(lblDesc,gbc);

        Label lblSize = new Label(names[4]);
        lblSize.setFont(f);
        gbc.gridx++;
        pnl.add(lblSize,gbc);

        gbc.gridx++;
        gbc.weightx = 1.0;
        pnl.add(new Canvas(), gbc);
        gbc.weightx = 0.0;
    }

/* no easy way to use this right now...
    public void rebuildOpsTable(DUConfig cfg) {
        spn.remove(opnl);
        opnl = getOpsGUI(cfg, selOps);
        spn.add(opnl);
    }
//*/

    //
    // Utility methods:
    //
    public void refreshTotal() {
        int ttl = DynamicUpdateUtils.countDownloadSize(config,TYPE_CRITICAL, null)
                + DynamicUpdateUtils.countDownloadSize(config,TYPE_CORE,null)
                + DynamicUpdateUtils.countDownloadSize(config,TYPE_ALL,selOps.getSelectedIDs());
        txtTtl.setText(snf.format(ttl/1024));
    }
/*
    //
    // Implementation of the DUProgressListener interface:
    //
    public void progress(String name, String desc, int amount, int total) {
System.out.println("dup.p: n:"+name+"; d:"+desc+"; a="+amount+"; t="+total);
    }
//*/

    //
    // Implementation of the DUSelectedOps.Listener interface:
    //
    public void selectedIDsChanged(boolean anySelected) {
        refreshTotal();
    }
}
